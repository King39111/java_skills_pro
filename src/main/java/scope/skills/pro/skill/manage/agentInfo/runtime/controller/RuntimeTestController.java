package scope.skills.pro.skill.manage.agentInfo.runtime.controller;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.Event;
import io.agentscope.core.agent.EventType;
import io.agentscope.core.message.Msg;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import scope.skills.pro.skill.manage.agentInfo.runtime.MyFridayAgentHandler;
import scope.skills.pro.skill.manage.agentInfo.runtime.vo.RequestionByUser;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * 测试控制层
 */
@Log4j2
@RestController
@RequestMapping("/runtime")
public class RuntimeTestController {

    @Autowired(required = false)
    private MyFridayAgentHandler myFridayAgentHandler;

//    @Autowired
//    private AgentScopeClient agentScopeClient;


    private String agentId;
    private ReActAgent agent;

    @PostMapping("/test")
    public String test(
            @RequestBody RequestionByUser request
    ) {
        try {
            if (ObjectUtils.isEmpty(agent)) {
                log.info("🔧 正在创建带沙箱的新智能体...");
                agent = myFridayAgentHandler.createAgentWithSandbox(request);
                agentId = agent.getAgentId();
                Set<String> toolNames = agent.getToolkit().getToolNames();
                log.info("🛠️ 可用工具: {}", toolNames);
            }
            CountDownLatch latch = new CountDownLatch(1);
            final StringBuilder result = new StringBuilder();
            final boolean[] hasError = {false};
            final int[] eventCount = {0};

            Flux<Event> stream = agent.stream(Msg.builder()
                    .textContent(request.getRequestion())
                    .build());
            stream.doOnNext(event -> {
                        eventCount[0]++;
                        EventType type = event.getType();
                        log.info("📨 收到事件 #{}: 类型={}", eventCount[0], type);
                        
                        if (type == EventType.TOOL_RESULT) {
                            log.info("✅ ✅ ✅ 收到工具执行结果！");
                            if (event.getMessage() != null) {
                                log.info("   工具结果内容: {}", event.getMessage());
                                String toolResult = event.getMessage().getTextContent();
                                if (toolResult != null && !toolResult.isEmpty()) {
                                    log.info("   工具结果文本: {}", toolResult);
                                }
                            }
                        }
                        
                        if (event.getMessage() != null) {
                            String textContent = event.getMessage().getTextContent();
                            if (textContent != null && !textContent.isEmpty()) {
                                log.info("💬 消息: {}", textContent);
                                result.append(textContent);
                            } else {
                                log.debug("⚠️ 消息内容为空或null，消息对象: {}", event.getMessage().getClass().getSimpleName());
                            }
                        }
                    })
                    .doOnError(error -> {
                        log.error("❌ 流错误: {}", error.getMessage(), error);
                        hasError[0] = true;
                        latch.countDown();
                    })
                    .doOnComplete(() -> {
                        log.info("\n✅ 流执行完成。总事件数: {}", eventCount[0]);
                        latch.countDown();
                    })
                    .subscribe();

            log.info("⏳ 等待流执行完成（最多 120 秒）...");
            boolean completed = latch.await(120, TimeUnit.SECONDS);

            if (!completed) {
                log.warn("⚠️ 流在超时时间内未完成");
                return "请求处理超时，请检查日志和沙箱服务状态";
            }

            if (hasError[0]) {
                return "请求执行出错，请查看日志: " + result.toString();
            }

            log.info("🎉 智能体执行完成。结果: {}", result.toString());

        } catch (Exception e) {
            log.error("❌ 错误: {}", e.getMessage(), e);
            return "请求处理失败: " + e.getMessage();
        }

        return "请求已完成，请查看控制台日志获取详细执行过程";
    }

//    @GetMapping("/test-agent")
//    public String test() {
//        // 给智能体发消息
//        String prompt = "你好，介绍一下自己";
//
//        // 调用 Studio 里的智能体
//        String response = agentScopeClient.chat("default-agent", prompt);
//
//        return "AI 返回：" + response;
//    }
}
