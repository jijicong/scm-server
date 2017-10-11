package org.trc.form.external;

import javax.ws.rs.QueryParam;

/**
 * Created by hzwyz on 2017/10/11 0011.
 */
public class OperateForm {
    @QueryParam("id")
    private Long id;
    @QueryParam("operate")
    private Integer operate;
    @QueryParam("remark")
    private String remark;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getOperate() {
        return operate;
    }

    public void setOperate(Integer operate) {
        this.operate = operate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
