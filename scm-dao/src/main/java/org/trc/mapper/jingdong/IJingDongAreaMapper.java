package org.trc.mapper.jingdong;

import org.trc.domain.jingDong.JingDongArea;
import org.trc.util.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * Created by hzdaa on 2017/6/19.
 */
public interface IJingDongAreaMapper extends BaseMapper<JingDongArea>{

    List<JingDongArea> selectAreaByName(Map<String,Object> map);

    JingDongArea selectProvinceByName(String name);

    JingDongArea selectCityByName(String name);


}
