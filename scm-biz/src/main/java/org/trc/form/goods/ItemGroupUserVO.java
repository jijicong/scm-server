package org.trc.form.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.ws.rs.FormParam;

/**
 * Created by hzgjl on 2018/8/3.
 */
@Data
public class ItemGroupUserVO {

    @ApiModelProperty("用户id,编辑页面需要传值")
    @FormParam("id")
    private Long id;

    @ApiModelProperty("用户名字")
    @FormParam("name")
    private String name;

    @ApiModelProperty("用户手机号码")
    @FormParam("phoneNumber")
    private String phoneNumber;

    @ApiModelProperty("商品组编号")
    @FormParam("itemGroupCode")
    private String itemGroupCode;

    @ApiModelProperty("是否组长，0组员，1组长")
    @FormParam("isLeader")
    private String isLeader;

    @ApiModelProperty("创建人,前端不需要传值")
    @FormParam("createOperator")
    private String createOperator; //创建人

    @ApiModelProperty("启停用状态0-停用,1-启用")
    @FormParam("isValid")
    private String isValid; //是否有效:0-否,1-是

    @ApiModelProperty("创建时间")
    @FormParam("createTime")
    private String createTime; //创建时间

    @ApiModelProperty("更新时间")
    @FormParam("updateTime")
    private String updateTime; //更新时间

    @ApiModelProperty("无")
    @FormParam("status")
    private String status;

    @ApiModelProperty("是否删除:0-否,1-是")
    @FormParam("isDeleted")
    private String isDeleted; //是否删除:0-否,1-是
}
