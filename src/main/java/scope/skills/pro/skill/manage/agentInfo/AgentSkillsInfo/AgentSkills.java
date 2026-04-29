package scope.skills.pro.skill.manage.agentInfo.AgentSkillsInfo;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.Event;
import io.agentscope.core.memory.InMemoryMemory;
import io.agentscope.core.message.Msg;
import io.agentscope.core.message.ToolResultBlock;
import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.session.Session;
import io.agentscope.core.skill.AgentSkill;
import io.agentscope.core.skill.SkillBox;
import io.agentscope.core.skill.repository.ClasspathSkillRepository;
import io.agentscope.core.state.SessionKey;
import io.agentscope.core.state.SimpleSessionKey;
import io.agentscope.core.tool.AgentTool;
import io.agentscope.core.tool.ToolCallParam;
import io.agentscope.core.tool.Toolkit;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import scope.skills.pro.skill.manage.agentInfo.entity.ProductInfo;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * 类描述
 */

@Log4j2
public class AgentSkills {

    private static String apiKey = "sk-f5bb24c138ea4b7996e67c215d729c20";


    public static DashScopeChatModel createDashScopeChatModel() {
        return DashScopeChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen3-max")
                .stream(true)
                .enableThinking(true)
                .build();
    }

    /**
     * 创建有记忆的一个技能智能体
     *
     * @param memory 内存
     * @param toolkit 工具集
     * @return 技能智能体
     */
    public static ReActAgent createAgentForUser(InMemoryMemory memory, Toolkit toolkit) {
        SkillBox skillBox = new SkillBox(toolkit);

        try {
            ClasspathSkillRepository repository = new ClasspathSkillRepository("skills");
            List<AgentSkill> allSkills = repository.getAllSkills();
            allSkills.forEach(AgentSkill -> {
                AgentTool loadDataTool = new AgentTool() {
                    @Override
                    public String getName() {
                        return AgentSkill.getName() + "tool";
                    }

                    @Override
                    public String getDescription() {
                        return AgentSkill.getDescription();
                    }

                    @Override
                    public Map<String, Object> getParameters() {
                        return Map.of();
                    }

                    @Override
                    public Mono<ToolResultBlock> callAsync(ToolCallParam param) {
                        return null;
                    }
                };
                skillBox.registration()
                        .skill(AgentSkill)
                        .tool(loadDataTool)
                        .apply();
//                skillBox.registerSkill(AgentSkill);
            });
            System.out.println("Loaded skills: " + allSkills);
        } catch (Exception e) {
            log.error("Error loading skills", e);
            throw new RuntimeException("Error loading skills", e);
        }

        // ... 初始化技能 ...
        return ReActAgent.builder()
                .skillBox(skillBox)
                .name("Assistant")
                .hook(new AgentHook().createLogHook())
                // 提示词
                .sysPrompt("You are a helpful assistant.")
                .model(AgentSkills.createDashScopeChatModel())
                .memory(memory)
                .build();
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
        return createAgentForUser(new InMemoryMemory(),new Toolkit());
    }


    public void talk(ReActAgent agent) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("请输入你的问题：");
            String choice = scanner.nextLine();
            if (choice.equals("exit")) break;
            Msg buildMsg = Msg.builder()
                    .textContent(choice)
                    .build();

            Flux<Event> stream = agent.stream(buildMsg);
//            Event event = stream.blockFirst();
            stream.buffer().subscribe(events -> {
                for (Event event : events) {
//                    if (event.getType() == EventType.)
//                        System.out.println(event.getMessage().getTextContent());

                }
            });
//            stream.buffer().subscribe();

            // 3. 使用 Agent  Mono响应
//            Mono<Msg> mono = agent.call(buildMsg);
//            Msg response = mono.block();
//            if (response != null && response.getTextContent() != null) {
//                System.out.println(response.getTextContent());
//            } else {
//                System.out.println("没有返回结果");
//            }
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
//        String userId = "user_1234";
//        SessionKey key = SimpleSessionKey.of(userId);
//        Session session = new JsonSession(Path.of("D:\\workspace\\java_skills_pro\\chat_history", ".agentscope", "sessions"));
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
