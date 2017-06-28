package org.trc.domain.jingDong;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.ws.rs.PathParam;
import java.util.Date;

/**
 * Created by sone on 2017/6/27.
 */
public class JingDongAreaUpdateResult {

    private String id;//京东推送的id

    private Date time; //消息推送时间

    private String type; //消息类型

    private Result result; //变更结果

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

}
