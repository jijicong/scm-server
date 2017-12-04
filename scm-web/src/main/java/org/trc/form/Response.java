package org.trc.form;

import com.taobao.api.internal.mapping.ApiField;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author hzszy
 */
@XmlRootElement
public class Response {
    public Response(){

    }
    public Response(String flag, String code, String message) {
        this.flag = flag;
        this.code = code;
        this.message = message;
    }

    @ApiField("flag")
    private String flag;
    @ApiField("code")
    private String code;
    @ApiField("message")
    private String message;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
