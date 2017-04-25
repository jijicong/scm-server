package org.trc.form;

import org.trc.domain.score.Dict;
import org.trc.util.Pagenation;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/4/21.
 */
public class DictForm extends QueryModel{
    /**
     * 字典类型编码
     */
    @QueryParam("typeNo")
    private String typeNo;
    /**
     * 字典名称
     */
    @QueryParam("name")
    private String name;
    /**
     * 字典值
     */
    @QueryParam("value")
    private String value;

    public String getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(String typeNo) {
        this.typeNo = typeNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
