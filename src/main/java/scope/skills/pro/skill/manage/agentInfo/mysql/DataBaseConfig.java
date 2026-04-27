package scope.skills.pro.skill.manage.agentInfo.mysql;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component("dataBaseConfig")
@ConfigurationProperties(
        prefix = "spring.datasource.dynamic.datasource.master"
)
public class DataBaseConfig {
    private String url;
    private String username;
    private String password;
    private String driverClassName;

    public DataBaseConfig() {
    }

//    public static DataSource getInstance() {
//
//    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDriverClassName() {
        return this.driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
}
