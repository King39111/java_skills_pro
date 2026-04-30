package scope.skills.pro.skill.manage.agentInfo.runtime.vo;

import io.agentscope.runtime.engine.schemas.AgentRequest;
import lombok.Data;

/**
 * 类描述
 */
@Data
public class RequestionByUser extends AgentRequest {

    private String requestion;

}
