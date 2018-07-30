package org.trc.domain.goods;

import lombok.Data;
import org.trc.domain.BaseDO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.ws.rs.PathParam;

/**
 * 商品组用户关系映射表
 * Created by hzgjl on 2018/7/26.
 */
@Data
public class ItemGroupUserRelation extends BaseDO {

    private static final long serialVersionUID = 6099373355840300070L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @PathParam("id")
    private Long id;


    private String itemGroupCode;//商品组编码

    private String userId;//用户中心授权id


}

