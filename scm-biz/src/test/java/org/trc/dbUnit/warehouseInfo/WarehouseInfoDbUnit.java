package org.trc.dbUnit.warehouseInfo;

import com.alibaba.fastjson.JSON;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.trc.biz.warehouseInfo.IWarehouseInfoBiz;
import org.trc.form.warehouse.ScmItemSyncRequest;
import org.trc.form.warehouse.ScmItemSyncResponse;
import org.trc.form.warehouse.ScmOrderCancelRequest;
import org.trc.service.BaseTest;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.util.AppResult;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by hzcyn on 2018/3/26.
 */
public class WarehouseInfoDbUnit extends BaseTest {

    @Autowired
    private IWarehouseInfoBiz warehouseInfoBiz;

    @Test
    public void receiptAdvice_success () throws Exception {
        mockQimene(true);
//        warehouseInfoBiz.warehouseItemNoticeQimen("1");
    }

    private void mockQimene(Boolean isSucc) {
        IWarehouseApiService service = mock(IWarehouseApiService.class);
        warehouseInfoBiz.setWmsService(service);
        AppResult<List<ScmItemSyncResponse>> ret = new AppResult<>();
        List<ScmItemSyncResponse> list = new ArrayList<>();
        ScmItemSyncResponse response = new ScmItemSyncResponse();
        response.setCode("200");
        response.setItemCode("skuCode123456");
        response.setItemId("warehouse123456");
        response.setMessage("成功");
        list.add(response);
        ret.setResult(list);
        ret.setAppcode("200");
        ret.setDatabuffer("");
        when(service.itemSync(any(ScmItemSyncRequest.class))).thenReturn(ret);
    }
}
