package org.trc.biz;


import org.trc.common.TcoinAck;

/**
 * Created by george on 2016/12/21.
 */
public interface ITcoinService {

    /**
     * Tcoin查询
     * @param userId
     * @return
     */
    TcoinAck queryTcoinBalance(String userId);

    /**
     * Tcoin操作
     * @param userId
     * @param amount
     * @param requestNo
     * @return
     */
    TcoinAck operateTcoin(String userId, Long amount, String requestNo);

}
