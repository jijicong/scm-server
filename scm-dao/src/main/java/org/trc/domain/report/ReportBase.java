package org.trc.domain.report;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;

/**
 * Created by hzcyn on 2018/9/17.
 */
@Setter
@Getter
public class ReportBase implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 库存类型1.正品2.残品
     */
    @Column(name = "stock_type")
    @ApiModelProperty(value = "库存类型1.正品2.残品")
    private String stockType;

    /**
     * 商品类别1.小泰良品2.非小泰良品
     */
    @Column(name = "goods_type")
    @ApiModelProperty(value = "商品类别1.小泰良品2.非小泰良品")
    private String goodsType;
}
