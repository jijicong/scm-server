package org.trc.util.excel;


import org.apache.poi.hssf.util.HSSFColor;

public class CellDefinitionNew {

    public static final String TEXT = "TEXT";

    public static final String DATE = "yyyy年MM月dd日";

    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public static final String NUM_0 = "#,##0";

    public static final String NUM_0_00 = "#,##0.00";

    private String entry;

    private String name;

    /**
     * 标题颜色字符串
     */
    private String titleColorStr;

    /**
     * 标题颜色字符串颜色
     */
    private HSSFColor titleColor;

    private String format;

    private HSSFColor color;

    private int width;

    public CellDefinitionNew(String entry, String name, String titleColorStr, HSSFColor titleColor, String format, HSSFColor color, int width){
        this.color = color;
        this.entry = entry;
        this.name = name;
        this.titleColorStr = titleColorStr;
        this.titleColor = titleColor;
        this.format = format;
        this.width = width;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(short width) {
        this.width = width;
    }

    public HSSFColor getColor() {
        return color;
    }

    public void setColor(HSSFColor color) {
        this.color = color;
    }

    public void setWidth(int width) {
        this.width = width;
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

    @Override
    public String toString() {
        return "CellDefinition{" +
                "entry='" + entry + '\'' +
                ", name='" + name + '\'' +
                ", format='" + format + '\'' +
                ", width=" + width +
                '}';
    }
}
