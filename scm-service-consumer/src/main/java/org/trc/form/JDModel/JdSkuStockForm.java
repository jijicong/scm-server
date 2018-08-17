package org.trc.form.JDModel;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JdSkuStockForm {
    public JdSkuStockForm(List<JdSkuStockQueryDO> skuArray, String area) {
        this.skuArray = skuArray;
        this.area = area;
    }

    /**
     * 商品数量列表
     */
    private List<JdSkuStockQueryDO> skuArray;
    /**
     * 地址
     */
    private String area;

}
