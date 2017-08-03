package org.trc.domain.config;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import java.util.Date;

/**
 * Created by hzszy on 2017/7/6.
 */
public class TimeRecord {
    private Long id;
    private String method;
    private Long useTime;
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date endTime; //创建时间

    @JsonSerialize(using = CustomDateSerializer.class)
    private Date startTime; //开始时间

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Long getUseTime() {
        return useTime;
    }

    public void setUseTime(Long useTime) {
        this.useTime = useTime;
    }
}
