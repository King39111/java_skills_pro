package scope.skills.pro.skill.manage.agentInfo.runtime.controller;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.Event;
import io.agentscope.core.message.Msg;
import io.agentscope.runtime.app.AgentApp;
import io.agentscope.runtime.engine.schemas.AgentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import scope.skills.pro.skill.manage.agentInfo.runtime.MyFridayAgentHandler;

import java.util.Set;

/**
 * 测试控制层
 */
@RestController
@RequestMapping("/runtime")
public class RuntimeTestController {
    //
    @Autowired(required = false)
    private MyFridayAgentHandler myFridayAgentHandler = new MyFridayAgentHandler();
    private String agentId;
    private ReActAgent agent;

    @PostMapping("/test")
    public String test(@RequestBody AgentRequest request, @RequestBody String question) {

        if (agentId == null  || !agentId.equals(agent.getAgentId()))
            agent = myFridayAgentHandler.agentSandbox(request, null);
        if (agentId != agent.getAgentId())
            agentId = agent.getAgentId();
        AgentApp agentApp = new AgentApp(myFridayAgentHandler);
        agentApp.run(9998);
        agentId = agent.getAgentId();
        Set<String> toolNames = agent.getToolkit().getToolNames();
        Flux<Event> stream = agent.stream(Msg.builder().textContent(question).build());
        stream.subscribe();
        return "请求已提交，结果正在控制台输出中...";
    }
}
