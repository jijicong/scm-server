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
    private Long id;

    @ApiModelProperty("用户名字")
    private String name;

    @ApiModelProperty("用户手机号码")
    private String phoneNumber;

    @ApiModelProperty("商品组编号")
    private String itemGroupCode;

    @ApiModelProperty("是否组长，0组员，1组长")
    private String isLeader;

    @ApiModelProperty("创建人,前端不需要传值")
    private String createOperator; //创建人

    @ApiModelProperty("启停用状态0-停用,1-启用")
    private String isValid; //是否有效:0-否,1-是

    @ApiModelProperty("创建时间")
    private String createTime; //创建时间

    @ApiModelProperty("更新时间")
    private String updateTime; //更新时间

    @ApiModelProperty("无")
    private String status;

    @ApiModelProperty("是否删除:0-否,1-是")
    private String isDeleted; //是否删除:0-否,1-是
}
