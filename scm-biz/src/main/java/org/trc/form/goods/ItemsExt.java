package org.trc.form.goods;

import org.trc.domain.goods.ItemNaturePropery;
import org.trc.domain.goods.ItemSalesPropery;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.Skus;

import java.util.List;

/**
 * Created by hzwdx on 2017/5/31.
 */
public class ItemsExt {

    /**
     * 商品基础信息
     */
    private Items items;
    /**
     * 商品SKU信息
     */
    private List<Skus> skus;
    /**
     * 商品自然属性信息
     */
    private List<ItemNaturePropery> itemNatureProperys;
    /**
     * 商品采购属性信息
     */
    private List<ItemSalesPropery> itemSalesProperies;

    public Items getItems() {
        return items;
    }

    public void setItems(Items items) {
        this.items = items;
    }

    public List<Skus> getSkus() {
        return skus;
    }

    public void setSkus(List<Skus> skus) {
        this.skus = skus;
    }

    public List<ItemNaturePropery> getItemNatureProperys() {
        return itemNatureProperys;
    }

    public void setItemNatureProperys(List<ItemNaturePropery> itemNatureProperys) {
        this.itemNatureProperys = itemNatureProperys;
    }

    public List<ItemSalesPropery> getItemSalesProperies() {
        return itemSalesProperies;
    }

    public void setItemSalesProperies(List<ItemSalesPropery> itemSalesProperies) {
        this.itemSalesProperies = itemSalesProperies;
    }
}
