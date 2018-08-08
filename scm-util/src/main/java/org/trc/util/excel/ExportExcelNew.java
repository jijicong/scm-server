package org.trc.util.excel;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trc.util.CellDefinition;
import org.trc.util.ExportExcel;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by george on 2017/3/9.
 */
public class ExportExcelNew {

    private static Logger log = LoggerFactory.getLogger(ExportExcel.class);

    /***
     * 标题行开始位置
     */
    private static final int TITLE_START_POSITION = 0;
    /***
     * 时间行开始位置
     */
    private static final int DATEHEAD_START_POSITION = 1;

    /***
     * 构造方法
     */
    private ExportExcelNew() {

    }

    public static void generateExcel(HSSFWorkbook workbook, List<?> dataList, List<CellDefinitionNew> cellDefinitionList, String sheetName, int sheetPosition){
        HSSFSheet sheet = workbook.createSheet(sheetName);
        workbook.setSheetName(sheetPosition, sheetName);
        HSSFCellStyle headCellStyle = workbook.createCellStyle();
        headCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        headCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        headCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        headCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        headCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        headCellStyle.setWrapText(true);
        HSSFFont font2 = workbook.createFont();
        font2.setFontName("仿宋_GB2312");
        font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
        font2.setFontHeightInPoints((short) 12);
        headCellStyle.setFont(font2);//选择需要用到的字体格式
        HSSFCellStyle titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        titleCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        titleCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        titleCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        titleCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        titleCellStyle.setWrapText(true);
        HSSFCellStyle contentCellStyle = workbook.createCellStyle();
        contentCellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);
        contentCellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN);
        contentCellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);
        contentCellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);
        // 表头行
        createHeadRow(workbook, sheet, titleCellStyle, cellDefinitionList);
        // 文本行
        createContentRow(workbook, sheet, contentCellStyle, dataList, cellDefinitionList);
    }

    private static void createHeadRow(HSSFWorkbook workbook, HSSFSheet sheet, HSSFCellStyle titleCellStyle, List<CellDefinitionNew> cellDefinitionList) {
        // 第1行创建
        HSSFRow headRow = sheet.createRow(TITLE_START_POSITION);
        int i = 0;
        for (CellDefinitionNew cellDefinition : cellDefinitionList) {
            HSSFCell headCell = headRow.createCell(i);
            sheet.setColumnWidth(i,cellDefinition.getWidth());
            headCell.setCellStyle(titleCellStyle);
            if(StringUtils.isBlank(cellDefinition.getTitleColorStr())){
                headCell.setCellValue(cellDefinition.getName());
            }else{
                setTitleColorStr(workbook, cellDefinition, headCell);
            }
            i++;
        }
    }

    private static void setTitleColorStr(HSSFWorkbook workbook, CellDefinitionNew cellDefinition, HSSFCell headCell){
        HSSFFont redFont = (HSSFFont) workbook.createFont();
        redFont.setColor(cellDefinition.getTitleColor().getIndex());
        int idx = cellDefinition.getName().indexOf(cellDefinition.getTitleColorStr());
        HSSFRichTextString titelVal = new HSSFRichTextString(cellDefinition.getName());
        titelVal.applyFont( idx, idx+cellDefinition.getTitleColorStr().length(), redFont );
        headCell.setCellValue(titelVal);
    }

    /**
     * @param dataList 对象数据集合
     * @param cellDefinitionList 表头信息
     */
    private static void createContentRow(HSSFWorkbook workbook, HSSFSheet sheet, HSSFCellStyle contentCellStyle, List<?> dataList, List<CellDefinitionNew> cellDefinitionList) {
        try {
            int i = 0;
            for (Object obj : dataList) {
                HSSFRow textRow = sheet.createRow(DATEHEAD_START_POSITION + i);
                for(int j=0; j<cellDefinitionList.size(); j++){
                    CellDefinitionNew cellDefinition = cellDefinitionList.get(j);
                    createContentCell(workbook, contentCellStyle, obj, textRow, cellDefinition, j);
                }
                i++;
            }
        } catch (Exception e) {
           log.error(e.getMessage(), e);
        }
    }

    private static void createContentCell(HSSFWorkbook workbook, HSSFCellStyle contentCellStyle, Object obj, HSSFRow textRow, CellDefinitionNew cellDefinition, int j) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String entry = cellDefinition.getEntry();
        String method = "get" + entry.substring(0, 1).toUpperCase() + entry.substring(1);
        Method m = obj.getClass().getMethod(method, null);
        Object object = m.invoke(obj, null);
        String value = null!=object?object.toString():"";
        HSSFCell textcell = textRow.createCell(j);
        textcell.setCellStyle(contentCellStyle);
        if(null != cellDefinition.getColor()){
            HSSFCellStyle style = workbook.createCellStyle();
            // 生成一个字体
            HSSFFont font = workbook.createFont();
            font.setColor(cellDefinition.getColor().getIndex());
            style.setFont(font);
            textcell.setCellStyle(style);
        }
        if(CellDefinition.TEXT.equals(cellDefinition.getFormat())){
            textcell.setCellValue(value.toString());
        } else if(CellDefinition.DATE.equals(cellDefinition.getFormat()) && null!=object){
            textcell.setCellValue(new SimpleDateFormat(CellDefinition.DATE).format(object));
        } else if(CellDefinition.DATE_TIME.equals(cellDefinition.getFormat()) && null!=object){
            textcell.setCellValue(new SimpleDateFormat(CellDefinition.DATE_TIME).format(object));
        }else if(CellDefinition.NUM_0.equals(cellDefinition.getFormat()) && null!=object){
            NumberFormat format = NumberFormat.getInstance();
            format.setMinimumFractionDigits(0);//setMinimumFractionDigits(int) 设置数值的小数部分允许的最小位数。
            format.setMaximumFractionDigits(0);//setMaximumFractionDigits(int) 设置数值的小数部分允许的最大位数。
            format.setMaximumIntegerDigits(20);//setMaximumIntegerDigits(int)  设置数值的整数部分允许的最大位数。
            format.setMinimumIntegerDigits(0);//setMinimumIntegerDigits(int)  设置数值的整数部分允许的最小位数.
            textcell.setCellValue(format.format(object));
        }else if(CellDefinition.NUM_0_00.equals(cellDefinition.getFormat()) && null!=object){
            NumberFormat format = NumberFormat.getInstance();
            format.setMinimumFractionDigits(2);//setMinimumFractionDigits(int) 设置数值的小数部分允许的最小位数。
            format.setMaximumFractionDigits(2);//setMaximumFractionDigits(int) 设置数值的小数部分允许的最大位数。
            format.setMaximumIntegerDigits(20);//setMaximumIntegerDigits(int)  设置数值的整数部分允许的最大位数。
            format.setMinimumIntegerDigits(0);//setMinimumIntegerDigits(int)  设置数值的整数部分允许的最小位数.
            textcell.setCellValue(Double.parseDouble(object.toString()));
        } else{
            if(StringUtils.isNotBlank(value)) {
                textcell.setCellValue(Double.parseDouble(value));
            }
        }
    }






}
