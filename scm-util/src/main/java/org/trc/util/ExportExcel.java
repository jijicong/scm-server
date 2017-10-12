package org.trc.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by george on 2017/3/9.
 */
public class ExportExcel {

    /***
     * 构造方法
     */
    private ExportExcel() {

    }

    /***
     * 工作簿
     */
    private static HSSFWorkbook workbook;

    /***
     * sheet
     */
    private static HSSFSheet sheet;

    private static HSSFCellStyle headCellStyle;

    private static HSSFCellStyle titleCellStyle;

    /***
     * 标题行开始位置
     */
    private static final int TITLE_START_POSITION = 0;

    /***
     * 时间行开始位置
     */
    private static final int DATEHEAD_START_POSITION = 1;

    /***
     * 表头行开始位置
     */
    private static final int HEAD_START_POSITION = 2;

    /***
     * 文本行开始位置
     */
    private static final int CONTENT_START_POSITION = 3;

    /**
     * @param dataList  对象集合
     * @param titleMap  表头信息（对象属性名称->要显示的标题值)[按顺序添加]
     * @param sheetName sheet名称和表头值
     */
    public static void excelExport(List<?> dataList, Map<String, String> titleMap, String sheetName) {
        initExcel(dataList, titleMap, sheetName);
        // 写入处理结果
        try {
            //生成UUID文件名称
            UUID uuid = UUID.randomUUID();
            String filedisplay = uuid + ".xls";
            OutputStream out = new FileOutputStream("D:\\" + filedisplay);
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param dataList  对象集合
     * @param cellDefinitionList  表头信息（对象属性名称->要显示的标题值)[按顺序添加]
     * @param sheetName sheet名称和表头值
     */
    public static void excelExport(List<?> dataList, List<CellDefinition> cellDefinitionList, String sheetName) {
        initExcel(dataList, cellDefinitionList, sheetName);
        // 写入处理结果
        try {
            //生成UUID文件名称
            UUID uuid = UUID.randomUUID();
            String filedisplay = uuid + ".xls";
            OutputStream out = new FileOutputStream("D:\\" + filedisplay);
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param dataList  对象集合
     * @param cellDefinitionList  表头信息（对象属性名称->要显示的标题值)[按顺序添加]
     * @param sheetName sheet名称和表头值
     */
    public static HSSFWorkbook generateExcel(List<?> dataList, List<CellDefinition> cellDefinitionList, String sheetName){
        initExcel(dataList, cellDefinitionList, sheetName);
        return workbook;
    }

    /**
     * @param dataList  对象集合
     * @param titleMap  表头信息（对象属性名称->要显示的标题值)[按顺序添加]
     * @param sheetName sheet名称和表头值
     */
    public static void excelExport(String filename, List<?> dataList, Map<String, String> titleMap, String sheetName, HttpServletResponse response){
        initExcel(dataList, titleMap, sheetName);
        // 写入处理结果
        try {
            //如果web项目，1、设置下载框的弹出（设置response相关参数)；
            // 2、通过httpservletresponse.getOutputStream()获取// 初始化workbook
            byte[] fileNameByte = (filename).getBytes("UTF-8");
            filename = new String(fileNameByte, "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="
                    + filename);
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param dataList  对象集合
     * @param titleMap  表头信息（对象属性名称->要显示的标题值)[按顺序添加]
     * @param sheetName sheet名称和表头值
     */
    public static HSSFWorkbook generateExcel(List<?> dataList, Map<String, String> titleMap, String sheetName){
        initExcel(dataList, titleMap, sheetName);
        return workbook;
    }

    private static void initExcel(List<?> dataList, Map<String, String> titleMap, String sheetName){
        // 初始化workbook
        initHSSFWorkbook(sheetName);
        // 标题行
        createTitleRow(titleMap.size(), sheetName);
        // 时间行
        createDateHeadRow(titleMap.size());
        // 表头行
        createHeadRow(titleMap);
        // 文本行
        createContentRow(dataList, titleMap);
        //设置自动伸缩
        //autoSizeColumn(titleMap.size());
    }

    /***
     *
     * @param sheetName
     *        sheetName
     */
    private static void initHSSFWorkbook(String sheetName) {
        workbook = new HSSFWorkbook();
        sheet = workbook.createSheet(sheetName);
        headCellStyle = workbook.createCellStyle();
        headCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        headCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        headCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        headCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        headCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        HSSFFont font2 = workbook.createFont();
        font2.setFontName("仿宋_GB2312");
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
        font2.setFontHeightInPoints((short) 12);
        headCellStyle.setFont(font2);//选择需要用到的字体格式
        titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        titleCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        titleCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        titleCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        titleCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
    }

    /**
     * 生成标题（第零行创建）
     *
     * @param length
     * @param sheetName sheet名称
     */
    private static void createTitleRow(int length, String sheetName) {
        CellRangeAddress titleRange = new CellRangeAddress(0, 0, 0, length - 1);
        sheet.addMergedRegion(titleRange);
        HSSFRow titleRow = sheet.createRow(TITLE_START_POSITION);
        HSSFCell titleCell = titleRow.createCell(0);
        titleCell.setCellStyle(headCellStyle);
        titleCell.setCellValue(sheetName);
    }

    /**
     * 创建时间行（第一行创建）
     *
     * @param length
     */
    private static void createDateHeadRow(int length) {
        CellRangeAddress dateRange = new CellRangeAddress(1, 1, 0, length - 1);
        sheet.addMergedRegion(dateRange);
        HSSFRow dateRow = sheet.createRow(DATEHEAD_START_POSITION);
        HSSFCell dateCell = dateRow.createCell(0);
        dateCell.setCellStyle(headCellStyle);
        dateCell.setCellValue(new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));
    }

    /**
     * 创建表头行（第二行创建）
     *
     * @param titleMap 对象属性名称->表头显示名称
     */
    private static void createHeadRow(Map<String, String> titleMap) {
        // 第1行创建
        HSSFRow headRow = sheet.createRow(HEAD_START_POSITION);
        int i = 0;
        for (String entry : titleMap.keySet()) {
            HSSFCell headCell = headRow.createCell(i);
            headCell.setCellStyle(titleCellStyle);
            headCell.setCellValue(titleMap.get(entry));
            i++;
        }
    }

    /**
     * @param dataList 对象数据集合
     * @param titleMap 表头信息
     */
    private static void createContentRow(List<?> dataList, Map<String, String> titleMap) {
        try {
            int i = 0;
            for (Object obj : dataList) {
                HSSFRow textRow = sheet.createRow(CONTENT_START_POSITION + i);
                int j = 0;
                for (String entry : titleMap.keySet()) {
                    String method = "get" + entry.substring(0, 1).toUpperCase() + entry.substring(1);
                    Method m = obj.getClass().getMethod(method, null);
                    Object object = m.invoke(obj, null);
                    String value = null!=object?object.toString():"";
                    HSSFCell textcell = textRow.createCell(j);
                    textcell.setCellValue(value);
                    j++;
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param dataList  对象集合
     * @param cellDefinitionList  表头信息（对象属性名称->要显示的标题值)[按顺序添加]
     * @param sheetName sheet名称和表头值
     */
    public static void excelExport(String filename, List<?> dataList, List<CellDefinition> cellDefinitionList, String sheetName, HttpServletResponse response){
        initExcel(dataList, cellDefinitionList, sheetName);
        // 写入处理结果
        try {
            //如果web项目，1、设置下载框的弹出（设置response相关参数)；
            // 2、通过httpservletresponse.getOutputStream()获取// 初始化workbook
            byte[] fileNameByte = (filename).getBytes("UTF-8");
            filename = new String(fileNameByte, "ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename="
                    + filename);
            OutputStream out = response.getOutputStream();
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initExcel(List<?> dataList, List<CellDefinition> cellDefinitionList, String sheetName){
        // 初始化workbook
        initHSSFWorkbook(sheetName);
        // 标题行
        //createTitleRow(cellDefinitionList.size(), sheetName);
        // 时间行
        //createDateHeadRow(cellDefinitionList.size());
        // 表头行
        createHeadRow(cellDefinitionList);
        // 文本行
        createContentRow(dataList, cellDefinitionList);
        //设置自动伸缩
        //autoSizeColumn(cellDefinitionList.size());
    }

    /**
     * 创建表头行（第二行创建）
     *
     * @param cellDefinitionList 对象属性名称->表头显示名称
     */
    private static void createHeadRow(List<CellDefinition> cellDefinitionList) {
        // 第1行创建
        HSSFRow headRow = sheet.createRow(TITLE_START_POSITION);
        int i = 0;
        for (CellDefinition cellDefinition : cellDefinitionList) {
            HSSFCell headCell = headRow.createCell(i);
            sheet.setColumnWidth(i,cellDefinition.getWidth());
            headCell.setCellStyle(titleCellStyle);
            headCell.setCellValue(cellDefinition.getName());
            i++;
        }
    }

    /**
     * @param dataList 对象数据集合
     * @param cellDefinitionList 表头信息
     */
    private static void createContentRow(List<?> dataList, List<CellDefinition> cellDefinitionList) {
        try {
            int i = 0;
            for (Object obj : dataList) {
                HSSFRow textRow = sheet.createRow(DATEHEAD_START_POSITION + i);
                int j = 0;
                for (CellDefinition cellDefinition : cellDefinitionList) {
                    String entry = cellDefinition.getEntry();
                    String method = "get" + entry.substring(0, 1).toUpperCase() + entry.substring(1);
                    Method m = obj.getClass().getMethod(method, null);
                    Object object = m.invoke(obj, null);
                    String value = null!=object?object.toString():"";
                    HSSFCell textcell = textRow.createCell(j);
                    if(CellDefinition.TEXT.equals(cellDefinition.getFormat())){
                        textcell.setCellValue(value.toString());
                    } else if(CellDefinition.DATE.equals(cellDefinition.getFormat())){
                        if(null!=object) {
                            textcell.setCellValue(new SimpleDateFormat(CellDefinition.DATE).format(object));
                        }
                    } else if(CellDefinition.DATE_TIME.equals(cellDefinition.getFormat())){
                        if(null!=object) {
                            textcell.setCellValue(new SimpleDateFormat(CellDefinition.DATE_TIME).format(object));
                        }
                    }else if(CellDefinition.NUM_0.equals(cellDefinition.getFormat())){
                        if(null!=object) {
                        	NumberFormat format = NumberFormat.getInstance();  
                            format.setMinimumFractionDigits(0);//setMinimumFractionDigits(int) 设置数值的小数部分允许的最小位数。   
                            format.setMaximumFractionDigits(0);//setMaximumFractionDigits(int) 设置数值的小数部分允许的最大位数。  
                            format.setMaximumIntegerDigits(20);//setMaximumIntegerDigits(int)  设置数值的整数部分允许的最大位数。   
                            format.setMinimumIntegerDigits(0);//setMinimumIntegerDigits(int)  设置数值的整数部分允许的最小位数.   
                            textcell.setCellValue(format.format(object));
                        }
                    }else if(CellDefinition.NUM_0_00.equals(cellDefinition.getFormat())){
                        if(null!=object) {
                        	NumberFormat format = NumberFormat.getInstance();  
                            format.setMinimumFractionDigits(2);//setMinimumFractionDigits(int) 设置数值的小数部分允许的最小位数。   
                            format.setMaximumFractionDigits(2);//setMaximumFractionDigits(int) 设置数值的小数部分允许的最大位数。  
                            format.setMaximumIntegerDigits(20);//setMaximumIntegerDigits(int)  设置数值的整数部分允许的最大位数。   
                            format.setMinimumIntegerDigits(0);//setMinimumIntegerDigits(int)  设置数值的整数部分允许的最小位数.   
                            textcell.setCellValue(format.format(object));
                        }
                    } else{
                        if(StringUtils.isNotBlank(value)) {
                            textcell.setCellValue(Double.parseDouble(value));
                        }
                    }
                    j++;
                }
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 自动伸缩列（如非必要，请勿打开此方法，耗内存）
     *
     * @param size 列数
     */
    private static void autoSizeColumn(Integer size) {
        for (int j = 0; j < size; j++) {
            sheet.autoSizeColumn(j);
        }
    }

}
