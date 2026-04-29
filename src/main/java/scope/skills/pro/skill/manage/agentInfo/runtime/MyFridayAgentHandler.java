package scope.skills.pro.skill.manage.agentInfo.runtime;


// ... 其他必要的import

import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.Event;
import io.agentscope.core.memory.InMemoryMemory;
import io.agentscope.core.message.Msg;
import io.agentscope.core.tool.Toolkit;
import io.agentscope.runtime.adapters.AgentHandler;
import io.agentscope.runtime.adapters.agentscope.AgentScopeAgentHandler;
import io.agentscope.runtime.engine.agents.agentscope.tools.ToolkitInit;
import io.agentscope.runtime.engine.schemas.AgentRequest;
import io.agentscope.runtime.sandbox.box.Sandbox;
import io.agentscope.runtime.sandbox.manager.ManagerConfig;
import io.agentscope.runtime.sandbox.manager.SandboxService;
import io.agentscope.runtime.sandbox.manager.utils.InMemorySandboxMap;
import io.micrometer.common.lang.NonNull;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import scope.skills.pro.skill.manage.agentInfo.AgentSkillsInfo.AgentSkills;

// 声明为Spring Bean
@Component
public class MyFridayAgentHandler extends AgentScopeAgentHandler implements AgentHandler {


    @Override
    public boolean isHealthy() {
        return false;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getDescription() {
        return "";
    }


    @Override
    @NonNull
    public Flux<Event> streamQuery(AgentRequest request, Object messages) {

        if (ObjectUtils.isEmpty(sandboxService)) {
            sandboxService = new SandboxService(ManagerConfig.builder().sandboxMap(new InMemorySandboxMap()).build());
        }
        // 1. 获取或创建与当前会话关联的沙箱
        Sandbox sandbox = null;
        if (sandboxService != null) {
            sandbox = new Sandbox(sandboxService, "user_1234", "user_1234", "python");
        }
        System.out.println("sandbox  information : " + sandbox);
        // 2. 构建工具集(Toolkit)，并将沙箱能力封装成工具
        Toolkit toolkit = new Toolkit();
        if (sandbox != null) {
            // 这是一个关键步骤：将沙箱实例转化为智能体可用的“Python代码执行工具”
            toolkit.registerTool(ToolkitInit.RunPythonCodeTool(sandbox));
            // 你可以在这里注册更多工具，例如文件操作、网络请求等
            // toolkit.registerTool(new MyCustomTool());
        }
        InMemoryMemory inMemoryMemory = new InMemoryMemory();
        ReActAgent agent = AgentSkills.createAgentForUser(inMemoryMemory,toolkit);
//        new AgentSkills().talk(agent);
        // 5. 调用智能体并返回流式响应
        // AgentScope的stream方法返回Flux<Event>，与Runtime的接口完美匹配
        return agent.stream(Msg.builder()
                .textContent((String) messages)
                .build());
    }
}
