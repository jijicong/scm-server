package org.trc.service.impl.util;

import org.springframework.stereotype.Service;
import org.trc.domain.jingDong.JingDongArea;
import org.trc.mapper.jingdong.IJingDongAreaMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.impl.jingdong.IJingdongAreaService;
import org.trc.util.HttpRequestUtil;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Created by sone on 2017/6/19.
 */
@Service("jingDongAreaService")
public class JingDongAreaService extends BaseService<JingDongArea,Long> implements IJingdongAreaService{

    @Resource
    private IJingDongAreaMapper iJingDongAreaMapper;

    private static final String URL = "";

    @Override
    public List<JingDongArea> selectAreaByName(Map<String, Object> map){
        return iJingDongAreaMapper.selectAreaByName(map);
    }

    @Override
    public String addressUpdate() throws Exception {

        String url = URL;
        String data ="";
        String msg = HttpRequestUtil.sendGet(url, data);
        return msg;

    }

    @Override
    public JingDongArea selectProvinceByName(String name) {
        return iJingDongAreaMapper.selectProvinceByName(name);
    }

    @Override
    public JingDongArea selectCityByName(String name) {
        return iJingDongAreaMapper.selectCityByName(name);
    }
}
