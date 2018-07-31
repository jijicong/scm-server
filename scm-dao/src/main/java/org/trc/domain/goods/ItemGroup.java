package org.trc.domain.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 * 商品组管理
 * Created by hzgjl on 2018/7/25.
 */
@Data
public class ItemGroup extends BaseDO{
    private static final long serialVersionUID = 6077285329430988185L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PathParam("id")
    @ApiModelProperty("商品组id")
    private Long id;

    @FormParam("itemGroupCode")
    @Length(max=32,message = "商品组编码字母和数字不能超过32个,汉字不能超过16个")
    @ApiModelProperty("商品组编号")
    private String itemGroupCode;

    @FormParam("itemGroupName")
    @Length(max=40,message = "商品名称字母和数字不能超过40个,汉字不能超过20个")
    @ApiModelProperty("商品组名称")
    private String itemGroupName;

    @FormParam("channelCode")
    @Length(max = 32, message = "业务线渠道编码字母和数字不能超过32个,汉字不能超过16个")
    @ApiModelProperty("业务线编码")
    private String channelCode;

    @FormParam("leaderUserId")
    @Length(max=32,message = "组长id字母和数字不能超过32个,汉字不能超过16个")
    @ApiModelProperty("组长id")
    private String leaderUserId;

    @FormParam("leaderName")
    @Length(max=32,message = "组长名称字母和数字不能超过32个,汉字不能超过16个")
    @ApiModelProperty("组长名字")
    private String leaderName;

    @FormParam("memberUserId")
    @Length(max=1024,message = "组员id字母和数字不能超过1024个,汉字不能超过512个")
    @ApiModelProperty("所有组员id，以逗号分隔")
    private String memberUserId;

    @FormParam("memberName")
    @Length(max=1024,message = "组员名称字母和数字不能超过1024个,汉字不能超过512个")
    @ApiModelProperty("所有组员名字，以逗号分隔")
    private String memberName;

    @FormParam("remark")
    @Length(max =400, message = "商品组的备注字母和数字不能超过400个,汉字不能超过个200")
    @ApiModelProperty("备注")
    private  String remark;
}
