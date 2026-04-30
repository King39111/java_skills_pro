//package scope.skills.pro.skill.manage.agentInfo.grcAgent.knowledgeConf;
//
//
//import io.agentscope.core.rag.integration.ragflow.RAGFlowConfig;
//import io.agentscope.core.rag.integration.ragflow.RAGFlowKnowledge;
//import io.agentscope.core.rag.model.Document;
//import io.agentscope.core.rag.model.RetrieveConfig;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//
///**
// * 类描述
// */
//
//@Component
//public class RagFlowAgentConf {
//
//    @Value("${ragFlow_key}")
//    public String ragFlow_key;
//
//    public RAGFlowConfig createRagFlowAgentConf() {
//        // dify知识库
//        RAGFlowConfig ragflowConfig = RAGFlowConfig.builder()
//                .apiKey(ragFlow_key)             // 必填：API Key
//                .baseUrl("http://address")           // 必填：RAGFlow 服务地址
//                .addDatasetId("dataset-id")                 // 必填：至少设置 dataset_ids 或 document_ids
//                .topK(10).similarityThreshold(0.3)
//                .build();
//
//        RAGFlowKnowledge ragflowKnowledge = RAGFlowKnowledge.builder().config(ragflowConfig).build();
//
//        List<Document> ragflowResults   = ragflowKnowledge.retrieve("查询内容",
//                RetrieveConfig.builder().limit(5).build()).block();
//
//        return ragflowConfig;
//    }
//}
