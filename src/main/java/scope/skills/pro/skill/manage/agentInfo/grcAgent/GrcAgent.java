//package scope.skills.pro.skill.manage.agentInfo.grcAgent;
//
//import io.agentscope.core.ReActAgent;
//import io.agentscope.core.rag.RAGMode;
//import io.agentscope.core.rag.model.RetrieveConfig;
//import io.agentscope.core.tool.Toolkit;
//import scope.skills.pro.skill.manage.agentInfo.AgentSkillsInfo.AgentSkills;
//
///**
// * 类描述
// */
//
//public class GrcAgent {
//    private static String apiKey = "sk-f5bb24c138ea4b7996e67c215d729c20";
//
//    public ReActAgent createGrcAgent() {
//
//        ReActAgent agent = ReActAgent.builder()
//                .name("助手")
//                .sysPrompt("你是一个可以访问知识库的有用助手。" +
//                        // agentic模式
////                        "需要信息时使用retrieve_knowledge工具" +
//                        "")
//                .model(AgentSkills.createDashScopeChatModel())
//                .toolkit(new Toolkit())
//                .knowledge(null)
//                // 启用 Generic RAG 模式
//                .ragMode(RAGMode.GENERIC)
//                // agentic模式
////                .ragMode(RAGMode.AGENTIC)
//                .retrieveConfig(
//                        RetrieveConfig.builder()
//                                .limit(3)
//                                .scoreThreshold(0.3)
//                                .build())
//                .build();
//        return agent;
//    }
//}
