package org.trc.form;

import org.trc.domain.score.DictType;
import org.trc.util.Pagination;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/4/21.
 */
public class DictTypeForm extends Pagination<DictType>{
    /**
     * 字典类型名称
     */
    @QueryParam("name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
