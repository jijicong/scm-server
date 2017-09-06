package org.trc.service.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.trc.service.util.IRealIpService;
import org.trc.util.HostData;

/**
 * Created by hzwyz on 2017/9/4 0004.
 */
@Service("realIpService")
public class RealIpService implements IRealIpService {
    private Logger log = LoggerFactory.getLogger(RealIpService.class);

    @Value("${retry.server.ip}")
    private String taskIp;
    @Override
    public boolean isRealTimerService() {
        String realIp = HostData.getLocalIP();
        if(!realIp.equals(taskIp)){
            log.info("非任务补偿机，跳过任务!"+realIp+"!="+taskIp);
            return false;
        }
        return true;
    }

}
