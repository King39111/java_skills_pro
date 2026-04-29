package scope.skills.pro.skill.manage.agentInfo.localPath;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.memory.InMemoryMemory;
import io.agentscope.core.session.JsonSession;
import io.agentscope.core.session.Session;
import io.agentscope.core.state.SessionKey;
import io.agentscope.core.state.SimpleSessionKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import scope.skills.pro.skill.manage.agentInfo.AgentSkillsInfo.AgentSkills;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * 类描述
 */
@SpringBootApplication
@Log4j2
public class LocalPathTest {

    private static String apiKey = "sk-f5bb24c138ea4b7996e67c215d729c20";

    public static void main(String[] args) {
        // 启动 Spring 上下文
        System.out.println("正在启动 Spring Boot 应用...");
        ConfigurableApplicationContext context = SpringApplication.run(LocalPathTest.class, args);

        start(null, null);
        context.close();
        System.out.println("程序执行完毕");
    }

    public static void start(DataSource dataSource, String apiKey) {

        Scanner scanner = new Scanner(System.in);
        String userId = "user_1234";
        // 创建 SessionKey
        SessionKey key = SimpleSessionKey.of(userId);
        Session session = new JsonSession(Path.of("D:\\workspace\\java_skills_pro\\chat_history", ".agentscope", "sessions"));
        // 1. 创建组件
        InMemoryMemory memory = new InMemoryMemory();
        AgentSkills agentSkills = new AgentSkills();
        // 2. 创建 Agent
        ReActAgent agent = agentSkills.createAgentForUser(memory);
        // 4. 加载已有会话
        agent.loadIfExists(session, key);
        // 5. 对话循环
        agentSkills.talk(agent);
        // 6. 保存会话
        agent.saveTo(session, key);
        scanner.close();
        System.out.println("对话已结束");
    }
}
