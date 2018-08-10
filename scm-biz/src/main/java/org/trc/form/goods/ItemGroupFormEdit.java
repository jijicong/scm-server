package org.trc.form.goods;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.ws.rs.FormParam;

/**
 * Created by hzgjl on 2018/8/2.
 */
@Data
@Api(value = "状态更新操作传值对象")
public class ItemGroupFormEdit {
    @FormParam("isValid")
    @ApiModelProperty("启停用")
    private String isValid;


    @FormParam("itemGroupCode")
    @ApiModelProperty("商品组编号")
    private String itemGroupCode;
}
