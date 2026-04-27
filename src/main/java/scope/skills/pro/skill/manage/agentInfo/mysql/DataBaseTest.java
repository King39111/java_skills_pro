package scope.skills.pro.skill.manage.agentInfo.mysql;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.memory.InMemoryMemory;
import io.agentscope.core.message.Msg;
import io.agentscope.core.state.SessionKey;
import io.agentscope.core.state.SimpleSessionKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import scope.skills.pro.skill.manage.agentInfo.AgentSkillsInfo.AgentSkills;
import scope.skills.pro.skill.manage.agentInfo.localPath.LocalPathTest;

import javax.sql.DataSource;
import java.util.Scanner;

/**
 * 类描述
 */

@Slf4j
public class DataBaseTest {

    private static String apiKey = "sk-f5bb24c138ea4b7996e67c215d729c20";

    public static void main(String[] args) {
        // 启动 Spring 上下文
        ConfigurableApplicationContext context = SpringApplication.run(LocalPathTest.class, args);

        // 从 Spring 容器获取 DataSource
        DataSource dataSource = context.getBean(DataSource.class);
        apiKey = context.getEnvironment().getProperty("qianwen.API_KEY");
        String userId = "user_mobai";
        databaseStart(userId, dataSource);
    }

    private static void databaseStart(String userId, DataSource dataSource) {
        // 创建 SessionKey
        SessionKey key = SimpleSessionKey.of(userId);
        // 获取 session ID
        String sessionId = ((SimpleSessionKey) key).sessionId();
        DatabaseSession databaseSession = new DatabaseSession(dataSource);
        Scanner scanner = new Scanner(System.in);
        //  1. 创建组件
        InMemoryMemory memory = new InMemoryMemory();
        AgentSkills agentSkills = new AgentSkills();
        // 2. 创建 Session 并加载已有会话
        ReActAgent agent = agentSkills.createAgentForUser(memory);
        try {
            agent.loadFrom(databaseSession, userId);
        } catch (Exception e) {
            log.error("Error:", e);
        }
        agentSkills.talk(agent);
        // 4. 保存会话
        agent.saveTo(databaseSession, userId);
        databaseSession.save(key, sessionId, memory.getMessages());
        scanner.close();
        System.out.println("对话已结束");
        System.out.println("保存记录为" + databaseSession.getList(key, sessionId, Msg.class));

    }
}
