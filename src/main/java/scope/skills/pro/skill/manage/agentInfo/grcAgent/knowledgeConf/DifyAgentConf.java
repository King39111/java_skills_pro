package scope.skills.pro.skill.manage.agentInfo.grcAgent.knowledgeConf;

import io.agentscope.core.rag.integration.dify.*;
import io.agentscope.core.rag.model.Document;
import io.agentscope.core.rag.model.RetrieveConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * 类描述
 */

@Component
public class DifyAgentConf {

    @Value("${dify_key}")
    public String dify_key;

    public List<?> difyResult() {
        DifyRAGConfig difyRAGConfig = this.craeteDifyRAGConfig();
        DifyKnowledge difyKnowledge = DifyKnowledge.builder().config(difyRAGConfig).build();
        List<Document> difyResults = difyKnowledge.retrieve("查询内容",
                RetrieveConfig.builder().limit(5).build()).block();
        return difyResults;
    }

    /**
     * 创建DifyRAGConfig
     *
     * @return
     */
    public DifyRAGConfig craeteDifyRAGConfig() {
        DifyRAGConfig config = DifyRAGConfig.builder()
                // === 连接配置（必填）===
                .apiKey(System.getenv(dify_key))  // Dataset API Key
                .datasetId("your-dataset-uuid")             // 数据集 ID（UUID 格式）
                // === 端点配置（可选）===
                .apiBaseUrl("https://api.dify.ai/v1")       // Dify Cloud（默认）
                // .apiBaseUrl("https://your-dify.com/v1")  // 自托管实例
                // === 检索配置（可选）===
                .retrievalMode(RetrievalMode.HYBRID_SEARCH) // 检索模式，默认 HYBRID_SEARCH
                // 可选模式：KEYWORD（关键词）、SEMANTIC_SEARCH（语义）、HYBRID_SEARCH（混合）、FULLTEXT（全文）
                .topK(10)                                   // 检索返回数量，范围 1-100，默认 10
                .scoreThreshold(0.5)                        // 相似度阈值，范围 0.0-1.0，默认 0.0
                .weights(0.6)                               // 混合搜索语义权重，范围 0.0-1.0

                // === 重排序配置（可选）===
                .enableRerank(true)                         // 启用重排序，默认 false
                .rerankConfig(RerankConfig.builder()
                        .providerName("cohere")                 // Rerank 模型提供商
                        .modelName("rerank-english-v2.0")// Rerank 模型名称
                        .topN(5)                                // 重排序后返回数量
                        .build())

                // === 元数据过滤（可选）===
                .metadataFilter(MetadataFilter.builder()
                        .logicalOperator("AND")                 // 逻辑运算符：AND 或 OR
                        .addCondition(MetadataFilterCondition.builder()
                                .name("category")                   // 元数据字段名
                                .comparisonOperator("=")            // 比较运算符
                                .value("documentation")             // 过滤值
                                .build())
                        .build())

                // === HTTP 配置（可选）===
                .connectTimeout(Duration.ofSeconds(30))     // 连接超时，默认 30s
                .readTimeout(Duration.ofSeconds(60))        // 读取超时，默认 60s
                .maxRetries(3)                              // 最大重试次数，默认 3
                .addCustomHeader("X-Custom-Header", "value") // 自定义请求头

                .build();
        return config;
    }
}
