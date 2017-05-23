package org.trc.form.JDModel;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Created by hzwyz on 2017/5/18 0018.
 */
@Component ("configProperty")
/*@PropertySource(value = "classpath:config/dev/comsumer-config.properties",encoding = "utf-8")*/
public class JdBaseDO {
    private String grant_type;

    private String client_id;

    private String client_secret;

    private String timestamp;

    private String username;

    private String password;

    private String scope;

    private String sign;

    private String jdurl;

    public String getGrant_type() {
        return grant_type;
    }

    public void setGrant_type(String grant_type) {
        this.grant_type = grant_type;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getClient_secret() {
        return client_secret;
    }

    public void setClient_secret(String client_secret) {
        this.client_secret = client_secret;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
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

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getJdurl() {
        return jdurl;
    }

    public void setJdurl(String jdurl) {
        this.jdurl = jdurl;
    }
}
