package scope.skills.pro.skill.manage.agentInfo.runtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.Event;
import io.agentscope.core.message.Msg;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.runtime.adapters.AgentHandler;
import io.agentscope.runtime.adapters.agentscope.AgentScopeAgentHandler;
import io.agentscope.runtime.engine.agents.agentscope.tools.ToolkitInit;
import io.agentscope.runtime.engine.schemas.AgentRequest;
import io.agentscope.runtime.sandbox.box.BaseSandbox;
import io.agentscope.runtime.sandbox.manager.ManagerConfig;
import io.agentscope.runtime.sandbox.manager.SandboxService;
import io.agentscope.runtime.sandbox.manager.client.container.docker.DockerClientStarter;
import io.micrometer.common.lang.NonNull;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import scope.skills.pro.skill.manage.agentInfo.AgentSkillsInfo.AgentSkills;

import java.util.Set;

@Log4j2
@Component
public class MyFridayAgentHandler extends AgentScopeAgentHandler implements AgentHandler {

    @Value("${sandbox.base-url:http://localhost:8877}")
    private String sandboxBaseUrl;
    
    private SandboxService sandboxService;

    /**
     * 初始化 SandboxService（应用启动时调用一次）
     */
    @jakarta.annotation.PostConstruct
    public void init() {
        if (sandboxService == null) {
            log.info("🔧 正在初始化 SandboxService，baseUrl: {}", sandboxBaseUrl);
            DockerClientStarter dockerClientStarter = DockerClientStarter.builder()
                    .host("localhost")
                    .port(2375)
                    .certPath("")
                    .build();
            ManagerConfig build = ManagerConfig.builder()
                    .clientStarter(dockerClientStarter)
//                    .baseUrl(sandboxBaseUrl)
                    .build();
            sandboxService = new SandboxService(build);
//            sandboxService = new SandboxService(ManagerConfig.builder()
//                    .sandboxMap(new InMemorySandboxMap())
//                    .baseUrl(sandboxBaseUrl)
//                    .build());
            // 关键：必须调用 start() 来初始化 containerClient
            sandboxService.start();

            log.info("✅ SandboxService 初始化并启动成功");
            log.info("✅ SandboxService 健康状态: {}", isHealthy());
        }
    }
    
    @Override
    public boolean isHealthy() {
        return sandboxService != null;
    }

    @Override
    public String getName() {
        return "MyFridayAgent";
    }

    @Override
    public String getDescription() {
        return "A custom agent handler for Friday tasks";
    }

    @Override
    @NonNull
    public Flux<Event> streamQuery(AgentRequest request, Object messages) {
        ReActAgent agent = createAgentWithSandbox(request);
        log.info("Available tools: {}", agent.getToolkit().getToolNames());
        return agent.stream(Msg.builder()
                .textContent((String) messages)
                .build());
    }

    /**
     * 创建带有 Sandbox 的 ReActAgent
     *
     * @param request AgentRequest
     * @return ReActAgent
     */
    public ReActAgent createAgentWithSandbox(AgentRequest request) {
        // 确保 sandboxService 已初始化（如果 @PostConstruct 未执行）
        if (sandboxService == null) {
            log.warn("⚠️ SandboxService 未初始化，现在开始初始化...");
            init();
        }
        
        if (!isHealthy()) {
            throw new IllegalStateException("SandboxService 不健康。请检查沙箱服务是否运行在: " + sandboxBaseUrl);
        }
        
        String userId = request.getUserId() != null ? request.getUserId() : "default_user";
        String sessionId = request.getSessionId() != null ? request.getSessionId() : "default_session";
        
        log.info("📦 为用户创建沙箱: {}, 会话: {}, 运行时: python", userId, sessionId);
        log.info("🌐 沙箱服务地址: {}", sandboxBaseUrl);
        
        BaseSandbox sandbox = new BaseSandbox(sandboxService, userId, sessionId);
        sandbox.runIpythonCell("print(1+1)");
        try {
            log.info("🚀 尝试创建沙箱容器...");
            sandboxService.createContainer(sandbox);
            log.info("✅ 沙箱容器创建成功，用户: {}", userId);
        } catch (JsonProcessingException e) {
            log.error("❌ 创建沙箱容器时出错: {}", e.getMessage(), e);
            log.error("💡 提示: 请确保 Docker 正在运行，并且沙箱服务可访问: {}", sandboxBaseUrl);
            throw new RuntimeException("创建沙箱容器失败。请检查 Docker 和沙箱服务是否正在运行。", e);
        }
        
        Toolkit toolkit = new Toolkit();
        log.info("🔧 正在注册沙箱工具...");
        toolkit.registerTool(ToolkitInit.RunPythonCodeTool(sandbox));
        toolkit.registerTool(ToolkitInit.RunShellCommandTool(sandbox));
        
        log.info("✅ 已注册沙箱工具: {}", toolkit.getToolNames());
        log.info("📋 沙箱信息: {}", sandbox);
        
        // 创建 Agent
        ReActAgent agent = AgentSkills.createAgentForUser(toolkit);
        
        // 关键验证：确认沙箱工具仍然存在于最终的 agent 中
        Set<String> finalTools = agent.getToolkit().getToolNames();
        log.info("✅ 智能体创建成功，ID: {}", agent.getAgentId());
        log.info("✅ 最终智能体包含 {} 个工具: {}", finalTools.size(), finalTools);
        
        // 验证沙箱工具是否存在
        if (!finalTools.contains("python_code") && !finalTools.contains("run_ipython_cell")) {
            log.error("❌ 严重错误: 沙箱 Python 工具在智能体中丢失！");
            log.error("❌ 当前可用工具: {}", finalTools);
            throw new IllegalStateException("沙箱工具在智能体创建过程中丢失。这是一个 bug。");
        }
        
        log.info("✅ 沙箱工具验证通过: Python 执行工具可用");
        
        return agent;
    }
}
