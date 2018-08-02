package org.trc.form.goods;

import io.swagger.annotations.ApiModelProperty;

import javax.ws.rs.FormParam;

/**
 * Created by hzgjl on 2018/8/2.
 */
public class ItemGroupFormEdit {
    @FormParam("itemGroupCode")
    @ApiModelProperty("启停用")
    private String isValid;


    @FormParam("itemGroupCode")
    @ApiModelProperty("商品组编号")
    private String itemGroupCode;

    public String getIsValid() {
        return isValid;
    }

    public void setIsValid(String isValid) {
        this.isValid = isValid;
    }

    public String getItemGroupCode() {
        return itemGroupCode;
    }

    public void setItemGroupCode(String itemGroupCode) {
        this.itemGroupCode = itemGroupCode;
    }
}
