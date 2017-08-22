package org.trc.domain.recordTime;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

/**
 * 该类没有规避因为方法名超长，引起的mysql 的 date too long
 * Created by sone on 2017/8/21.
 */
public class MethodInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    //类名
    private String className;
    //方法名
    private String methodName;
    //调用参数
    private String args;
    //调用频率
    private Long frequency;//(次/天)
    //使用总次数
    private Long useNumber;

    private Long totalTime;
    //平均耗时
    private Long averageTime;//（毫秒/次）
    //创建时间
    @JsonSerialize(using = CustomDateSerializer.class)
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(Long totalTime) {
        this.totalTime = totalTime;
    }

    public Long getUseNumber() {
        return useNumber;
    }

    public void setUseNumber(Long useNumber) {
        this.useNumber = useNumber;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public Long getFrequency() {
        return frequency;
    }

    public void setFrequency(Long frequency) {
        this.frequency = frequency;
    }

    public Long getAverageTime() {
        return averageTime;
    }

    public void setAverageTime(Long averageTime) {
        this.averageTime = averageTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
