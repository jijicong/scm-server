package org.trc.domain.config;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by hzwyz on 2017/6/1 0001.
 */
@Table(name = "input_record")
public class InputRecordDO {
    //主键
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    //输入参数
    private String inputParam;
    //类型
    private String type;
    //创建时间
    private String createDate;
    //更新时间
    private String updateDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInputParam() {
        return inputParam;
    }

    public void setInputParam(String inputParam) {
        this.inputParam = inputParam;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
}
