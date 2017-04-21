package org.trc.form;

import org.trc.domain.score.Dict;
import org.trc.util.Pagination;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/4/21.
 */
public class DictForm  extends Pagination<Dict> {
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

}
