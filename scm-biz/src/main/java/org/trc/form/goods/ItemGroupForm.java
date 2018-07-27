package org.trc.form.goods;

import org.hibernate.validator.constraints.Length;
import org.trc.util.QueryModel;

import javax.ws.rs.QueryParam;

/**
 * Created by hzgjl on 2018/7/26.
 */

public class ItemGroupForm extends QueryModel {
    @QueryParam("itemGroupName")
    @Length(max=32,message = "商品名称字母和数字不能超过32个,汉字不能超过16个")
    private String itemGroupName;

    public String getItemGroupName() {
        return itemGroupName;
    }

    public void setItemGroupName(String itemGroupName) {
        this.itemGroupName = itemGroupName;
    }
}
