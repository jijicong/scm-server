package org.trc.util;


public class CellDefinition {

    public static final String TEXT = "TEXT";

    public static final String DATE = "yyyy年MM月dd日";

    public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";

    public static final String NUM_0 = "#,##0";

    public static final String NUM_0_00 = "#,##0.00";

    private String entry;

    private String name;

    private String format;

    private int width;

    public CellDefinition(String entry, String name, String format, int width){
        this.entry = entry;
        this.name = name;
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
