package org.trc.enums;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author A18ccms a18ccms_gmail_com
 * @ClassName: ClearanceEnum
 * @Description: 分类动作的枚举
 * @date 2017年7月18日 上午9:16:13
 */
public enum CategoryActionEnum {

    ADD_ONE_LEVEL("1", "新增一级分类"),
    ADD_TWO_LEVEL("2", "新增二级分类"),
    ADD_THREE_LEVEL("3", "新增三级分类"),
    EDIT_ONE_LEVEL("11", "修改一级分类"),
    EDIT_TWO_LEVEL("12", "修改二级分类"),
    EDIT_THREE_LEVEL("13", "修改三级分类");


    private String code;
    private String name;

    CategoryActionEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    /**
     * @param @param  name
     * @param @return
     * @return ValidEnum
     * @throws
     * @Title: getValidEnumByName
     * @Description: 根据枚举名称获取枚举
     */
    public static CategoryActionEnum getCategoryActionEnumByName(String name) {
        for (CategoryActionEnum validEnum : CategoryActionEnum.values()) {
            if (StringUtils.equals(name, validEnum.getName())) {
                return validEnum;
            }
        }
        return null;
    }

    /**
     * @param @param  name
     * @param @return
     * @return ValidEnum
     * @throws
     * @Title: getValidEnumByCode
     * @Description: 根据枚举编码获取枚举
     */
    public static CategoryActionEnum getCategoryActionEnumByCode(String code) {
        for (CategoryActionEnum validEnum : CategoryActionEnum.values()) {
            if (StringUtils.equals(validEnum.getCode(), code)) {
                return validEnum;
            }
        }
        return null;
    }

    /**
     * @param @return
     * @return JSONArray
     * @throws
     * @Title: toJSONArray
     * @Description: 转换成json数组
     */
    public static JSONArray toJSONArray() {
        JSONArray array = new JSONArray();
        for (CategoryActionEnum sexEnum : CategoryActionEnum.values()) {
            JSONObject obj = new JSONObject();
            obj.put("code", sexEnum.getCode());
            obj.put("name", sexEnum.getName());
            array.add(obj);
        }
        return array;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
