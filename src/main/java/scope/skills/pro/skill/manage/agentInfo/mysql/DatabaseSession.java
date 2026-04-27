package scope.skills.pro.skill.manage.agentInfo.mysql;

import io.agentscope.core.ReActAgent;
import io.agentscope.core.session.Session;
import io.agentscope.core.session.mysql.MysqlSession;
import io.agentscope.core.state.SessionKey;
import io.agentscope.core.state.State;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Data
@Component
public class DatabaseSession implements Session {

    private MysqlSession mysqlSession;
    
    private DataSource dataSource;

    /**
     * 通过构造函数注入 DataSource（Spring 会自动调用）
     */
    @Autowired
    public DatabaseSession(DataSource dataSource) {
        this.dataSource = dataSource;
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource cannot be null");
        }
        // 使用 DataSource 初始化 MysqlSession
        this.mysqlSession = new MysqlSession(dataSource, true);
    }

    @Override
    public void save(SessionKey sessionKey, String key, State value) {
        // 保存单个状态到数据库
        mysqlSession.save(sessionKey, key, value);
    }

    @Override
    public void save(SessionKey sessionKey, String key, List<? extends State> values) {
        // 保存状态列表到数据库
        mysqlSession.save(sessionKey, key, values);
    }

    @Override
    public <T extends State> Optional<T> get(SessionKey sessionKey, String key, Class<T> type) {
        // 从数据库获取单个状态
        Optional<T> t = mysqlSession.get(sessionKey, key, type);
        return ObjectUtils.isEmpty(t) ? Optional.empty() : t;
    }

    @Override
    public <T extends State> List<T> getList(SessionKey sessionKey, String key, Class<T> itemType) {
        // 从数据库获取状态列表
        List<T> list = mysqlSession.getList(sessionKey, key, itemType);
        return ObjectUtils.isEmpty(list) ? List.of() : list;
    }

    @Override
    public boolean exists(SessionKey sessionKey) {
        // 检查会话是否存在
        boolean exists = mysqlSession.exists(sessionKey);
        return exists;
    }

    @Override
    public void delete(SessionKey sessionKey) {
        // 删除会话
        mysqlSession.delete(sessionKey);
        mysqlSession.truncateAllSessions();
    }

    @Override
    public Set<SessionKey> listSessionKeys() {
        // 列出所有会话
        Set<SessionKey> sessionKeys = mysqlSession.listSessionKeys();
        return ObjectUtils.isEmpty(sessionKeys) ? Set.of() : sessionKeys;
    }

    @Override
    public void close() {
        // 关闭连接
        mysqlSession.close();
    }


    public void saveMysql(ReActAgent agent, String sessionId) {
        agent.saveTo(this.mysqlSession, "user123");
    }
}

// / 使用
//Session session = new DatabaseSession(dbConnection);
//agent.saveTo(session, "user123");