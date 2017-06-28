package org.trc.service.impl.jingdong;

import org.trc.domain.jingDong.JingDongArea;
import org.trc.service.IBaseService;

import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/6/19.
 */
public interface IJingdongAreaService extends IBaseService<JingDongArea,Long>{

    /**
     * 查询对应的京东地址
     * @param map
     * @return
     * @throws Exception
     */
    List<JingDongArea> selectAreaByName(Map<String,Object> map);

    String addressUpdate() throws Exception;

    JingDongArea selectProvinceByName(String name);

    JingDongArea selectCityByName(String name);
}
