package scope.skills.pro.skill.manage.agentInfo.AgentSkillsInfo;

import io.agentscope.core.hook.*;
import reactor.core.publisher.Mono;

/**
 * 类描述
 */

public class AgentHook {


    public Hook createLogHook(){
        Hook loggingHook = new Hook() {
            @Override
            public <T extends HookEvent> Mono<T> onEvent(T event) {
                // 处理不同类型的事件
                if (event instanceof PreCallEvent) {
                    System.out.println("智能体开始处理...");
                    return Mono.just(event);
                } else if (event instanceof ReasoningChunkEvent) {
                    ReasoningChunkEvent chunkEvent = (ReasoningChunkEvent) event;
                    System.out.print(chunkEvent.getIncrementalChunk().getTextContent());  // 打印流式输出
                    return Mono.just(event);
                } else if (event instanceof PostCallEvent) {
                    PostCallEvent postCallEvent = (PostCallEvent) event;
                    System.out.println("处理完成: " + postCallEvent.getFinalMessage().getTextContent());
                    return Mono.just(event);
                } else {
                    return Mono.just(event);
                }
            }

            @Override
            public int priority() {
                return 50;  // 高优先级
            }
        };
        return loggingHook;
    }
}
