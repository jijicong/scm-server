package org.trc.domain.goods;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 * 商品组用户
 * Created by hzgjl on 2018/7/26.
 */
@Data
public class ItemGroupUser extends BaseDO {
    private static final long serialVersionUID = 2597419973651063890L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PathParam("id")
    @ApiModelProperty("用户id")
    private Long id;

    @FormParam("name")
    @ApiModelProperty("用户名字")
    private String name;

    @FormParam("channelCode")
    @Length(max = 32, message = "业务线渠道编码字母和数字不能超过32个,汉字不能超过16个")
    @ApiModelProperty("用户业务线编码")
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
}
