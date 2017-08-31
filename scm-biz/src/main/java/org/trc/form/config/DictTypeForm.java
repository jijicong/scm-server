package org.trc.form.config;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/4/21.
 */
public class DictTypeForm extends QueryModel {
    /**
     * 字典类型名称
     */
    @QueryParam("name")
    @Length(max = 64, message = "字典类型名称长度不能超过64个")
    private String name;
    @QueryParam("code")
    @Length(max = 32, message = "字典类型编码长度不能超过32个")
    private String code;
    @QueryParam("description")
    @Length(max = 512, message = "字典类型说明长度不能超过512个")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString(){
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
