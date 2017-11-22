package org.trc.service;

import com.qimen.api.request.ItemsSynchronizeRequest;
import org.trc.form.JDModel.ReturnTypeDO;

import java.util.List;

/**
 * Created by hzcyn on 2017/11/22.
 */
public interface IQimenService {

    /**
     * 商品同步接口 (批量)
     * @return
     */
    ReturnTypeDO itemsSync(String warehouseCode, String ownerCode, List<ItemsSynchronizeRequest.Item> items);
}
