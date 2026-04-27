package scope.skills.pro.skill.manage.agentInfo.AgentSkillsInfo;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.memory.InMemoryMemory;
import io.agentscope.core.message.Msg;
import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.session.JsonSession;
import io.agentscope.core.session.Session;
import io.agentscope.core.skill.AgentSkill;
import io.agentscope.core.skill.SkillBox;
import io.agentscope.core.skill.repository.ClasspathSkillRepository;
import io.agentscope.core.state.SessionKey;
import io.agentscope.core.state.SimpleSessionKey;
import io.agentscope.core.tool.Toolkit;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;
import scope.skills.pro.skill.manage.agentInfo.entity.ProductInfo;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * 类描述
 */

@Log4j2
public class AgentSkills {

    private static String apiKey = "sk-f5bb24c138ea4b7996e67c215d729c20";


    public static DashScopeChatModel createDashScopeChatModel(){
        return DashScopeChatModel.builder()
                .apiKey(apiKey)
                .modelName("qwen3-max")
                .stream(true)
                .enableThinking(true)
                .build();
    }

    public static ReActAgent createAgentForUser(InMemoryMemory memory) {
        // 复用你现有的 Agent 创建逻辑
        Toolkit toolkit = new Toolkit();
        SkillBox skillBox = new SkillBox(toolkit);
        try {
            ClasspathSkillRepository repository = new ClasspathSkillRepository("skills");
            List<AgentSkill> allSkills = repository.getAllSkills();
            allSkills.forEach(AgentSkill -> skillBox.registerSkill(AgentSkill));
            System.out.println("Loaded skills: " + allSkills);
        } catch (Exception e) {
            log.error("Error loading skills", e);
            throw new RuntimeException("Error loading skills", e);
        }
        // ... 初始化技能 ...
        return ReActAgent.builder()
                .skillBox(skillBox)
                .name("Assistant")
                // 提示词
                .sysPrompt("You are a helpful assistant.")
                .model(AgentSkills.createDashScopeChatModel())
                .memory(memory)
                .build();
    }


    public void talk(ReActAgent agent) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("请输入你的问题：");
            String choice = scanner.nextLine();
            if (choice.equals("exit")) break;
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
        }catch (Exception e){
            System.out.println("没有返回结果");
        }
    }
}
