package org.trc.service.impl.goods;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.domain.goods.BusiSkus;
import org.trc.domain.goods.Skus;
import org.trc.mapper.goods.ISkusMapper;
import org.trc.service.goods.IBusiSkusService;
import org.trc.service.goods.ISkusService;
import org.trc.service.impl.BaseService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hzwdx on 2017/5/24.
 */
@Service("busiSkusService")
public class BusiSkusService extends BaseService<BusiSkus, String> implements IBusiSkusService {



}
