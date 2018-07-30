package org.trc.domain.goods;

import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
    private Long id;

    @FormParam("name")
    private String name;

    @FormParam("channelCode")
    @Length(max = 32, message = "业务线渠道编码字母和数字不能超过32个,汉字不能超过16个")
    private String channelCode;

    @FormParam("phoneNumber")
    private String phoneNumber;
}
