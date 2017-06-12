package org.trc.service.impl.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.util.CommonDO;
import org.trc.service.impower.IUserAccreditInfoService;
import org.trc.service.util.IUserNameUtilService;
import org.trc.util.AssertUtil;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hzdaa on 2017/6/10.
 */
@Service("userNameUtilService")
public class UserNameUtilService implements IUserNameUtilService{

    @Resource
    private IUserAccreditInfoService userAccreditInfoService;

    @Override
    public void handleUserName(List list) {
        if(AssertUtil.CollectionIsEmpty(list)){
            return;
        }
        Set<String> userIdsSet=new HashSet<>();
        for (Object obj:list) {
            if(obj instanceof CommonDO){
                userIdsSet.add(((CommonDO)obj).getCreateOperator());
            }
        }
        String[] userIdArr=new String[userIdsSet.size()];
        userIdsSet.toArray(userIdArr);
        Map<String,UserAccreditInfo> mapTemp = userAccreditInfoService.selectByIds(userIdArr);
        for (Object obj:list) {
            if(obj instanceof CommonDO){
                if(!StringUtils.isBlank(((CommonDO)obj).getCreateOperator())){
                    if(mapTemp!=null){
                        UserAccreditInfo userAccreditInfo=mapTemp.get(((CommonDO)obj).getCreateOperator());
                        if(userAccreditInfo!=null){
                            ((CommonDO)obj).setCreateOperator(userAccreditInfo.getName());
                        }
                    }
                }
            }
        }
    }
}
