package org.trc.enums;

import org.apache.commons.lang3.StringUtils;

/**
 * 供应商订单状态
 * Created by hzqph on 2017/5/16.
 */
public enum SupplierOrderStatusEnum {
    //WAIT_FOR_SUBMIT("1","待发送"),SUBMIT("2","已发送"),WAIT_FOR_DELIVER("3","待发货"),DELIVER("4","已发货"),SUBMIT_FAILURE("5","下单失败");
    WAIT_FOR_SUBMIT("1","待发送供应商"),ORDER_EXCEPTION("2","供应商下单异常"),WAIT_FOR_DELIVER("3","等待供应商发货"),ALL_DELIVER("4","全部发货"),
    ORDER_FAILURE("5","供应商下单失败"),PARTS_DELIVER("6","部分发货"),ORDER_CANCEL("7","已取消");
    public static SupplierOrderStatusEnum queryNameByCode(String code){
        for(SupplierOrderStatusEnum auditStatusEnum: SupplierOrderStatusEnum.values()){
            if (auditStatusEnum.getCode().equals(code)){
                return auditStatusEnum;
            }
        }
        return null;
    }

    SupplierOrderStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    private String code;
    private String name;


    /**
     *
     * @Title: getOneToNineEnumByCode
     * @Description: 根据枚举编码获取枚举
     * @param @param name
     * @param @return
     * @return OneToNineEnum
     * @throws
     */
    public static SupplierOrderStatusEnum getSupplierOrderStatusEnumByCode(String code){
        for(SupplierOrderStatusEnum supplierOrderStatusEnum : SupplierOrderStatusEnum.values()){
            if(StringUtils.equals(supplierOrderStatusEnum.getCode(), code)){
                return supplierOrderStatusEnum;
            }
        }
        return null;
    }


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
