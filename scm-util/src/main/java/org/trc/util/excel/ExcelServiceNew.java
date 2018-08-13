package org.trc.util.excel;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.trc.enums.CommonExceptionEnum;
import org.trc.exception.DataHandlerException;
import org.trc.exception.ParamValidException;
import org.trc.util.AssertUtil;
import org.trc.util.CellDefinition;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExcelServiceNew<T> {

    private Logger log = LoggerFactory.getLogger(ExcelServiceNew.class);

    private final static String COUNT = "count";
    private final static String SHEET_NAME = "sheet1";

    /**
     * 获取当前泛型的class
     *
     * @return
     */
    Class<T> getCurrentClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    /**
     * 获取导入数据列表
     * * @param fieldMap 字段映射map,key-字段对应的英文名称,value-字段标题(excel中的列标题)
     *
     * @param uploadedInputStream
     * @param fileName
     * @return
     */
    public List<T> getImportDataList(Map<String, ExcelFieldInfo> fieldMap, InputStream uploadedInputStream, String fileName) throws Exception {
        //检查文件信息
        String[] titleResult = checkFileInfo(uploadedInputStream, fileName, fieldMap);
        //获取文件内容
        Map<String, String> contentResult = ImportExcelNew.readExcelContent2(",");
        if (StringUtils.equals("0", String.valueOf(contentResult.get(COUNT)))) {
            throw new DataHandlerException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "导入附件不能为空!");
        }
        return getEntityList(fieldMap, titleResult, contentResult);
    }

    /**
     * 导出数据
     *
     * @param fieldMap 字段映射map,key-字段对应的英文名称,value-字段标题(excel中的列标题)
     * @param list
     */
    public Response exportData(Map<String, ExcelFieldInfo> fieldMap, List<?> list, String fileName) {
        try {
            AssertUtil.notEmpty(fieldMap, "导出数据字段映射参数map不能为空");
            List<CellDefinitionNew> cellDefinitionList = new ArrayList<>();
            Set<Map.Entry<String, ExcelFieldInfo>> entrySet = fieldMap.entrySet();
            for (Map.Entry<String, ExcelFieldInfo> entry : entrySet) {
                CellDefinitionNew cell = new CellDefinitionNew(entry.getKey(), entry.getValue().getFieldChinessName(), entry.getValue().getTitleColorStr(), entry.getValue().getTitleColor(), CellDefinition.TEXT, entry.getValue().gethSSFColor(), 4000);
                cellDefinitionList.add(cell);
            }
            HSSFWorkbook workbook = new HSSFWorkbook();
            ExportExcelNew.generateExcel(workbook, list, cellDefinitionList, SHEET_NAME, 0);
            fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
            fileName +=  ".xls";
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            workbook.write(stream);
            return javax.ws.rs.core.Response.ok(stream.toByteArray()).header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=utf-8'zh_cn'" + fileName).type(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Cache-Control", "no-cache").build();
        } catch (Exception e) {
            log.error("数据导出异常" + e.getMessage(), e);
        }
        return null;
    }

    /**
     * 导出多个sheet的excel表格
     *
     * @param fieldMaps  多个sheet页数据对应的字段映射map数组,key-字段对应的英文名称,value-字段标题(excel中的列标题)
     * @param list       多个sheet页对应的数据列表数组
     * @param sheetNames 多个sheet页名称数组
     * @param fileName   导出文件名称
     * @param response
     */
    public void exportDataMultiSheets(Map<String, ExcelFieldInfo>[] fieldMaps, List<?>[] list, String[] sheetNames, String fileName, HttpServletResponse response) {
        try {
            AssertUtil.notEmpty(fieldMaps, "导出数据字段映射参数map不能为空");
            AssertUtil.notEmpty(sheetNames, "导出数据excel的sheet名称不能为空");
            AssertUtil.isTrue(fieldMaps.length == sheetNames.length, "导出excel的字段映射map数量和sheet名称个数不相等");
            try (HSSFWorkbook workbook = new HSSFWorkbook()) {
                for (int i = 0; i < fieldMaps.length; i++) {
                    Map<String, ExcelFieldInfo> fieldMap = fieldMaps[i];
                    List<CellDefinitionNew> cellDefinitionList = new ArrayList<>();
                    Set<Map.Entry<String, ExcelFieldInfo>> entrySet = fieldMap.entrySet();
                    for (Map.Entry<String, ExcelFieldInfo> entry : entrySet) {
                        CellDefinitionNew cell = new CellDefinitionNew(entry.getKey(), entry.getValue().getFieldChinessName(), entry.getValue().getTitleColorStr(), entry.getValue().getTitleColor(), CellDefinitionNew.TEXT, entry.getValue().gethSSFColor(), 4000);
                        cellDefinitionList.add(cell);
                    }
                    List<?> _list = null;
                    if (null == list) {
                        _list = new ArrayList<>();
                    } else {
                        if (list.length > i) {
                            _list = list[i];
                        } else {
                            _list = new ArrayList<>();
                        }
                    }
                    ExportExcelNew.generateExcel(workbook, _list, cellDefinitionList, sheetNames[i], i);
                }
                fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
                fileName += ".xls";
                response.setContentType("application/octet-stream");
                response.setCharacterEncoding("UTF-8");
                response.setHeader("Content-disposition", "attachment;filename=" + fileName);
                response.flushBuffer();
                workbook.write(response.getOutputStream());
            }
        } catch (Exception e) {
            log.error("数据导出异常" + e.getMessage(), e);
        }
    }

    /**
     * 获取数据实体列表
     *
     * @param fieldMap
     * @param titleResult
     * @param contentResult
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private List<T> getEntityList(Map<String, ExcelFieldInfo> fieldMap, String[] titleResult, Map<String, String> contentResult)
            throws IllegalAccessException, InstantiationException {
        List<T> entityList = new ArrayList<>();
        for (Map.Entry<String, String> entry : contentResult.entrySet()) {
            if (entry.getKey().equals(COUNT)) {
                continue;
            }
            String record = entry.getValue();
            String[] columVals = record.split(",");
            for (int i = 0; i < columVals.length; i++) {
                if (StringUtils.equals(ImportExcelNew.NULL_STRING, columVals[i])) {
                    columVals[i] = "";
                }
            }
            T t = getCurrentClass().newInstance();
            Set<Map.Entry<String, ExcelFieldInfo>> entrySet = fieldMap.entrySet();
            for (Map.Entry<String, ExcelFieldInfo> _entry : entrySet) {
                String fieldVal = getColumVal(columVals, titleResult, _entry.getValue().getFieldChinessName());
                Field f = null;
                try {
                    f = t.getClass().getDeclaredField(_entry.getKey());
                    f.setAccessible(true);
                } catch (NoSuchFieldException e) {
                    log.error("getEntityList方法异常",e);
                }

                try {
                    if (null != f) {
                        String type = f.getGenericType().getTypeName();
                        Object val = null;
                        if (StringUtils.equals(type, String.class.getName())) {
                            val = fieldVal;
                        } else if (StringUtils.equals(type, Integer.class.getName())) {
                            val = Integer.parseInt(fieldVal.equals("") ? "0" : fieldVal);
                        } else if (StringUtils.equals(type, Long.class.getName())) {
                            val = Long.parseLong(fieldVal.equals("") ? "0" : fieldVal);
                        } else if (StringUtils.equals(type, Double.class.getName())) {
                            val = Double.parseDouble(fieldVal.equals("") ? "0" : fieldVal);
                        } else if (StringUtils.equals(type, BigDecimal.class.getName())) {
                            val = new BigDecimal(fieldVal.equals("") ? "0" : fieldVal);
                        }
                        f.set(t, val);
                    }
                } catch (IllegalAccessException e) {
                    log.error(String.format("类%s将值%s赋值给字段%s异常", getCurrentClass().getName(), fieldVal, _entry.getKey()), e);
                }
            }
            entityList.add(t);
        }
        return entityList;
    }


    /**
     * 检查文件信息
     *
     * @param uploadedInputStream
     * @param fileName
     * @param fieldMap
     */
    private String[] checkFileInfo(InputStream uploadedInputStream, String fileName, Map<String, ExcelFieldInfo> fieldMap) {
        //检查文件类型
        checkFileType(uploadedInputStream, fileName);
        //校验导入文件抬头信息
        String[] titleResult = null;
        try {
            titleResult = ImportExcelNew.readExcelTitle(uploadedInputStream);
        } catch (Exception e) {
            log.error("导入模板错误!",e);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "导入模板错误!");
        }
        //检查导入列标题
        checkTitle(titleResult, fieldMap);
        return titleResult;
    }

    /**
     * 检查文件类型
     *
     * @param uploadedInputStream
     * @param fileName
     */
    private void checkFileType(InputStream uploadedInputStream, String fileName) {
        AssertUtil.notNull(uploadedInputStream, "上传文件不能为空");
        AssertUtil.notBlank(fileName, "上传文件名称不能为空");
        //检测是否是excel
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!(suffix.toLowerCase().equalsIgnoreCase("xls") || suffix.toLowerCase().equalsIgnoreCase("xlsx"))) {
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, "导入文件格式不支持");
        }
    }


    /**
     * 检查导入列标题
     *
     * @param titleResult
     * @param fieldMap
     */
    private void checkTitle(String[] titleResult, Map<String, ExcelFieldInfo> fieldMap) {
        StringBuilder sb = new StringBuilder();
        Set<Map.Entry<String, ExcelFieldInfo>> entrySet = fieldMap.entrySet();
        for (Map.Entry<String, ExcelFieldInfo> entry : entrySet) {
            _checkTitle(titleResult, entry.getValue().getFieldChinessName(), sb);
        }
        if (sb.length() > 0) {
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, String.format("导入订单模板缺少列%s", sb.toString()));
        }
    }

    private void _checkTitle(String[] titleResult, String colum, StringBuilder sb) {
        boolean flag = false;
        for (String title : titleResult) {
            if (StringUtils.equals(colum, title)) {
                flag = true;
                break;
            }
        }
        if (!flag) {
            sb.append(colum).append(",");
        }
    }


    /**
     * 获取列值
     *
     * @param columVals
     * @param titleResult
     * @param columName
     * @return
     */
    private String getColumVal(String[] columVals, String[] titleResult, String columName) {
        Integer idx = getColumIndex(titleResult, columName);
        if (null != idx) {
            return columVals[idx];
        }
        return "";
    }

    /**
     * 获取列对应的位置
     *
     * @param titleResult
     * @param colum
     * @return
     */
    private Integer getColumIndex(String[] titleResult, String colum) {
        for (int i = 0; i < titleResult.length; i++) {
            if (titleResult[i].equals(colum)) {
                return i;
            }
        }
        return null;
    }



}
