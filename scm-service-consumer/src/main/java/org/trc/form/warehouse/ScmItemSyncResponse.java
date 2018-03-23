package org.trc.form.warehouse;

import java.util.List;

/**
 * 仓库商品同步参数
 */
public class ScmItemSyncResponse {

    /**
     * 状态码，成功200，其他码都是错误
     */
    private String code;

    /**
     * 商品编码
     */
    private String itemCode;

    /**
     * 仓储系统商品编码
     */
    private String itemId;

    /**
     * 说明信息
     */
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
