package scope.skills.pro.skill.manage.agentInfo.runtime;

import io.agentscope.runtime.adapters.AgentHandler;
import io.agentscope.runtime.engine.services.agent_state.InMemoryStateService;
import io.agentscope.runtime.engine.services.agent_state.StateService;
import io.agentscope.runtime.engine.services.memory.persistence.memory.service.InMemoryMemoryService;
import io.agentscope.runtime.engine.services.memory.persistence.session.InMemorySessionHistoryService;
import io.agentscope.runtime.engine.services.memory.service.MemoryService;
import io.agentscope.runtime.engine.services.memory.service.SessionHistoryService;
import io.agentscope.runtime.sandbox.manager.ManagerConfig;
import io.agentscope.runtime.sandbox.manager.SandboxService;
import io.agentscope.runtime.sandbox.manager.utils.InMemorySandboxMap;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RuntimeConfig {

    @Bean
    public AgentHandler agentHandler(
            StateService stateService,
            SessionHistoryService sessionHistoryService,
            MemoryService memoryService,
            SandboxService sandboxService) {

        MyFridayAgentHandler handler = new MyFridayAgentHandler();
        handler.setStateService(stateService);
        handler.setSessionHistoryService(sessionHistoryService);
        handler.setMemoryService(memoryService);
        handler.setSandboxService(sandboxService);

        return handler;
    }

    @Bean
    @ConditionalOnMissingBean
    public SandboxService sandboxService() {
        return new SandboxService(ManagerConfig.builder().sandboxMap(new InMemorySandboxMap()).build());
    }

    @Bean
    @ConditionalOnMissingBean
    public StateService stateService() {
        return new InMemoryStateService();
    }

    @Bean
    @ConditionalOnMissingBean
    public SessionHistoryService sessionHistoryService() {
        return new InMemorySessionHistoryService();
    }

    @Bean
    @ConditionalOnMissingBean
    public MemoryService memoryService() {
        return new InMemoryMemoryService();
    }
}
