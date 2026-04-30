package scope.skills.pro.skill.manage.agentInfo.AgentSkillsInfo;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.Event;
import io.agentscope.core.memory.InMemoryMemory;
import io.agentscope.core.message.Msg;
import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.session.Session;
import io.agentscope.core.skill.AgentSkill;
import io.agentscope.core.skill.SkillBox;
import io.agentscope.core.skill.repository.ClasspathSkillRepository;
import io.agentscope.core.state.SessionKey;
import io.agentscope.core.state.SimpleSessionKey;
import io.agentscope.core.tool.Toolkit;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import scope.skills.pro.skill.manage.agentInfo.entity.ProductInfo;

import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * 智能体技能管理类
 */

@Log4j2
public class AgentSkills {

    private static String deepseekApiKey = "sk-8219d87f683445dc8cbc697b9a2fd6c8";
    private static String qianwen = "sk-44f92601a27e4692802d7db2eb9eb574";


    /**
     * 创建一个 DashScope 模型
     *
     * @return 模型
     */
    public static DashScopeChatModel createDashScopeChatModel() {
        return DashScopeChatModel.builder()
                .apiKey(qianwen)
                .modelName("qwen3-max")
                .stream(true)
                .enableThinking(true)
                .build();
    }

    /**
     * 创建有记忆的技能智能体
     *
     * @param memory  内存
     * @param toolkit 工具集（包含沙箱工具等）
     * @return 技能智能体
     */
    public static ReActAgent createAgentForUser(InMemoryMemory memory, Toolkit toolkit) {
        log.info("正在创建智能体，工具集包含 {} 个工具: {}",
                toolkit.getToolNames().size(), toolkit.getToolNames());
        // 使用传入的 toolkit 创建 SkillBox
        SkillBox skillBox = new SkillBox(toolkit);
        try {
            ClasspathSkillRepository repository = new ClasspathSkillRepository("skills");
            List<AgentSkill> allSkills = repository.getAllSkills();
            // 记录注册前的工具列表
            log.info("技能注册前的工具列表: {}", toolkit.getToolNames());
            // 正确的方式：直接注册 skill，让 SkillBox 自动处理
            allSkills.forEach(AgentSkill -> {
                log.info("正在注册技能: {} - {}", AgentSkill.getName(), AgentSkill.getDescription().substring(0, Math.min(50, AgentSkill.getDescription().length())));
                skillBox.registration()
                        .skill(AgentSkill)
                        .apply();  // 只需要注册 skill，不需要手动创建 tool
            });
            // 记录注册后的工具列表
            log.info("已加载 {} 个技能", allSkills.size());
            log.info("技能注册后的工具列表: {}", toolkit.getToolNames());
        } catch (Exception e) {
            log.error("加载技能时出错", e);
            throw new RuntimeException("加载技能失败", e);
        }
        // 创建智能体
        ReActAgent agent = ReActAgent.builder()
                // 最大迭代次数
                .maxIters(10)
                .skillBox(skillBox)
                .toolkit(toolkit)
                .name("智能助手")
                .hook(new AgentHook().createLogHook())
                // 系统提示词 - 明确说明可以使用 Python 代码执行工具和技能
                .sysPrompt("你是一个有用的智能助手。你可以访问以下工具：\n" +
                        "1. Python 代码执行工具（run_python_code、run_shell_command）- 在沙箱环境中执行代码\n" +
                        "2. 各种技能工具（docx、pdf、database 等）- 用于文档处理、数据库查询等任务\n" +
                        "当被要求执行计算、数据查询、文档处理或任何编程任务时，请务必使用相应的工具来执行，而不是仅仅描述你要做什么。")
                .model(AgentSkills.createDashScopeChatModel())
                .memory(memory)
                .build();
        // 验证最终的工具列表
        Set<String> finalTools = agent.getToolkit().getToolNames();
        log.info("最终智能体工具集包含 {} 个工具: {}", finalTools.size(), finalTools);
        // 检查是否包含沙箱工具
        boolean hasSandboxTools = finalTools.stream()
                .anyMatch(name -> name.contains("python") || name.contains("ipython") || name.contains("shell"));
        if (!hasSandboxTools) {
            log.warn("⚠️ 警告: 最终工具集中未检测到沙箱工具！");
        } else {
            log.info("✅ 沙箱工具已正确注册");
        }

        return agent;
    }

    /**
     * 有记忆的默认技能智能体
     *
     * @param memory 内存
     * @return 技能智能体
     */
    public static ReActAgent createAgentForUser(InMemoryMemory memory) {
        return createAgentForUser(memory, new Toolkit());
    }


    /**
     * 创建一个默认技能智能体
     *
     * @return 技能智能体
     */
    public static ReActAgent createAgentForUser() {
        return createAgentForUser(new InMemoryMemory(), new Toolkit());
    }

    /**
     * 创建一个无记忆技能智能体
     *
     * @return 技能智能体
     */
    public static ReActAgent createAgentForUser(Toolkit toolkit) {
        return createAgentForUser(new InMemoryMemory(), toolkit);
    }


    public void talk(ReActAgent agent) {
        try {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                System.out.println("请输入你的问题：");
                String choice = scanner.nextLine();
                if (choice.equals("exit")) break;
                Msg buildMsg = Msg.builder()
                        .textContent(choice)
                        .build();
                Flux<Event> stream = agent.stream(buildMsg);
                stream.buffer().subscribe(events -> {
                    for (Event event : events) {
                        // 处理事件
                    }
                });
            }
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Arrearage")) {
                log.error("API Key 无效：账户欠费，请前往阿里云控制台充值");
            } else {
                log.error("API Key 验证失败: {}", e.getMessage());
            }
        }

    }

    public void sessionStatus(Session session) {

// 检查会话是否存在
        boolean exists = session.exists(SimpleSessionKey.of("sessionId"));

// 删除会话
        session.delete(SimpleSessionKey.of("sessionId"));

// 列出所有会话
        Set<SessionKey> keys = session.listSessionKeys();
    }

    // 结构化输出
    public static void main(String[] args) {
        InMemoryMemory inMemoryMemory = new InMemoryMemory();
        ReActAgent agent = createAgentForUser(inMemoryMemory);
        // 发送查询，指定输出类型
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入你的问题：");
        String choice = scanner.nextLine();
        // 3. 使用 Agent
        Mono<Msg> mono = agent.call(Msg.builder()
                .textContent(choice)
                .build());
        Msg response = mono.block();
        if (response != null && response.getTextContent() != null) {
            System.out.println(response.getTextContent());
        } else {
            System.out.println("没有返回结果");
        }
        try {
            // 提取类型化数据
            ProductInfo data = response.getStructuredData(ProductInfo.class);
            System.out.println("产品: " + data.name);
            System.out.println("价格: $" + data.price);
            System.out.println(response);
            // 业务验证
            if (data.price < 0) {
                throw new IllegalArgumentException("价格无效");
            }
        } catch (Exception e) {
            System.out.println("没有返回结果");
        }
    }


}
