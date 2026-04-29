package scope.skills.pro.skill.manage.agentInfo.runtime.controller;

import io.agentscope.core.agent.Event;
import io.agentscope.runtime.engine.schemas.AgentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import scope.skills.pro.skill.manage.agentInfo.runtime.MyFridayAgentHandler;

/**
 * 类描述
 */

@RestController
@RequestMapping("/runtime")
public class RuntimeTestController {
//
    @Autowired(required = false)
    private MyFridayAgentHandler myFridayAgentHandler = new MyFridayAgentHandler();

    @PostMapping("/test")
    public String test(@RequestBody String question) {
        AgentRequest request = new AgentRequest();
        Flux<Event> hello = myFridayAgentHandler.streamQuery(request, question);

        // 只订阅流，不返回给前端，所有结果打印到控制台
//        hello.subscribe(
//                event -> System.out.println("收到事件：" + event),      // 收到数据时打印
//                error -> {                                            // 异常时打印错误
//                    System.err.println("处理异常：" + error.getMessage());
//                    error.printStackTrace();
//                },
//                () -> System.out.println("流处理完成！")               // 流结束时打印
//        );
//        hello.subscribe();
//        System.out.println(question);
        // 直接给前端返回一个成功提示，请求不会挂掉
        return "请求已提交，结果正在控制台输出中...";
    }
}
