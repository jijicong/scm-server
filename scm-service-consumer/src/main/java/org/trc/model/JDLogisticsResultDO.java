package org.trc.model;

import java.util.List;

/**
 * Created by ding on 2017/7/4.
 */
public class JDLogisticsResultDO {


    private String jdOrderId;

    private List<OrderTrackDO> orderTrack;

    public String getJdOrderId() {
        return jdOrderId;
    }

    public void setJdOrderId(String jdOrderId) {
        this.jdOrderId = jdOrderId;
    }

    public List<OrderTrackDO> getOrderTrack() {
        return orderTrack;
    }

    public void setOrderTrack(List<OrderTrackDO> orderTrack) {
        this.orderTrack = orderTrack;
    }

    class  OrderTrackDO{

        private String msgTime;

        private String content;

        public String getMsgTime() {
            return msgTime;
        }

        public void setMsgTime(String msgTime) {
            this.msgTime = msgTime;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
