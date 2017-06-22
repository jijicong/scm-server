package org.trc.service.impl.util;

import org.springframework.stereotype.Service;
import org.trc.domain.util.JingDongArea;
import org.trc.mapper.jingdong.IJingDongAreaMapper;
import org.trc.mapper.jingdong.IJingDongMapper;
import org.trc.service.impl.BaseService;
import org.trc.service.util.IJingdongAreaService;

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

    @Override
    public List<JingDongArea> selectAreaByName(Map<String, Object> map){
        return iJingDongAreaMapper.selectAreaByName(map);
    }
}
