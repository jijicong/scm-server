package org.trc.form.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * Created by hzgjl on 2018/8/3.
 */
@Data
public class ItemGroupVo {

    @ApiModelProperty("商品组id，前端不需要传值")
    private Long id;

    @ApiModelProperty("商品组编号")
    private String itemGroupCode;

    @Length(max=40,message = "商品名称字母和数字不能超过40个,汉字不能超过20个")
    @ApiModelProperty("商品组名称")
    private String itemGroupName;


    @Length(max=32,message = "组长名称字母和数字不能超过32个,汉字不能超过16个")
    @ApiModelProperty("组长名字")
    private String leaderName;

    @Length(max=1024,message = "组员名称字母和数字不能超过1024个,汉字不能超过512个")
    @ApiModelProperty("所有组员名字，以逗号分隔")
    private String memberName;

    @Length(max =400, message = "商品组的备注字母和数字不能超过400个,汉字不能超过个200")
    @ApiModelProperty("备注")
    private  String remark;

    @ApiModelProperty("创建人,前端不需要传值")
    private String createOperator; //创建人
    //公共字段
    @ApiModelProperty("是否删除:0-否,1-是")
    private String isDeleted; //是否删除:0-否,1-是

    @ApiModelProperty("启停用状态0-停用,1-启用")
    private String isValid; //是否有效:0-否,1-是

    @ApiModelProperty("创建时间")
    private String createTime; //创建时间

    @ApiModelProperty("更新时间")
    private String updateTime; //更新时间
}
