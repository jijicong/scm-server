package org.trc.domain.impower;

import org.hibernate.validator.constraints.Length;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.FormParam;
import javax.ws.rs.PathParam;

/**
 * @author hzszy
 */
public class AclUserChannelSell {
    @PathParam("id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @FormParam("userId")
    @Length(max = 64)
    private String userId;

    @FormParam("userAccreditId")
    private Long userAccreditId;

    @FormParam("channelCode")
    @Length(max = 32, message = "业务线编码字母和数字不能超过32个,汉字不能超过16个")
    private String channelCode;



    @FormParam("sellChannelCode")
    @Length(max = 32, message = "销售渠道编码字母和数字不能超过32个,汉字不能超过16个")
    private String sellChannelCode;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getUserAccreditId() {
        return userAccreditId;
    }

    public void setUserAccreditId(Long userAccreditId) {
        this.userAccreditId = userAccreditId;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }


    public String getSellChannelCode() {
        return sellChannelCode;
    }

    public void setSellChannelCode(String sellChannelCode) {
        this.sellChannelCode = sellChannelCode;
    }

}
