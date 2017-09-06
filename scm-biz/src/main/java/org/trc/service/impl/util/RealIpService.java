package org.trc.service.impl.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.trc.service.util.IRealIpService;
import org.trc.util.IpUtil;

import java.net.SocketException;

/**
 * Created by hzwyz on 2017/9/4 0004.
 */
@Service("realIpService")
public class RealIpService implements IRealIpService {
    private Logger log = LoggerFactory.getLogger(RealIpService.class);
    @Override
    public boolean isRealTimerService(String taskIp) {
        try {
            String realIp = IpUtil.getRealIp();
            if(!realIp.equals(taskIp)){
                log.info("非任务补偿机，跳过任务!"+realIp+"!="+taskIp);
                return true;
            }
        } catch (SocketException e) {
            e.printStackTrace();
            log.error("ConsumptionSummaryJob!跳过任务!");
            return true;
        }
        return false;
    }
}
