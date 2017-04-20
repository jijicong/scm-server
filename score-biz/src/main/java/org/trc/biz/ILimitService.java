package org.trc.biz;

import java.util.Date;

/**
 * Created by george on 2016/12/21.
 */
public interface ILimitService {

    String getKeySuffix(Date time);

    boolean enoughLimitInAmount(String exchangeCurrency, String orderCode, int amount, Date now);

    boolean rollbackLimitInAmount(String limitInKey, String orderCode, int amount);

    boolean enoughLimitOutAmount(String exchangeCurrency, String orderCode, int amount, Date now);

    boolean rollbackLimitOutAmount(String limitOutKey, String orderCode, int amount);

    boolean enoughPersonalLimitInAmount(String exchangeCurrency, String userId, String orderCode, int amount, Date now);

    boolean rollbackPersonalLimitInAmount(String personalLimitInKey, String userId, String orderCode, int amount);

    boolean enoughPersonalLimitOutAmount(String exchangeCurrency, String userId, String orderCode, int amount, Date now);

    boolean rollbackPersonalLimitOutAmount(String personalLimitOutKey, String userId, String orderCode, int amount);

}
