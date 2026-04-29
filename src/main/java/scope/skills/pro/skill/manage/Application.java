package scope.skills.pro.skill.manage;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * 类描述
 */
@SpringBootApplication
@Log4j2
@ComponentScan(basePackages = "scope.skills.pro.skill.**.**")
public class Application {
    //    private static DatabaseSession databaseSession = new DatabaseSession();

    private static final String HISTORY_DIR = "chat_history";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static String apiKey = "sk-f5bb24c138ea4b7996e67c215d729c20";

    public static void main(String[] args) {
        // 启动 Spring 上下文以读取配置
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);

//        // 从 Spring 环境中读取 API_KEY
//        apiKey = context.getEnvironment().getProperty("qianwen.API_KEY");
//        DataSource dataSource = context.getBean(DataSource.class);
//        DatabaseSession databaseSession = new DatabaseSession(dataSource);
//        if (apiKey == null || apiKey.isEmpty()) {
//            System.err.println("错误: 未在配置文件中找到 qianwen.API_KEY");
//            System.exit(1);
//        }
////
////        // 初始化 SessionManager
////        sessionManager = SessionManager.getInstance(apiKey);
////
////        // 创建历史记录目录
////        createHistoryDirectory();
////
//        System.out.println("输入你的问题");
//        Scanner scanner = new Scanner(System.in);
////        String userId = "user_1234";
////
////        while (true) {
////            System.out.println("\n========== 菜单 =========="
////                    + "\n1. 继续上次的对话"
////                    + "\n2. 开启新对话"
////                    + "\n3. 查看历史对话列表"
////                    + "\n4. 删除历史记录"
////                    + "\n5. 退出"
////                    + "\n=========================="
////                    + "\n请选择操作: ");
////
////            String choice = scanner.nextLine();
////
////            switch (choice) {
////                case "1":
////                    // 继续上次的对话
////                    continueChat(userId, scanner);
////                    break;
////                case "2":
////                    // 开启新对话
////                    startNewChat(userId, scanner);
////                    break;
////                case "3":
////                    // 查看历史对话列表
////                    listChatHistory(userId);
////                    break;
////                case "4":
////                    // 删除历史记录
////                    deleteChatHistory(userId, scanner);
////                    break;
////                case "5":
////                    System.out.println("再见！");
////                    context.close();
////                    scanner.close();
////                    return;
////                default:
////                    System.out.println("无效的选择，请重新输入\n");
////            }
////        }
//
//        // 官方文档保存示例
//        String s = scanner.nextLine();
//        // 1. 创建组件
//        InMemoryMemory memory = new InMemoryMemory();
//        ReActAgent agent = ReActAgent.builder()
//                .name("Assistant")
//                .model(DashScopeChatModel.builder()
//                        .apiKey(apiKey)
//                        .modelName("qwen3-max")
//                        .stream(true)
//                        .enableThinking(true)
//                        .build())
//                .memory(memory)
//                .build();
//
//// 2. 创建 Session 并加载已有会话
//        Path sessionPath = Path.of(System.getProperty("user.home"), ".agentscope", "sessions");
//        Session session = new JsonSession(sessionPath);
//        agent.loadIfExists(session, "userId");
//        scanner.close();
//
//// 3. 使用 Agent
////        Msg response = agent.call().block();
//        Mono<Msg> mono = agent.call(Msg.builder()
//                .textContent(s)
//                .build());
//        Msg response = mono.block();
//        System.out.println("保存地址为" + sessionPath);
//        if (response != null && response.getTextContent() != null) {
//            System.out.println(response.getTextContent());
//        } else {
//            System.out.println("没有返回结果");
//        }
//
//// 4. 保存会话
//        agent.saveTo(session, "userId");
    }


}
