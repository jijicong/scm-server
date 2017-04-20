package org.trc.form;

import org.trc.domain.score.DictType;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwdx on 2017/4/18.
 */
public class DictForm extends Pagination<DictType>{
    @QueryParam("name")
    private String name;
    @QueryParam("p1")
    private String name2;
    @QueryParam("p2")
    private String name3;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName2() {
        return name2;
    }

    public void setName2(String name2) {
        this.name2 = name2;
    }

    public String getName3() {
        return name3;
    }

    public void setName3(String name3) {
        this.name3 = name3;
    }



}
