package org.trc.domain.goods;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.trc.custom.CustomDateDeserializer;
import org.trc.custom.CustomDateSerializer;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.io.Serializable;
import java.util.Date;

/**
 * 商品组管理
 * Created by hzgjl on 2018/7/25.
 */
@Data
@Api(value = "商品组")
public class ItemGroup implements Serializable{
    private static final long serialVersionUID = 3197429285613752258L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PathParam("id")
    @ApiModelProperty("商品组id，前端不需要传值")
    private Long id;

    @FormParam("itemGroupCode")
    @Length(max=32,message = "商品组编码字母和数字不能超过32个,汉字不能超过16个")
    @ApiModelProperty("商品组编号")
    private String itemGroupCode;

    @FormParam("itemGroupName")
    @Length(max=20,message = "商品名称字母和数字不能超过20个,汉字不能超过10个")
    @ApiModelProperty("商品组名称")
    private String itemGroupName;

    @FormParam("channelCode")
    @Length(max = 32, message = "业务线渠道编码字母和数字不能超过32个,汉字不能超过16个")
    @ApiModelProperty("业务线编码,前端不需要传值")
    private String channelCode;

    @FormParam("leaderName")
    @Length(max=10,message = "组长名称字母和数字不能超过10个,汉字不能超过5个")
    @ApiModelProperty("组长名字")
    private String leaderName;

    @FormParam("memberName")
    @Length(max=1024,message = "组员名称字母和数字不能超过1024个,汉字不能超过512个")
    @ApiModelProperty("所有组员名字，以逗号分隔")
    private String memberName;

    @FormParam("remark")
    @Length(max =200, message = "商品组的备注字母和数字不能超过200个,汉字不能超过个100")
    @ApiModelProperty("备注")
    private  String remark;


    //公共字段
    @FormParam("isDeleted")
    @ApiModelProperty("是否删除:0-否,1-是")
    private String isDeleted; //是否删除:0-否,1-是

    @FormParam("createOperator")
    @Length(max = 32, message = "字典类型编码字母和数字不能超过32个,汉字不能超过16个")
    @ApiModelProperty("创建人,前端不需要传值")
    private String createOperator; //创建人

    @FormParam("isValid")
    @Length(max = 2, message = "是否有编码字母和数字不能超过2个")
    @ApiModelProperty("启停用状态0-停用,1-启用")
    private String isValid; //是否有效:0-否,1-是

    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @ApiModelProperty("创建时间")
    private Date createTime; //创建时间

    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using = CustomDateDeserializer.class)
    @ApiModelProperty("更新时间")
    private Date updateTime; //更新时间
}
