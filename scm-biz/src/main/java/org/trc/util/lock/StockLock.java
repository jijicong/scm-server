package org.trc.util.lock;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class StockLock extends LockBaseService{

    /**
     * 锁
     * @param resourceId
     * @return
     */
    public String Lock(String resourceId) {
        DistributedLock lock = new DistributedLock(pool);
        String identifier="";
        // 返回锁的value值，供释放锁时候进行判断
        if (StringUtils.isNotBlank(resourceId)){
            identifier = lock.lockWithTimeout(resourceId, 500, 1000);
        }
        return identifier;
    }

    /**
     *  释放锁
     * @param resourceId
     * @param identifier
     * @return
     */
    public boolean releaseLock(String resourceId,String identifier) {
        DistributedLock lock = new DistributedLock(pool);
        boolean isFlag = false;
        if (StringUtils.isNotBlank(resourceId) && StringUtils.isNotBlank(identifier)) {
            isFlag = lock.releaseLock(resourceId, identifier);
        }
        return isFlag;
    }
    @Override
    public void updateStock() {
        DistributedLock lock = new DistributedLock(pool);
        String identifier="";


    }
}
