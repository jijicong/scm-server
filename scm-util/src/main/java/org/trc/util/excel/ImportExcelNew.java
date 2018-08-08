package org.trc.util.excel;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Created by hzcyn on 2017/11/16.
 */
public class ImportExcelNew {

    private static Logger log = LoggerFactory.getLogger(ImportExcelNew.class);

    private static POIFSFileSystem fs;
    private static HSSFWorkbook wb;
    private static HSSFSheet sheet;
    private static HSSFRow row;

    public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String COUNT = "count";
    /**
     * 空字符串默认
     */
    public static final String NULL_STRING = "null";

    private ImportExcelNew(){

    }

    /**
     * 读取Excel表格表头的内容
     *
     * @return String 表头内容的数组
     */
    public static String[] readExcelTitle(InputStream is) {
        try {
            fs = new POIFSFileSystem(is);
            wb = new HSSFWorkbook(fs);
        } catch (IOException e) {
            log.error("读取excel内容异常", e);
        }
        sheet = wb.getSheetAt(0);
        row = sheet.getRow(0);
        // 标题总列数
        int colNum = row.getPhysicalNumberOfCells();
        String[] title = new String[colNum];
        for (int i = 0; i < colNum; i++) {
            title[i] = getStringCellValue(row.getCell((short) i));
        }
        return title;
    }


    /**
     * 读取Excel数据内容
     *
     * @return Map 包含单元格数据内容的Map对象
     */
    public static Map<String, String> readExcelContent2(String separator) {
        Map<String, String> content = new LinkedMap();
        try {
            wb = new HSSFWorkbook(fs);
        } catch (IOException e) {
            log.error("读取excel内容异常", e);
        }
        sheet = wb.getSheetAt(0);
        // 得到总行数
        int rowNum = sheet.getLastRowNum();
        content.put(COUNT, String.valueOf(rowNum));
        row = sheet.getRow(0);
        int colNum = row.getPhysicalNumberOfCells();
        // 正文内容应该从第二行开始,第一行为表头的标题
        for (int i = 1; i <= rowNum; i++) {
            StringBuilder str = new StringBuilder("");
            row = sheet.getRow(i);
            int j = 0;
            while (j < colNum) {
                // 每个单元格的数据内容用"-"分割开，以后需要时用String类的replace()方法还原数据
                // 也可以将每个单元格的数据设置到一个javabean的属性中，此时需要新建一个javabean
                String colVal = "";
                if(null != row.getCell((short) j)){
                    String val = getStringCellValue(row.getCell((short) j)).trim();
                    if(StringUtils.isNotBlank(val)){
                        colVal = val;
                    }else{
                        colVal = NULL_STRING;
                    }
                }else{
                    colVal = NULL_STRING;
                }
                str.append(colVal).append(separator);
                j++;
            }
            content.put(String.valueOf(i), str.toString());
        }
        return content;
    }

    /**
     * 获取单元格数据内容为字符串类型的数据
     *
     * @param cell Excel单元格
     * @return String 单元格数据内容
     */
    private static String getStringCellValue(HSSFCell cell){
        String strCell = "";
        switch (cell.getCellType()) {
            case HSSFCell.CELL_TYPE_STRING:
                strCell = cell.getStringCellValue();
                break;
            case HSSFCell.CELL_TYPE_NUMERIC:
                HSSFDataFormatter dataFormatter = new HSSFDataFormatter();
                strCell = dataFormatter.formatCellValue(cell);
                if(StringUtils.isNotBlank(strCell) && (strCell.contains("/") || strCell.contains("-"))){
                    try{
                        Date date = cell.getDateCellValue();
                        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
                        strCell = sdf.format(date);
                    }catch (Exception e){
                        log.error(e.getMessage(), e);
                    }
                }
                break;
            case HSSFCell.CELL_TYPE_BOOLEAN:
                strCell = String.valueOf(cell.getBooleanCellValue());
                break;
            case HSSFCell.CELL_TYPE_BLANK:
                strCell = "";
                break;
            default:
                strCell = "";
                break;
        }
        if (strCell == null) {
            return "";
        }
        return strCell;
    }


}
