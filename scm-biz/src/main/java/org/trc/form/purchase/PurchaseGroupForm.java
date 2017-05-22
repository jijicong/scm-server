package org.trc.form.purchase;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by sone on 2017/5/19.
 */
public class PurchaseGroupForm extends QueryModel{
    /**
     * 采购组名称
     */
    @QueryParam("name")
    @Length(max = 64)
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
