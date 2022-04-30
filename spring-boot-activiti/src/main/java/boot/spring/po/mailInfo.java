package boot.spring.po;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;

@ApiModel("参数配置表")
public class MailInfo implements Serializable {
    @ApiModelProperty("主键")
    Integer id;

    @ApiModelProperty("主机服务器地址")
    String host;

    @ApiModelProperty("端口")
    Integer port;

    @ApiModelProperty("用户名")
    String username;

    @ApiModelProperty("邮箱授权码")
    String password;

    @ApiModelProperty("超时时间")
    Integer timeout;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
