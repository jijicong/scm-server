package org.trc.domain.jingDong;

/**
 * 京东地址变更的地址结果result
 * Created by sone on 2017/6/27.
 */
public class Result {

    private String areaId; //京东地址编码

    private String areaName;//京东地址名称

    private String parentId; //父京东ID编码

    private String areaLevel; //地址等级(行政级别：国家(1)、省(2)、市(3)、县(4)、镇(5))

    private String operateType; //操作类型(插入数据为1，更新时为2，删除时为3)}

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getAreaLevel() {
        return areaLevel;
    }

    public void setAreaLevel(String areaLevel) {
        this.areaLevel = areaLevel;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }
}
