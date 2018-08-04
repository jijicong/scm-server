package org.trc.domain.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.hibernate.validator.constraints.Length;
import org.trc.custom.CustomDateDeserializer;
import org.trc.custom.CustomDateSerializer;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;
import java.io.Serializable;
import java.util.Date;

/**
 * 商品组用户
 * Created by hzgjl on 2018/7/26.
 */
@Data
public class ItemGroupUser implements Serializable{
    private static final long serialVersionUID = -9163692309990098921L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PathParam("id")
    @ApiModelProperty("用户id,编辑页面需要传值")
    private Long id;

    @FormParam("name")
    @ApiModelProperty("用户名字")
    private String name;

    @FormParam("channelCode")
    @Length(max = 32, message = "业务线渠道编码字母和数字不能超过32个,汉字不能超过16个")
    @ApiModelProperty("用户业务线编码，前端不需要传值")
    private String channelCode;

    @FormParam("phoneNumber")
    @ApiModelProperty("用户手机号码")
    private String phoneNumber;

    @FormParam("itemGroupCode")
    @ApiModelProperty("商品组编号")
    private String itemGroupCode;

    @FormParam("isLeader")
    @ApiModelProperty("是否组长，0组员，1组长")
    private String isLeader;

    @Transient
    @FormParam("status")
    @ApiModelProperty("商品组员操作：0无操作，1新增,2更新,3：删除)")
    private Integer status;

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
