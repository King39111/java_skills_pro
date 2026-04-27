package scope.skills.pro.skill.manage.agentInfo.grcAgent;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.model.DashScopeChatModel;
import io.agentscope.core.rag.RAGMode;
import io.agentscope.core.rag.integration.dify.DifyKnowledge;
import io.agentscope.core.rag.integration.dify.DifyRAGConfig;
import io.agentscope.core.rag.integration.dify.RetrievalMode;
import io.agentscope.core.rag.integration.ragflow.RAGFlowConfig;
import io.agentscope.core.rag.integration.ragflow.RAGFlowKnowledge;
import io.agentscope.core.rag.model.Document;
import io.agentscope.core.rag.model.RetrieveConfig;
import io.agentscope.core.tool.Toolkit;
import scope.skills.pro.skill.manage.agentInfo.AgentSkillsInfo.AgentSkills;

import java.util.List;

/**
 * 类描述
 */

public class GrcAgent {
    private static String apiKey = "sk-f5bb24c138ea4b7996e67c215d729c20";

    public ReActAgent createGrcAgent(){

        // dify知识库
        DifyRAGConfig difyConfig = DifyRAGConfig.builder()
                .apiKey(System.getenv("DIFY_RAG_API_KEY"))
                .datasetId("your-dataset-id")
                .retrievalMode(RetrievalMode.HYBRID_SEARCH)
                .topK(10).scoreThreshold(0.5)
                .build();

        DifyKnowledge difyKnowledge = DifyKnowledge.builder().config(difyConfig).build();

        List<Document> difyResults = difyKnowledge.retrieve("查询内容",
                RetrieveConfig.builder().limit(5).build()).block();
        // ragflow知识库
        RAGFlowConfig ragflowConfig = RAGFlowConfig.builder()
                .apiKey("ragflow-your-api-key")             // 必填：API Key
                .baseUrl("http://address")           // 必填：RAGFlow 服务地址
                .addDatasetId("dataset-id")                 // 必填：至少设置 dataset_ids 或 document_ids
                .topK(10).similarityThreshold(0.3)
                .build();

        RAGFlowKnowledge ragflowKnowledge = RAGFlowKnowledge.builder().config(ragflowConfig).build();

        List<Document> ragflowResults   = ragflowKnowledge.retrieve("查询内容",
                RetrieveConfig.builder().limit(5).build()).block();

        ReActAgent agent = ReActAgent.builder()
                .name("助手")
                .sysPrompt("你是一个可以访问知识库的有用助手。" +
                        // agentic模式
//                        "需要信息时使用retrieve_knowledge工具" +
                        "")
                .model(AgentSkills.createDashScopeChatModel())
                .toolkit(new Toolkit())
                // 启用 Generic RAG 模式
                .knowledge(difyKnowledge)
                .ragMode(RAGMode.GENERIC)
                // agentic模式
//                .ragMode(RAGMode.AGENTIC)
                .retrieveConfig(
                        RetrieveConfig.builder()
                                .limit(3)
                                .scoreThreshold(0.3)
                                .build())
                .build();
        return agent;
    }
}
