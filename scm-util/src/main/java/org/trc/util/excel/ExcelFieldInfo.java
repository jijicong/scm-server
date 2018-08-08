package org.trc.util.excel;

import org.apache.poi.hssf.util.HSSFColor;

public class ExcelFieldInfo {

    /**
     * 导入字段中文名称
     */
    private String fieldChinessName;
    /**
     * 标题颜色字符串
     */
    private String titleColorStr;

    /**
     * 标题颜色字符串颜色
     */
    private HSSFColor titleColor;

    /**
     * 导入字段显示颜色
     */
    private HSSFColor hSSFColor;

    public ExcelFieldInfo(String fieldChinessName, HSSFColor hSSFColor){
        this.fieldChinessName = fieldChinessName;
        this.hSSFColor = hSSFColor;
    }

    public ExcelFieldInfo(String fieldChinessName, String titleColorStr, HSSFColor titleColor, HSSFColor hSSFColor){
        this.fieldChinessName = fieldChinessName;
        this.titleColorStr = titleColorStr;
        this.titleColor = titleColor;
        this.hSSFColor = hSSFColor;
    }

    public String getFieldChinessName() {
        return fieldChinessName;
    }

    public void setFieldChinessName(String fieldChinessName) {
        this.fieldChinessName = fieldChinessName;
    }

    public HSSFColor gethSSFColor() {
        return hSSFColor;
    }

    public void sethSSFColor(HSSFColor hSSFColor) {
        this.hSSFColor = hSSFColor;
    }

    public String getTitleColorStr() {
        return titleColorStr;
    }

    public void setTitleColorStr(String titleColorStr) {
        this.titleColorStr = titleColorStr;
    }

    public HSSFColor getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(HSSFColor titleColor) {
        this.titleColor = titleColor;
    }
}
