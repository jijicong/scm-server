package org.trc.biz.impl.warehouseInfo;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.trc.biz.category.ICategoryBiz;
import org.trc.biz.warehouseInfo.IWarehouseInfoBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.category.Brand;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.SkuStock;
import org.trc.domain.goods.Skus;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.util.ExcelException;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.domain.warehouseInfo.WarehouseOwner;
import org.trc.domain.wms.WmsItemInfo;
import org.trc.enums.*;
import org.trc.exception.WarehouseInfoException;
import org.trc.form.JDWmsConstantConfig;
import org.trc.form.warehouse.ScmItemSyncRequest;
import org.trc.form.warehouse.ScmItemSyncResponse;
import org.trc.form.warehouse.ScmWarehouseItem;
import org.trc.form.warehouseInfo.*;
import org.trc.service.category.IBrandService;
import org.trc.service.category.ICategoryService;
import org.trc.service.goods.IItemsService;
import org.trc.service.goods.ISkuStockService;
import org.trc.service.goods.ISkusService;
import org.trc.service.impl.config.LogInfoService;
import org.trc.service.util.IExcelExceptionService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouse.IWarehouseApiService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseInfo.IWarehouseItemInfoService;
import org.trc.service.wms.IWmsItemInfoService;
import org.trc.util.*;
import org.trc.util.cache.WarehouseCacheEvict;
import org.trc.util.cache.WarehouseItemCacheEvict;
import tk.mybatis.mapper.entity.Example;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by wangyz on 2017/11/15.
 */
@Service("warehouseInfoBiz")
public class WarehouseInfoBiz implements IWarehouseInfoBiz {

    //错误信息
    public final static String BAR = "-";
    //错误信息
    public final static String EXCEL = ".xls";
    private static final String SUCCESS = "200";
    private static final String TITLE_ONE = "商品SKU编号";
    private static final String TITLE_TWO = "仓库商品ID";
    private static final String TITLE_THREE = "异常说明";
    private static final String CODE = "code";
    private static final String MSG = "msg";
    private static final String URL = "url";
    private final static String XLS = "xls";
    private final static String XLSX = "xlsx";
    private final static String CLASS_NAME = "warehouseItemInfo";

    private Logger log = LoggerFactory.getLogger(WarehouseInfoBiz.class);
    @Autowired
    private IWarehouseInfoService warehouseInfoService;
    @Autowired
    private IWarehouseItemInfoService warehouseItemInfoService;
    @Autowired
    private ISkusService skusService;
    @Autowired
    private IItemsService iItemsService;
    @Autowired
    private IBrandService brandService;
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private IExcelExceptionService excelExceptionService;
    @Autowired
    private ICategoryBiz categoryBiz;
    @Autowired
    private ISerialUtilService serialUtilService;
    @Autowired
    private IWarehouseApiService warehouseApiService;
    @Autowired
    private ISkuStockService skuStockService;
    @Autowired
    private JDWmsConstantConfig jDWmsConstantConfig;
    @Autowired
    private IWmsItemInfoService wmsItemInfoService;
    @Autowired
    private LogInfoService logInfoService;

    @Value("${exception.notice.upload.address}")
    private String EXCEPTION_NOTICE_UPLOAD_ADDRESS;

    @Value("${sftp.host.value}")
    private String HOST;

    @Value("${sftp.username.value}")
    private String USERNAME;

    @Value("${sftp.password.value}")
    private String PASSWORD;

    @Override
    @WarehouseCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response saveWarehouse(String code,AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notBlank(code,"仓库编号不能为空");
        log.info("查询符合条件的仓库=====》");
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("code", code);
        List<WarehouseInfo> list = warehouseInfoService.selectByExample(example);
        if (list.size()>1){
            log.info("一个奇门仓库编号取到多条数据");
        }
        WarehouseInfo warehouse = list.get(0);
        if (warehouse.getIsValid().equals(ZeroToNineEnum.ZERO.getCode())){
            String msg = "仓库已停用";
            log.error(msg);
            throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
        }
        warehouse.setSkuNum(0);

        if (warehouse.getIsNoticeSuccess()!=null && warehouse.getIsNoticeSuccess().equals(NoticeSuccessEnum.NOTIC.getCode())){
            //调用仓库接口获取仓库货主ID
            //warehouseInfo.setWarehouseOwnerId();
        }else {
            warehouse.setWarehouseOwnerId(null);
        }
        warehouse.setOwnerWarehouseState(WarehouseStateEnum.UN_NOTIC.getCode());
        warehouse.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        warehouse.setUpdateTime(Calendar.getInstance().getTime());
        log.info("保存仓库到数据库=====》");
        int count = warehouseInfoService.updateByPrimaryKeySelective(warehouse);
        if (count == 0) {
            String msg = "仓库信息管理添加新仓库到数据库失败";
            log.error(msg);
            throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
        }
        log.info("《===========保存到数据库成功");
        return ResultUtil.createSuccessResult("保存仓库成功","success");

    }

    @Override
    public Response selectWarehouseNotInLocation(AclUserAccreditInfo aclUserAccreditInfo) {
//        //1、首先查出本地存在的仓库
//        log.info("查询符合条件的仓库===》");
//        Example example1 = new Example(WarehouseInfo.class);
//        Example.Criteria criteria1 = example1.createCriteria();
//        criteria1.andEqualTo("isDelete",ZeroToNineEnum.ZERO.getCode());
//        criteria1.andEqualTo("channelCode",aclUserAccreditInfo.getChannelCode());
//        List<WarehouseInfo> resultList = warehouseInfoService.selectByExample(example1);
//        List<String> codeList = new ArrayList<>();
//        for (WarehouseInfo warehouseInfo:resultList){
//            codeList.add(warehouseInfo.getCode());
//        }
//        //2、查出我们未被添加的仓库
//        log.info("去除已经添加的仓库=========》");
//        Example example = new Example(Warehouse.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andEqualTo("isValid", ValidStateEnum.ENABLE.getCode());
//        if (codeList.size()!=0){
//            criteria.andNotIn("code",codeList);
//        }
//        List<Warehouse> list = warehouseService.selectByExample(example);
//        List<Map<String,String>> rev = new ArrayList<>();
//        for (Warehouse warehouse:list){
//            Map<String,String> map = new HashMap<>();
//            map.put("name",warehouse.getName());
//            map.put("code",warehouse.getCode());
//            rev.add(map);
//        }
//        log.info("《==========返回符合条件的仓库名称");
//        return ResultUtil.createSuccessResult("获取仓库名称成功",rev);
        return null;
    }

    @Override
    public Response selectWarehouse() {
        //1、首先查出所有的启动仓库
        log.info("开始查询启用的仓库====》");
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        //criteria.andEqualTo("isValid", ValidStateEnum.ENABLE.getCode());
        List<WarehouseInfo> list = warehouseInfoService.selectByExample(example);
        List<Map<String,String>> rev = new ArrayList<>();
        for (WarehouseInfo warehouse:list){
            Map<String,String> map = new HashMap<>();
            map.put("name",warehouse.getWarehouseName());
            map.put("code",warehouse.getCode());
            rev.add(map);
        }
        log.info("<======返回仓库名称");
        return ResultUtil.createSuccessResult("获取仓库名称成功",rev);
    }

    @Override
    public Pagenation<WarehouseInfoResult> selectWarehouseInfoByPage(WarehouseInfoForm query, Pagenation<WarehouseInfo> page,AclUserAccreditInfo aclUserAccreditInfo) {
//        AssertUtil.notNull(page.getPageNo(),"分页查询参数pageNo不能为空");
//        AssertUtil.notNull(page.getPageSize(),"分页查询参数pageSize不能为空");
//        AssertUtil.notNull(page.getStart(),"分页查询参数start不能为空");
//        log.info("开始查询符合条件的仓库信息===========》");
//        Example example = new Example(WarehouseInfo.class);
//        Example.Criteria criteria = example.createCriteria();
//        if(!StringUtils.isBlank(query.getWarehouseName())){
//            criteria.andLike("warehouseName","%"+query.getWarehouseName()+"%");
//        }
//        criteria.andEqualTo("channelCode",aclUserAccreditInfo.getChannelCode());
//        criteria.andEqualTo("isDelete",ZeroToNineEnum.ZERO.getCode());
//        example.orderBy("createTime").desc();
//        Pagenation<WarehouseInfo> pagenation = warehouseInfoService.pagination(example,page,query);
//        log.info("《==========查询结束，开始组装返回结果");
//        List<WarehouseInfo> list = pagenation.getResult();
//        List<WarehouseInfoResult> newList = new ArrayList<>();
//        for (WarehouseInfo warehouseInfo:list){
//            WarehouseInfoResult result = new WarehouseInfoResult();
//            result.setId(warehouseInfo.getId());
//            result.setWarehouseId(warehouseInfo.getWarehouseId());
//            result.setWarehouseName(warehouseInfo.getWarehouseName());
//            result.setType(warehouseInfo.getType());
//            result.setQimenWarehouseCode(warehouseInfo.getQimenWarehouseCode());
//            result.setSkuNum(warehouseInfo.getSkuNum());
//            String state = convertWarehouseState(warehouseInfo.getOwnerWarehouseState());
//            Warehouse warehouse = warehouseService.selectByPrimaryKey(Long.valueOf(warehouseInfo.getWarehouseId()));
//            result.setOwnerWarehouseState(state);
//            Integer noticeSuccess = warehouse.getIsNoticeSuccess();
//            if (noticeSuccess == null){
//                noticeSuccess=0;
//            }
//            Warehouse warehouse1 = warehouseService.selectByPrimaryKey(Long.valueOf(warehouseInfo.getWarehouseId()));
//            result.setIsNoticeWarehouseItems(warehouse1.getIsNoticeWarehouseItems()!=null?warehouse1.getIsNoticeWarehouseItems():"");
//            result.setIsNoticeSuccess(noticeSuccess);
//            result.setCreateTime(DateUtils.formatDateTime(warehouseInfo.getCreateTime()));
//            result.setUpdateTime(DateUtils.formatDateTime(warehouseInfo.getUpdateTime()));
//            result.setIsDelete(convertDeleteState(warehouseInfo));
//            result.setOwnerId(warehouseInfo.getChannelCode());
//            result.setOwnerName(warehouseInfo.getOwnerName());
//            result.setWarehouseOwnerId(warehouseInfo.getWarehouseOwnerId()==null?"":warehouseInfo.getWarehouseOwnerId());
//            result.setRemark(warehouseInfo.getRemark()==null?"":warehouseInfo.getRemark());
//            newList.add(result);
//        }
//
//        Pagenation<WarehouseInfoResult> resultPagenation = new Pagenation<>();
//        resultPagenation.setResult(newList);
//        resultPagenation.setPageNo(pagenation.getPageNo());
//        resultPagenation.setPageSize(pagenation.getPageSize());
//        resultPagenation.setTotalCount(pagenation.getTotalCount());
//        resultPagenation.setStart(pagenation.getStart());
//        log.info("组装数据完成《=============");
//        return resultPagenation;
        return null;
    }

    private void modifyWarehouseInfoItem(Pagenation<WarehouseItemInfo> page){
        if(page == null || page.getResult() == null || page.getResult().size() < 1){
            return;
        }
        List<WarehouseItemInfo> list = page.getResult();
        for(WarehouseItemInfo info : list){
            String status = info.getNoticeStatus().toString();
            String warehouseItemId = info.getWarehouseItemId();
            if(StringUtils.isBlank(warehouseItemId)){
                info.setWarehouseItemId("");
            }
            if(StringUtils.isEquals(status, ZeroToNineEnum.TWO.getCode()) ||
                    StringUtils.isEquals(status, ZeroToNineEnum.THREE.getCode()) ||
                    StringUtils.isEquals(status, ZeroToNineEnum.FOUR.getCode())){
                info.setDisabled("true");
            }
        }
    }

    @Override
    //@Cacheable(value = SupplyConstants.Cache.WAREHOUSE_ITEM)
    public Pagenation<WarehouseItemInfo> queryWarehouseItemInfoPage(WarehouseItemInfoForm form, String warehouseCode, Pagenation<WarehouseItemInfo> page) {
        AssertUtil.notNull(form, "查询仓库商品信息分页参数form不能为空");
        AssertUtil.notBlank(warehouseCode, "查询仓库商品信息分页参数warehouseCode不能为空");
        AssertUtil.notNull(page.getPageNo(), "分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(), "分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(), "分页查询参数start不能为空");

        log.info("开始查询符合条件的仓库商品信息===========》");
        Map<String, Object> map = new HashMap<>();
        map.put("skuCode", form.getSkuCode());
        map.put("itemName", form.getItemName());
        map.put("noticeStatus", form.getNoticeStatus());
        map.put("barCode", form.getBarCode());
        map.put("warehouseCode", warehouseCode);
        map.put("start", page.getStart());
        map.put("pageSize", page.getPageSize());
        int count = warehouseItemInfoService.selectWarehouseItemInfoCount(map);
        List<WarehouseItemInfo> list = warehouseItemInfoService.selectWarehouseItemInfo(map);
        if(CollectionUtils.isEmpty(list)){
            page.setTotalCount(0);
            return page;
        }
        page.setStart(page.getStart());
        page.setPageSize(page.getPageSize());
        page.setPageNo(page.getPageNo());
        page.setResult(list);
        page.setTotalCount(count);
        log.info("《==========查询结束，开始组装返回结果");
        this.modifyWarehouseInfoItem(page);
        return page;
    }


    @Override
    @WarehouseItemCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteWarehouseItemInfoById(Long id, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(id, "仓库商品信息ID不能为空");
        WarehouseItemInfo tmp = new WarehouseItemInfo();
        tmp.setId(id);
        tmp = warehouseItemInfoService.selectOne(tmp);
        if(tmp.getNoticeStatus() == Integer.parseInt(ZeroToNineEnum.THREE.getCode()) ||
                tmp.getNoticeStatus() == Integer.parseInt(ZeroToNineEnum.FOUR.getCode())){
            String msg = "只有当通知为“待通知”、“通知失败”、“取消通知”时才允许删除";
            log.error(msg);
            throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
        }
        tmp.setIsDelete(Integer.valueOf(ZeroToNineEnum.ONE.getCode()));
        tmp.setUpdateTime(Calendar.getInstance().getTime());
        int count = warehouseItemInfoService.updateByPrimaryKeySelective(tmp);
        if (count == 0) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", id.toString(), "]删除仓库商品信息失败").toString();
            log.error(msg);
            throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
        }
        //修改仓库信息sku数量
        this.updateSkuNum(id);
        //修改库存信息
        WarehouseInfo warehouseInfo = warehouseInfoService.selectByPrimaryKey(tmp.getWarehouseInfoId());
        this.deleteSkuStock(tmp, warehouseInfo);
        logInfoService.recordLog(tmp, tmp.getId().toString(), aclUserAccreditInfo.getUserId(),
                LogOperationEnum.DELETE.getMessage(), null, null);
    }

    private void deleteSkuStock(WarehouseItemInfo warehouseItemInfo, WarehouseInfo warehouseInfo){
        SkuStock skuStock = new SkuStock();
        skuStock.setSkuCode(warehouseItemInfo.getSkuCode());
        skuStock.setWarehouseId(warehouseInfo.getId());
        skuStock.setChannelCode(warehouseInfo.getChannelCode());
        skuStock.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        skuStock = skuStockService.selectOne(skuStock);
        if(skuStock != null){
            skuStock.setIsDeleted(ZeroToNineEnum.ONE.getCode());
            skuStock.setUpdateTime(Calendar.getInstance().getTime());
            skuStockService.updateByPrimaryKey(skuStock);
        }
    }

    private void updateSkuNum(Long id){
        WarehouseItemInfo tmp2 = new WarehouseItemInfo();
        tmp2.setId(id);
        tmp2 = warehouseItemInfoService.selectOne(tmp2);
        this.countSkuNum(tmp2.getWarehouseInfoId());
    }

    @Override
    @WarehouseItemCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updateWarehouseItemInfo(WarehouseItemInfo warehouseItemInfo) {
        AssertUtil.notNull(warehouseItemInfo.getId(), "仓库商品信息ID不能为空");
        warehouseItemInfo.setUpdateTime(Calendar.getInstance().getTime());
        int count = warehouseItemInfoService.updateByPrimaryKeySelective(warehouseItemInfo);
        if (count == 0) {
            String msg = CommonUtil.joinStr("修改仓库商品信息", JSON.toJSONString(warehouseItemInfo), "数据库操作失败").toString();
            log.error(msg);
            throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
        }
    }

    @Override
    public Response exportWarehouseItems(WarehouseItemInfoForm form, Long warehouseInfoId) {
        try{
            AssertUtil.notNull(warehouseInfoId,"仓库的主键不能为空");
            log.info("开始查询符合条件的仓库商品信息===========》");
            Example example = new Example(WarehouseItemInfo.class);
            Example.Criteria criteria = example.createCriteria();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(form.getSkuCode())) {
                criteria.andLike("skuCode", "%" + form.getSkuCode() + "%");
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(form.getItemName())) {
                criteria.andLike("itemName", "%" + form.getItemName() + "%");
            }
            if (org.apache.commons.lang3.StringUtils.isNotBlank(form.getNoticeStatus())) {
                criteria.andEqualTo("noticeStatus", form.getNoticeStatus());
            }
            criteria.andEqualTo("warehouseInfoId", String.valueOf(warehouseInfoId));
            criteria.andEqualTo("isDelete", "0");
            example.orderBy("noticeStatus").asc();
            example.orderBy("updateTime").desc();
            List<WarehouseItemInfo> list = warehouseItemInfoService.selectByExample(example);

            List<WarehouseItemsResult> results = converItemsResult(list);
            //开始导出商品信息
            log.info("开始导出商品信息=========》");
            CellDefinition skuCode = new CellDefinition("skuCode", "商品SKU编号", CellDefinition.TEXT, null, 4000);
            CellDefinition itemName = new CellDefinition("itemName", "商品SKU名称", CellDefinition.TEXT, null, 4000);
            CellDefinition specNatureInfo = new CellDefinition("specNatureInfo", "规格", CellDefinition.TEXT, null, 4000);
            CellDefinition isValid = new CellDefinition("isValid", "商品状态", CellDefinition.TEXT, null, 4000);
            CellDefinition warehouseItemId = new CellDefinition("warehouseItemId", "仓库商品ID", CellDefinition.TEXT, null, 4000);
            CellDefinition noticeStatus = new CellDefinition("noticeStatus", "通知仓库状态", CellDefinition.TEXT, null, 4000);
            CellDefinition updateTime = new CellDefinition("updateTime", "最近更新时间", CellDefinition.TEXT, null, 4000);

            List<CellDefinition> cellDefinitionList = new ArrayList<>();
            cellDefinitionList.add(skuCode);
            cellDefinitionList.add(itemName);
            cellDefinitionList.add(specNatureInfo);
            cellDefinitionList.add(isValid);
            cellDefinitionList.add(warehouseItemId);
            cellDefinitionList.add(noticeStatus);
            cellDefinitionList.add(updateTime);
            String sheetName = "仓库信息管理-商品信息报表";
            String fileName = "仓库信息管理-商品信息报表" + EXCEL;
            try {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
            HSSFWorkbook hssfWorkbook = ExportExcel.generateExcel(results, cellDefinitionList, sheetName);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            hssfWorkbook.write(stream);
            log.info("导出商品新成功《==========");
            return Response.ok(stream.toByteArray()).header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=utf-8'zh_cn'" + fileName).type(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Cache-Control", "no-cache").build();
        }catch (Exception e){
            log.error("仓库信息管理-商品信息导出异常"+e.getMessage(),e);
            return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION.getCode()),"仓库信息管理-商品信息导出异常");
        }
    }

    @Override
    @WarehouseItemCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response saveWarehouseItemsSku(String items, Long warehouseInfoId, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(warehouseInfoId,"仓库的主键不能为空");
        AssertUtil.notBlank(items,"至少选择一件商品");
        String userId = aclUserAccreditInfo.getUserId();
        AssertUtil.notBlank(userId, "获取当前登录的userId失败");
        List<Skus> itemsList = JSON.parseArray(items,Skus.class);
        //验证商品是否停用
        List<String> stopSkuCode = valideItems(itemsList);
        if (stopSkuCode.size()>0){
            return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION.getCode()),"如下商品SKU停用："+stopSkuCode.toString());
        }
        //验证商品是否添加过
        List<String> hasAdd = hasAddItems(itemsList,warehouseInfoId);
        if (hasAdd.size()>0){
            return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION.getCode()),"如下商品SKU已经添加："+hasAdd.toString());
        }
        //验证仓库是否通知成功
        WarehouseInfo warehouseInfo = warehouseInfoService.selectByPrimaryKey(warehouseInfoId);
        if (!StringUtils.isEquals(warehouseInfo.getOwnerWarehouseState(), ZeroToNineEnum.ONE.getCode())){
            return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION.getCode()),"仓库状态为非通知成功状态");
        }
        List<WarehouseItemInfo> list = new ArrayList<>();
        List<String> skuList = new ArrayList<>();
        for (Skus sku:itemsList){
            skuList.add(sku.getSkuCode());
        }
        Map<String,String> map = getItemNoBySku(skuList);
        if (map==null){
            return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION.getCode()),"根据skucode获取商品货号为空");
        }
        List<Skus> list2 = new ArrayList<>();
        List<Skus> list3 = new ArrayList<>();

        for (Skus sku:itemsList){
            if (StringUtils.isBlank(sku.getSkuName())){
                list2.add(sku);
            }
            if (StringUtils.isBlank(sku.getSkuCode())){
                list3.add(sku);
            }
        }
        AssertUtil.isTrue(list2.size()==0,"商品名称不能为空");
        AssertUtil.isTrue(list3.size()==0,"商品sku编码不能为空");
        String operationalNature = warehouseInfo.getOperationalNature();
        String warehouseName = warehouseInfo.getWarehouseName();
        String warehouseCode = warehouseInfo.getCode();
        for (Skus sku:itemsList){
            WarehouseItemInfo warehouseItemInfo = new WarehouseItemInfo();
            warehouseItemInfo.setWarehouseInfoId(warehouseInfoId);
            warehouseItemInfo.setWarehouseItemId(String.valueOf(sku.getItemId()));
            warehouseItemInfo.setSkuCode(sku.getSkuCode());
            warehouseItemInfo.setItemName(sku.getSkuName());
            warehouseItemInfo.setSpecNatureInfo(sku.getSpecInfo());
            warehouseItemInfo.setIsValid(Integer.valueOf(ZeroToNineEnum.ONE.getCode()));
            warehouseItemInfo.setWarehouseItemId(null);
            warehouseItemInfo.setNoticeStatus(NoticsWarehouseStateEnum.UN_NOTICS.getCode());
            warehouseItemInfo.setBarCode(sku.getBarCode());
            warehouseItemInfo.setIsDelete(Integer.valueOf(ZeroToNineEnum.ZERO.getCode()));
            //要修改
            //根据sku查询到spu,然后根据spu去items表查询出
            if (map.get(sku.getSkuCode())==null){
                log.info("未获取到该sku编号对应的商品货号");
                continue;
            }
            warehouseItemInfo.setSpuCode(sku.getSpuCode());
            warehouseItemInfo.setItemNo(map.get(sku.getSkuCode()));
            warehouseItemInfo.setItemType(ItemTypeEnum.NOEMAL.getCode());
            warehouseItemInfo.setWarehouseCode(warehouseInfo.getCode());
            warehouseItemInfo.setWarehouseOwnerId(warehouseInfo.getWarehouseOwnerId());
            warehouseItemInfo.setWmsWarehouseCode(warehouseInfo.getWmsWarehouseCode());
            if(StringUtils.isEquals(OperationalNatureEnum.SELF_SUPPORT.getCode(), operationalNature)){
                warehouseItemInfo.setNoticeStatus(NoticsWarehouseStateEnum.SUCCESS.getCode());
                warehouseItemInfo.setWarehouseItemId(sku.getSkuCode());
                warehouseItemInfoService.insert(warehouseItemInfo);
                logInfoService.recordLog(warehouseItemInfo, String.valueOf(warehouseItemInfo.getId()), userId,
                        LogOperationEnum.ADD_ITEM.getMessage(), null, null);
                logInfoService.recordLog(warehouseItemInfo, String.valueOf(warehouseItemInfo.getId()), warehouseName,
                        LogOperationEnum.NOTICE_SUCCESS.getMessage(), null, null);
            }else{
                warehouseItemInfoService.insert(warehouseItemInfo);
                logInfoService.recordLog(warehouseItemInfo, String.valueOf(warehouseItemInfo.getId()), userId,
                        LogOperationEnum.ADD_ITEM.getMessage(), null, null);
            }
            list.add(warehouseItemInfo);
        }
//        warehouseItemInfoService.insertList(list);
        countSkuNum(warehouseInfoId);
        //新增库存表信息
        if(StringUtils.isEquals(OperationalNatureEnum.SELF_SUPPORT.getCode(), operationalNature)){
            saveSkuStockIsNotice(list, warehouseInfo);
            //更新子系统商品
            this.saveWmsItemInfo(list);
        }else{
            this.saveSkuStock(list, warehouseInfo);
        }

        return ResultUtil.createSuccessResult("添加新商品成功","success");
    }

    private void saveWmsItemInfo(List<WarehouseItemInfo> list){
        List<WmsItemInfo> wmsItemInfos = new ArrayList<>();
        for(WarehouseItemInfo info : list){
            Items items = new Items();
            items.setSpuCode(info.getSpuCode());
            items = iItemsService.selectOne(items);

            WmsItemInfo wmsItemInfo = new WmsItemInfo();
            wmsItemInfo.setWarehouseCode(info.getWarehouseCode());
            wmsItemInfo.setBrandId(items.getBrandId());
            wmsItemInfo.setCategoryId(items.getCategoryId());
            wmsItemInfo.setBarCode(info.getBarCode());
            wmsItemInfo.setSkuCode(info.getSkuCode());
            wmsItemInfo.setSpuCode(info.getSpuCode());
            wmsItemInfo.setSkuName(info.getItemName());
            wmsItemInfo.setSpecNatureInfo(info.getSpecNatureInfo());
            wmsItemInfo.setOutSkuCode(info.getSkuCode());
            wmsItemInfo.setRealInventory(0L);
            wmsItemInfo.setLockDefectiveInventory(0L);
            wmsItemInfo.setLockInventory(0L);
            wmsItemInfo.setRealDefectiveInventory(0L);
            wmsItemInfo.setLockAllocateInventory(0L);
            wmsItemInfo.setLockAllocateDefectiveInventory(0L);
            wmsItemInfo.setDefectiveOnWayInventory(0L);
            wmsItemInfo.setOnWayInventory(0L);
            wmsItemInfo.setFrozenInventory(0L);
            wmsItemInfos.add(wmsItemInfo);
        }
        if(wmsItemInfos.size() > 0){
            wmsItemInfoService.insertList(wmsItemInfos);
        }
    }

    private void saveSkuStock(List<WarehouseItemInfo> list, WarehouseInfo warehouseInfo){
        List<SkuStock> skuStockList = new ArrayList<>();
        SkuStock skuStock = null;
        for(WarehouseItemInfo warehouseItemInfo : list){
            skuStock = new SkuStock();
            skuStock.setSpuCode(warehouseItemInfo.getSpuCode());
            skuStock.setSkuCode(warehouseItemInfo.getSkuCode());
            skuStock.setChannelCode(warehouseInfo.getChannelCode());
            skuStock.setWarehouseId(warehouseInfo.getId());
            skuStock.setWarehouseCode(warehouseInfo.getCode());
            skuStock.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            skuStock.setCreateTime(Calendar.getInstance().getTime());
            skuStock.setUpdateTime(Calendar.getInstance().getTime());
            skuStock.setIsValid(ZeroToNineEnum.ONE.getCode());
            skuStock.setAvailableInventory(0L);
            skuStock.setAvailableDefectiveInventory(0L);
            skuStock.setLockInventory(0L);
            skuStock.setAirInventory(0L);
            skuStock.setFrozenInventory(0L);
            skuStock.setRealInventory(0L);
            skuStock.setDefectiveInventory(0L);
            skuStockList.add(skuStock);
        }
        skuStockService.insertList(skuStockList);
    }

    private void countSkuNum(Long warehouseInfoId) {
        //开始统计warehouseItem数量
        AssertUtil.notNull(warehouseInfoId,"仓库信息Id不能为空");
        Example example01 = new Example(WarehouseItemInfo.class);
        Example.Criteria criteria01 = example01.createCriteria();
        criteria01.andEqualTo("warehouseInfoId",warehouseInfoId);
        criteria01.andEqualTo("isDelete",ZeroToNineEnum.ZERO.getCode());
        List<WarehouseItemInfo> list1 =warehouseItemInfoService.selectByExample(example01);
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",warehouseInfoId);
        WarehouseInfo warehouseInfo1 = new WarehouseInfo();
        warehouseInfo1.setSkuNum(list1.size());
        warehouseInfoService.updateByExampleSelective(warehouseInfo1,example);
    }

    private Map<String,String> getItemNoBySku(List<String> skuList){
        if (skuList == null){
            return null;
        }
        Example example = new Example(Skus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode",skuList);
        List<Skus> list = skusService.selectByExample(example);
        if (list.size()==0){
            return null;
        }
        Set<String> spuSet = new HashSet<>();
        for (Skus sku :list){
            spuSet.add(sku.getSpuCode());
        }

        Example example1 = new Example(Items.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andIn("spuCode",spuSet);
        List<Items> itemsList =iItemsService.selectByExample(example1);
        Map<String,String> map = new HashMap<>();
        for (Items items: itemsList){
            for (Skus skus :list){
                if (items.getSpuCode().equals(skus.getSpuCode())){
                    map.put(skus.getSkuCode(),items.getItemNo());
                }
            }
        }
        return map;

    }

    private List<String> valideItems(List<Skus> itemsList){
        List<String> list = new ArrayList<>();
        for (Skus sku:itemsList){
            list.add(sku.getSkuCode());
        }
        Example example = new Example(Skus.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode",list);
        List<Skus> skuList = skusService.selectByExample(example);
        List<String> stopSkuCode = new ArrayList<>();
        for (Skus sku:skuList){
            if (sku.getIsValid().equals(ZeroToNineEnum.ZERO.getCode())){
                stopSkuCode.add(sku.getSkuCode());
            }
        }
        return stopSkuCode;
    }

    private List<String> hasAddItems(List<Skus> itemsList,Long warehouseInfoId){
        List<String> list = new ArrayList<>();
        for (Skus sku:itemsList){
            list.add(sku.getSkuCode());
        }
        Example example = new Example(WarehouseItemInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode",list);
        criteria.andEqualTo("isDelete",ZeroToNineEnum.ZERO.getCode());
        criteria.andEqualTo("warehouseInfoId",warehouseInfoId);
        List<WarehouseItemInfo> skuList = warehouseItemInfoService.selectByExample(example);
        List<String> hasAdd = new ArrayList<>();
        for (WarehouseItemInfo sku:skuList){
            hasAdd.add(sku.getSkuCode());
        }
        return hasAdd;
    }

    @Override
    public Pagenation<ItemsResult> queryWarehouseItemsSku(SkusForm form, Pagenation<Skus> page, Long warehouseInfoId){
        AssertUtil.notNull(warehouseInfoId, "查询仓库商品信息分页参数warehouseInfoId不能为空");
        AssertUtil.notNull(page.getPageNo(), "分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(), "分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(), "分页查询参数start不能为空");
        log.info("开始查询已经添加过的仓库商品信息===========》");
        Example example = new Example(WarehouseItemInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDelete",ZeroToNineEnum.ZERO.getCode());
        criteria.andEqualTo("warehouseInfoId",warehouseInfoId);
        List<WarehouseItemInfo> warehouseItems = warehouseItemInfoService.selectByExample(example);
        Set<String> excludeSkuCode = new HashSet<>();
        for (WarehouseItemInfo warehouseItemInfo:warehouseItems){
            excludeSkuCode.add(warehouseItemInfo.getSkuCode());
        }
        log.info("添加过的商品信息结束，开始查询没有没有添加过的sku信息================》");
//        Example example1 = new Example(Skus.class);
//        Example.Criteria criteria1 = example1.createCriteria();
//        criteria1.andEqualTo("isValid",ZeroToNineEnum.ONE.getCode());
//        if (excludeSkuCode.size()!=0){
//            criteria1.andNotIn("skuCode",excludeSkuCode);
//        }
//        setQueryParam(example1, criteria1, form);
//        Pagenation<Skus> pageTem = skusService.pagination(example1,page,form);
//        List<Skus> includeList = pageTem.getResult();
        Map<String, Object> map1 = new HashMap<>();
        map1.put("skuName", form.getSkuName());
        if(StringUtils.isNotEmpty(form.getSkuCode())){
            map1.put("skuCode", form.getSkuCode().split(","));
            map1.put("skuCodeTemp", "skuTemp");
        }
        if(StringUtils.isNotEmpty(form.getBarCode())){
            String[] barCodes = form.getBarCode().split(",");
            List<String> barCodeList = new ArrayList<>();
            for(String bar : barCodes){
                if(StringUtils.isNotEmpty(bar)){
                    barCodeList.add(bar);
                }
            }
            if(barCodeList.size() > 0){
                map1.put("barCode", barCodeList);
                map1.put("barCodeTemp", "barTemp");
            }
        }
        map1.put("brandName", form.getBrandName());
        map1.put("spuCode", form.getSpuCode());
        map1.put("skuCodes", excludeSkuCode.toArray());
        if(excludeSkuCode.size() > 0){
            map1.put("spuTemp", "sku");
        }else{
            map1.put("spuTemp", null);
        }
        Integer skusListCount = 0;
        List<Skus> includeList = null;
        skusListCount = skusService.selectSkuListCount(map1);
        if(skusListCount < 1){
            return new Pagenation<ItemsResult>();
        }
        //获取分页数
        map1.put("start", page.getStart());
        map1.put("pageSize", page.getPageSize());
        includeList = skusService.selectSkuList(map1);
        log.info("开始补全未添加过的sku信息===============》");
        Pagenation<ItemsResult> pagenation = new Pagenation<>();
        if (includeList.size()!=0){
            Map<String,BrandCategoryForm> map = completionData(includeList);
            if (map==null){
                String msg = "补全商品信息异常";
                log.error(msg);
                throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
            }
            List<ItemsResult> newList = new ArrayList<>();
            for (Skus sku:includeList){
                ItemsResult itemsResult = new ItemsResult();
                itemsResult.setSkuCode(sku.getSkuCode());
                itemsResult.setSkuName(sku.getSkuName());
                itemsResult.setSpuCode(sku.getSpuCode());
                itemsResult.setSpecInfo(sku.getSpecInfo());
                BrandCategoryForm BrandCategoryForm = map.get(sku.getSpuCode());
                itemsResult.setBrandName(BrandCategoryForm.getBrandName());
                itemsResult.setCategoryName(BrandCategoryForm.getCategoryName());
                itemsResult.setBarCode(sku.getBarCode());
                itemsResult.setItemId(sku.getItemId());
                newList.add(itemsResult);
            }
            pagenation.setStart(page.getStart());
            pagenation.setTotalCount(skusListCount);
            pagenation.setPageSize(page.getPageSize());
            pagenation.setPageNo(page.getPageNo());
            pagenation.setResult(newList);
            log.info("补全数据结束，返回结果");
        }
        return pagenation;
    }

    private void setQueryParam(Example example, Example.Criteria criteria, SkusForm form){
        if (!StringUtils.isBlank(form.getSkuName())){
            criteria.andLike("skuName","%"+form.getSkuName()+"%");
        }
        if (!StringUtils.isBlank(form.getSkuCode())){
            criteria.andLike("skuCode","%"+form.getSkuCode()+"%");
        }
        if (!StringUtils.isBlank(form.getSpuCode())){
            criteria.andLike("spuCode","%"+form.getSpuCode()+"%");
        }

    }

    private Map<String,BrandCategoryForm> completionData(List<Skus> includeList){
        try{
            Set<String> spuCode = new HashSet<>();
            for (Skus sku:includeList){
                spuCode.add(sku.getSpuCode());
            }
            Example example = new Example(Items.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andIn("spuCode",spuCode);
            List<Items> itemsList = iItemsService.selectByExample(example);
            List<Long> brandList = new ArrayList<>();
            List<Long> categoryList = new ArrayList<>();
            for(Items items :itemsList){
                brandList.add(items.getBrandId());
                categoryList.add(items.getCategoryId());
            }
            Example example01 = new Example(Brand.class);
            Example.Criteria criteria01 = example01.createCriteria();
            criteria01.andIn("id",brandList);
            List<Brand> brands = brandService.selectByExample(example01);
            Map<Long,String> brandMap = new HashMap<>();
            for (Brand brand:brands){
                brandMap.put(brand.getId(),brand.getName());
            }
            Map<String,BrandCategoryForm> resultMap = new HashMap<>();
            for (Items items :itemsList){
                BrandCategoryForm brandCategoryForm = new BrandCategoryForm();
                brandCategoryForm.setBrandName(brandMap.get(items.getBrandId()));
                String categoryName = categoryBiz.getCategoryName(items.getCategoryId());
                brandCategoryForm.setCategoryName(categoryName);
                resultMap.put(items.getSpuCode(),brandCategoryForm);
            }
            return resultMap;
        }catch (Exception e){
            log.error("补全数据异常",e);
            return null;
        }
    }

    private List<WarehouseItemsResult> converItemsResult(List<WarehouseItemInfo> list){
        List<WarehouseItemsResult> resultList = new ArrayList<>();
        for (WarehouseItemInfo warehouseItemInfo:list){
            WarehouseItemsResult warehouseItemsResult = new WarehouseItemsResult();
            warehouseItemsResult.setSkuCode(warehouseItemInfo.getSkuCode());
            warehouseItemsResult.setItemName(warehouseItemInfo.getItemName());
            warehouseItemsResult.setSpecNatureInfo(warehouseItemInfo.getSpecNatureInfo());
            warehouseItemsResult.setIsValid(itemState(String.valueOf(warehouseItemInfo.getIsValid())));
            warehouseItemsResult.setWarehouseItemId(warehouseItemInfo.getWarehouseItemId());
            warehouseItemsResult.setNoticeStatus(convertNoticState(String.valueOf(warehouseItemInfo.getNoticeStatus())));
            warehouseItemsResult.setUpdateTime(DateUtils.formatDateTime(warehouseItemInfo.getCreateTime()));
            resultList.add(warehouseItemsResult);
        }
        return resultList;
    }

    private String itemState(String state){
        String str = null;
        if (StringUtils.isBlank(state)){
            str = "商品状态为空";
        }else if (state.equals(ZeroToNineEnum.ZERO.getCode())){
            str = "停用";
        }else if (state.equals(ZeroToNineEnum.ONE.getCode())){
            str = "启用";
        }
        return str;
    }

    private String convertNoticState(String ownerWarehouseState) {
        String state = null;
        if(StringUtils.isBlank(ownerWarehouseState)){
            state = "通知仓库状态为空";
        } else if (StringUtils.isEquals(ownerWarehouseState, ZeroToNineEnum.ZERO.getCode())){
            state = "待通知";
        }else if (StringUtils.isEquals(ownerWarehouseState, ZeroToNineEnum.ONE.getCode())){
            state = "通知失败";
        }else if (StringUtils.isEquals(ownerWarehouseState, ZeroToNineEnum.TWO.getCode())){
            state = "取消通知";
        }else if (StringUtils.isEquals(ownerWarehouseState, ZeroToNineEnum.THREE.getCode())){
            state = "通知中";
        }else if (StringUtils.isEquals(ownerWarehouseState, ZeroToNineEnum.FOUR.getCode())){
            state = "通知成功";
        }
        return state;
    }

    @Override
    @WarehouseItemCacheEvict
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Response uploadNoticeStatus(InputStream uploadedInputStream, FormDataContentDisposition fileDetail,
                                       String warehouseInfoId, AclUserAccreditInfo aclUserAccreditInfo) {
        String fileName = fileDetail.getFileName();
        String userId = aclUserAccreditInfo.getUserId();
        AssertUtil.notBlank(userId, "获取当前登录的userId失败");
        AssertUtil.notBlank(fileName, "上传文件名称不能为空");
        AssertUtil.notBlank(warehouseInfoId, "仓库信息id不能为空");
        boolean flag = true;
        WarehouseItemInfoExceptionResult result = new WarehouseItemInfoExceptionResult();
        String code = "";
        try {
            //获取仓库信息详情
            WarehouseInfo warehouseInfo = this.getWarehouseInfo(Long.parseLong(warehouseInfoId));
            if(warehouseInfo == null){
                String msg = "仓库信息不存在";
                log.error(msg);
                throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
            }
            if(!StringUtils.isEquals(warehouseInfo.getOwnerWarehouseState(), ZeroToNineEnum.ONE.getCode())){
                String msg = "无法通知，“货主仓库状态”还不是“通知成功”！";
                log.error(msg);
                throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
            }

            //检测是否是excel
            String suffix = fileName.substring(fileName.lastIndexOf(SupplyConstants.Symbol.FILE_NAME_SPLIT) + 1);
            if (!(suffix.toLowerCase().equals(XLSX) || suffix.toLowerCase().equals(XLS))) {
                return ResultUtil.createfailureResult(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(), "导入文件格式不支持", "");
            }

            //校验导入文件抬头信息
            String[] titleResult = null;
            try{
                titleResult = ImportExcel.readExcelTitle(uploadedInputStream);
            }catch(Exception e){
                return ResultUtil.createfailureResult(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(), "导入模板错误!", "");
            }
            Map<String, Object> titleMapResult = this.checkTitle(titleResult);
            if (titleMapResult.containsKey(CODE)) {
                return ResultUtil.createfailureResult((Integer) titleMapResult.get(CODE), (String) titleMapResult.get(MSG), "");
            }

            //校验导入文件信息，并获取信息
            Map<String, String> contentResult = ImportExcel.readExcelContent(uploadedInputStream, SupplyConstants.Symbol.COMMA);
            if(StringUtils.isEquals("0", contentResult.get("count").toString())){
                return ResultUtil.createfailureResult(Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(), "导入附件不能为空！", "");
            }
            Map<String, Object> contentMapResult = this.checkContent(contentResult, warehouseInfoId, titleResult.length);
            String count = (String) contentMapResult.get("count");
            int countNum = Integer.parseInt(count);

            //将通知状态保存入数据库
            Map<String, Skus> importContent = (Map<String, Skus>) contentMapResult.get("importContent");
            int successCount = this.saveNoticeStatus(importContent, warehouseInfoId, titleResult.length, userId);
            int failCount = countNum - successCount;

            //将错误通知导入excel
            if (!(Boolean) contentMapResult.get("flag")) {
                flag = false;
                code = this.saveExcelException((Map<String, String>) contentMapResult.get("exceptionContent"), titleResult.length);
            }

            //构造返回参数
            result = new WarehouseItemInfoExceptionResult(code, String.valueOf(successCount), String.valueOf(failCount));

        } catch (Exception e) {
            String msg = e.getMessage();
            log.error(msg, e);
            return ResultUtil.createfailureResult(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), msg, "");
        }
        if(flag){
            return ResultUtil.createSuccessResult("导入仓库商品信息通知状态成功", result);
        }
        return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), "导入文件参数错误", result);
    }

    @Override
    @WarehouseItemCacheEvict
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public Response warehouseItemNoticeQimen(String itemIds, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notBlank(itemIds, "同步商品不能为空");
        String userId = aclUserAccreditInfo.getUserId();
        AssertUtil.notBlank(userId, "获取当前登录的userId失败");
        //获取同步itemId
        List<String> itemList = this.getItemList(itemIds);
        if(itemList == null){
            String msg = "同步商品不能为空";
            log.error(msg);
            throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
        }

        //获取商品详情
        List<WarehouseItemInfo> warehouseItemInfoList = this.getWarehouseItemInfos(itemList);
        if(warehouseItemInfoList == null || warehouseItemInfoList.size() < 1){
            String msg = "同步商品不能为空";
            log.error(msg);
            throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
        }

        //组装商品
        List<ScmWarehouseItem> itemsSynList = this.getItemsSynList(warehouseItemInfoList);

        if(itemsSynList.size() < 1){
            String msg = "同步商品不能为空";
            log.error(msg);
            throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
        }

        //获取仓库信息详情
        WarehouseInfo warehouseInfo = this.getWarehouseInfo(warehouseItemInfoList.get(0).getWarehouseInfoId());
        if(warehouseInfo == null){
            String msg = "仓库信息不存在";
            log.error(msg);
            throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
        }

        if(!StringUtils.isEquals(warehouseInfo.getOwnerWarehouseState(), ZeroToNineEnum.ONE.getCode())){
            String msg = "无法通知，“货主仓库状态”还不是“通知成功”！";
            log.error(msg);
            throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
        }

        for(WarehouseItemInfo info : warehouseItemInfoList){
            logInfoService.recordLog(info, info.getId().toString(), userId,
                    LogOperationEnum.NOTICE_WAREHOUSE.getMessage(), null, null);
        }

        //调用奇门接口
        ScmItemSyncRequest request = new ScmItemSyncRequest();
        request.setWarehouseItemList(itemsSynList);
        request.setOwnerCode(warehouseInfo.getWarehouseOwnerId());
        request.setActionType("add");
        AppResult<List<ScmItemSyncResponse>> appResult = warehouseApiService.itemSync(request);

        //解析接口
        if(org.apache.commons.lang3.StringUtils.equals(appResult.getAppcode(), SUCCESS)){
            List<ScmItemSyncResponse> res = (List<ScmItemSyncResponse>)appResult.getResult();
            if(res != null && res.size() > 0){
                this.updateWarehouseItemInfo(this.getSuccessItemId(warehouseInfo.getCode(), res,
                        warehouseInfo.getWarehouseName()), userId);
            }
            return ResultUtil.createSuccessResult("导入仓库商品信息通知状态成功", "");
        }
        for(WarehouseItemInfo info : warehouseItemInfoList){
            logInfoService.recordLog(info, info.getId().toString(), warehouseInfo.getWarehouseName(),
                    LogOperationEnum.NOTICE_FAIL.getMessage(), "失败原因:" + appResult.getDatabuffer(), null);
        }
        return ResultUtil.createfailureResult(Response.Status.BAD_REQUEST.getStatusCode(), appResult.getDatabuffer(), "");
    }

    private Map<String, String> getSuccessItemId(String warehouseCode, List<ScmItemSyncResponse> batchItemSynItems, String warehouseName){
        Map<String, String> itemMap = new HashMap<>();
        for(ScmItemSyncResponse synItem : batchItemSynItems){
            WarehouseItemInfo info = new WarehouseItemInfo();
            info.setWarehouseCode(warehouseCode);
            info.setSkuCode(synItem.getItemCode());
            info.setIsDelete(Integer.valueOf(ZeroToNineEnum.ZERO.getCode()));
            info = warehouseItemInfoService.selectOne(info);
            if( SUCCESS.equals(synItem.getCode())){
                itemMap.put(String.valueOf(info.getId()), synItem.getItemId());
            }else{
                info.setExceptionReason(synItem.getMessage());
                info.setNoticeStatus(Integer.parseInt(ZeroToNineEnum.ONE.getCode()));
                warehouseItemInfoService.updateByPrimaryKeySelective(info);
                logInfoService.recordLog(info, info.getId().toString(), warehouseName,
                        LogOperationEnum.NOTICE_FAIL.getMessage(), "失败原因:" + synItem.getMessage(), null);
            }
        }
        return itemMap;
    }

    private void updateWarehouseItemInfo(Map<String, String> itemMap, String userId){
        WarehouseInfo warehouseInfo = null;
        for (Map.Entry<String, String> entry : itemMap.entrySet()) {
            String itemId = entry.getKey().toString();
            String warehouseItemId = entry.getValue().toString();
            WarehouseItemInfo info = new WarehouseItemInfo();
            info.setId(Long.parseLong(itemId));
            info.setWarehouseItemId(warehouseItemId);
            info.setNoticeStatus(Integer.parseInt(ZeroToNineEnum.FOUR.getCode()));
            warehouseItemInfoService.updateByPrimaryKeySelective(info);
            info = warehouseItemInfoService.selectByPrimaryKey(Long.parseLong(itemId));
            if(warehouseInfo == null){
                warehouseInfo = warehouseInfoService.selectByPrimaryKey(info.getWarehouseInfoId());
            }
            logInfoService.recordLog(info, info.getId().toString(), warehouseInfo.getWarehouseName(),
                    LogOperationEnum.NOTICE_SUCCESS.getMessage(), null, null);
            this.updateSkuStock(info, warehouseInfo);
        }
    }

    private List<ScmWarehouseItem> getItemsSynList(List<WarehouseItemInfo> infoList){
        List<ScmWarehouseItem> list = new ArrayList<ScmWarehouseItem>();
        ScmWarehouseItem item = null;
        for(WarehouseItemInfo info : infoList){
            if(!(info.getNoticeStatus() == Integer.parseInt(ZeroToNineEnum.ZERO.getCode()) ||
                    info.getNoticeStatus() == Integer.parseInt(ZeroToNineEnum.ONE.getCode()) ||
                    info.getNoticeStatus() == Integer.parseInt(ZeroToNineEnum.TWO.getCode()))){
                continue;
            }

            item = new ScmWarehouseItem();

            Skus skus = new Skus();
            skus.setSkuCode(info.getSkuCode());
            skus = skusService.selectOne(skus);
            Items items = iItemsService.selectByPrimaryKey(skus.getItemId());
            String isquality = items.getIsQuality();
            Long days = items.getQualityDay();
            if(StringUtils.isEquals(isquality, ZeroToNineEnum.ONE.getCode()) && days > 0){
                item.setSaveDays(days.intValue());
                item.setInstoreThreshold(jDWmsConstantConfig.getInstoreThreshold());
                item.setOutstoreThreshold(jDWmsConstantConfig.getOutstoreThreshold());
            }else{
                item.setSaveDays(0);
            }
            item.setItemCode(info.getSkuCode());
            item.setGoodsCode(info.getItemNo());
            item.setItemName(info.getItemName());
            item.setBarCode(info.getBarCode());
            item.setSkuProperty(info.getSpecNatureInfo());
            item.setItemType(info.getItemType());
            item.setThirdCategoryNo(jDWmsConstantConfig.getThirdCategoryNo());

            list.add(item);
        }
        return list;
    }

    private WarehouseInfo getWarehouseInfo(Long warehouseInfoId){
        WarehouseInfo info = new WarehouseInfo();
        info.setId(warehouseInfoId);
        return warehouseInfoService.selectOne(info);
    }

    private List<WarehouseItemInfo> getWarehouseItemInfos(List<String> itemList){
        Example example = new Example(WarehouseItemInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id", itemList);
        List<WarehouseItemInfo> warehouseItemInfoList = warehouseItemInfoService.selectByExample(example);
        return warehouseItemInfoList;
    }

    private List<String> getItemList(String items){
        String[] itemArray = items.split(SupplyConstants.Symbol.COMMA);
        if(itemArray.length < 1){
            return null;
        }
        List<String> itemList = new ArrayList<String>();
        for(String s : itemArray){
            if(StringUtils.isNotEmpty(s)){
                itemList.add(s);
            }
        }
        if(itemList.size() < 1){
            return null;
        }
        return itemList;
    }

    private void saveExcel(ByteArrayOutputStream out, String fileName) throws Exception{
        FTPUtil sf = new FTPUtil();
        int port = 22;

        ByteArrayInputStream swapStream = new ByteArrayInputStream(out.toByteArray());
        sf.connect(HOST, port, USERNAME, PASSWORD);
        sf.upload(EXCEPTION_NOTICE_UPLOAD_ADDRESS, swapStream, fileName);
        sf.disconnect();
    }

    private int saveNoticeStatus(Map<String, Skus> allMap, String warehouseInfoId, int length, String userId) {
        WarehouseItemInfo warehouseItemInfo = null;
        long warehouseInfoIdLong = Long.parseLong(warehouseInfoId);
        WarehouseInfo warehouseInfo = warehouseInfoService.selectByPrimaryKey(warehouseInfoIdLong);
        Map<String, Object> map = new HashMap<>();
        List<WarehouseItemInfo> addList = new ArrayList<>();
        List<WarehouseItemInfo> updateList = new ArrayList<>();
        List<String> skusList = new ArrayList<>();
        int count = 0;
        for(Map.Entry<String, Skus> entry : allMap.entrySet()){
            skusList.add(entry.getKey().split(SupplyConstants.Symbol.COMMA)[0]);
        }
        if(skusList.size() < 1){
            return 0;
        }
        Map<String,String> itemNoMap = getItemNoBySku(skusList);
        String warehouseCode =  warehouseInfo.getCode();
        String warehouseOwnerId = warehouseInfo.getWarehouseOwnerId();
        String wmsWarehouseCode = warehouseInfo.getWmsWarehouseCode();
        String operationalNature = warehouseInfo.getOperationalNature();
        String logOperation = "";

        if(length == Integer.parseInt(ZeroToNineEnum.ONE.getCode())){
            for(Map.Entry<String, Skus> entry : allMap.entrySet()){
                warehouseItemInfo = new WarehouseItemInfo();
                assembleWarehouseItemInfo(warehouseItemInfo, entry.getValue(), warehouseInfoIdLong, warehouseCode,
                        warehouseOwnerId, wmsWarehouseCode, itemNoMap, operationalNature);
                addList.add(warehouseItemInfo);
            }
            count = warehouseItemInfoService.insertList(addList);
            logOperation = LogOperationEnum.IMPORT_ORDER.getMessage();
            this.saveSkuStock(addList, warehouseInfo);
        }else if(length == Integer.parseInt(ZeroToNineEnum.TWO.getCode())){
            for(Map.Entry<String, Skus> entry : allMap.entrySet()){
                String[] str = entry.getKey().split(SupplyConstants.Symbol.COMMA);
                if(StringUtils.isEquals("new", str[2])){
                    warehouseItemInfo = new WarehouseItemInfo();
                    assembleWarehouseItemInfo(warehouseItemInfo, entry.getValue(), warehouseInfoIdLong, warehouseCode,
                            warehouseOwnerId, wmsWarehouseCode, itemNoMap, null);
                    warehouseItemInfo.setWarehouseItemId(str[1]);
                    warehouseItemInfo.setNoticeStatus(Integer.parseInt(WarehouseItemInfoNoticeStateEnum.NOTICE_SUCCESS.getCode()));
                    addList.add(warehouseItemInfo);
                }else{
                    warehouseItemInfo = new WarehouseItemInfo();
                    warehouseItemInfo.setSkuCode(str[0]);
                    warehouseItemInfo.setWarehouseItemId(str[1]);
                    updateList.add(warehouseItemInfo);
                }
            }

            if(updateList.size() > 0){
                map.put("warehouseInfoId", warehouseInfoId);
                map.put("arrSkus", updateList);
                //更新仓库商品信息
                count = count + warehouseItemInfoService.batchUpdate(map);
                //更新库存
                map.put("warehouseId", warehouseInfo.getId());
                map.put("channelCode", warehouseInfo.getChannelCode());
                skuStockService.batchUpdate(map);
            }
            if(addList.size() > 0){
                count = count + warehouseItemInfoService.insertList(addList);
                saveSkuStockIsNotice(addList, warehouseInfo);
            }
            logOperation = LogOperationEnum.IMPORT_ITEM_ID.getMessage();
        }

        //保存日志信息
        Example example = new Example(WarehouseItemInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("skuCode", skusList);
        criteria.andEqualTo("warehouseCode", warehouseCode);
        criteria.andEqualTo("isDelete", 0);
        String warehouseName = warehouseInfo.getWarehouseName();
        List<WarehouseItemInfo> warehouseItemInfoList = warehouseItemInfoService.selectByExample(example);
        List<String> warehouseItemIds = new ArrayList<>();
        for(WarehouseItemInfo warehouseItemInfoAdd : warehouseItemInfoList){
            warehouseItemIds.add(warehouseItemInfoAdd.getId().toString());
        }
        logInfoService.recordLogs(new WarehouseItemInfo(), userId, logOperation, null, null, warehouseItemIds);
        if(operationalNature != null &&
                StringUtils.isEquals(OperationalNatureEnum.SELF_SUPPORT.getCode(), operationalNature)){
            logInfoService.recordLogs(new WarehouseItemInfo(), warehouseName, LogOperationEnum.NOTICE_SUCCESS.getMessage(),
                    null, null, warehouseItemIds);
        }

        return count;
    }

    private void saveSkuStockIsNotice(List<WarehouseItemInfo> list, WarehouseInfo warehouseInfo){
        List<SkuStock> skuStockList = new ArrayList<SkuStock>();
        SkuStock skuStock = null;
        for(WarehouseItemInfo warehouseItemInfo : list){
            skuStock = new SkuStock();
            skuStock.setSpuCode(warehouseItemInfo.getSpuCode());
            skuStock.setSkuCode(warehouseItemInfo.getSkuCode());
            skuStock.setChannelCode(warehouseInfo.getChannelCode());
            skuStock.setWarehouseId(warehouseInfo.getId());
            skuStock.setWarehouseCode(warehouseInfo.getCode());
            skuStock.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
            skuStock.setCreateTime(Calendar.getInstance().getTime());
            skuStock.setUpdateTime(Calendar.getInstance().getTime());
            skuStock.setIsValid(ZeroToNineEnum.ONE.getCode());
            skuStock.setAvailableInventory(0L);
            skuStock.setAvailableDefectiveInventory(0L);
            skuStock.setWarehouseItemId(warehouseItemInfo.getWarehouseItemId());
            skuStock.setLockInventory(0L);
            skuStock.setAirInventory(0L);
            skuStock.setFrozenInventory(0L);
            skuStock.setRealInventory(0L);
            skuStock.setDefectiveInventory(0L);
            skuStockList.add(skuStock);
        }
        skuStockService.insertList(skuStockList);
    }

    private void assembleWarehouseItemInfo(WarehouseItemInfo warehouseItemInfo, Skus skus, long warehouseInfoId,
                                           String warehouseCode, String warehouseOwnerId, String wmsWarehouseCode,
                                           Map<String,String> itemNoMap, String operationalNature){
        warehouseItemInfo.setWarehouseInfoId(warehouseInfoId);
        warehouseItemInfo.setWarehouseItemId(String.valueOf(skus.getItemId()));
        warehouseItemInfo.setSkuCode(skus.getSkuCode());
        warehouseItemInfo.setItemName(skus.getSkuName());
        warehouseItemInfo.setSpecNatureInfo(skus.getSpecInfo());
        warehouseItemInfo.setIsValid(Integer.valueOf(ZeroToNineEnum.ONE.getCode()));
        warehouseItemInfo.setWarehouseItemId(null);
        warehouseItemInfo.setNoticeStatus(NoticsWarehouseStateEnum.UN_NOTICS.getCode());
        warehouseItemInfo.setBarCode(skus.getBarCode());
        warehouseItemInfo.setIsDelete(Integer.valueOf(ZeroToNineEnum.ZERO.getCode()));
        warehouseItemInfo.setSpuCode(skus.getSpuCode());
        warehouseItemInfo.setItemNo(itemNoMap.get(skus.getSkuCode()));
        warehouseItemInfo.setItemType(ItemTypeEnum.NOEMAL.getCode());
        warehouseItemInfo.setWarehouseCode(warehouseCode);
        warehouseItemInfo.setWarehouseOwnerId(warehouseOwnerId);
        warehouseItemInfo.setWmsWarehouseCode(wmsWarehouseCode);
        if(operationalNature != null && StringUtils.isEquals(OperationalNatureEnum.SELF_SUPPORT.getCode(), operationalNature)){
            warehouseItemInfo.setNoticeStatus(NoticsWarehouseStateEnum.SUCCESS.getCode());
            warehouseItemInfo.setWarehouseItemId(skus.getSkuCode());
        }
    }

    private void updateSkuStock(WarehouseItemInfo warehouseItemInfo, WarehouseInfo warehouseInfo){
        SkuStock skuStock = new SkuStock();
        skuStock.setSkuCode(warehouseItemInfo.getSkuCode());
        skuStock.setWarehouseId(warehouseInfo.getId());
        skuStock.setChannelCode(warehouseInfo.getChannelCode());
        skuStock.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        skuStock = skuStockService.selectOne(skuStock);
        skuStock.setIsValid(ZeroToNineEnum.ONE.getCode());
        skuStock.setWarehouseItemId(warehouseItemInfo.getWarehouseItemId());
        skuStock.setUpdateTime(Calendar.getInstance().getTime());
        skuStockService.updateByPrimaryKey(skuStock);
    }

    private String saveExcelException(Map<String, String> map, int length) throws Exception{
        //获取所有异常信息
        List<WarehouseItemInfoException> warehouseItemInfoExceptionList = this.getWarehouseItemInfoExceptionList(map, length);
        //获取code
        String code = serialUtilService.generateCode(SupplyConstants.Serial.SKU_LENGTH, "EXCEL",
                "0", DateUtils.dateToCompactString(Calendar.getInstance().getTime()));

        List<ExcelException> list = new ArrayList<>();
        for(WarehouseItemInfoException warehouseItemInfoException : warehouseItemInfoExceptionList){
            ExcelException e = new ExcelException();
            e.setExcelCode(code);
            e.setException(warehouseItemInfoException.getExceptionReason());
            e.setItemId(warehouseItemInfoException.getItemId());
            e.setSkuCode(warehouseItemInfoException.getSkuCode());
            if(length == Integer.parseInt(ZeroToNineEnum.ONE.getCode())){
                e.setType(ZeroToNineEnum.ONE.getCode());
            }else{
                e.setType(ZeroToNineEnum.TWO.getCode());
            }
            list.add(e);
        }
        excelExceptionService.insertList(list);
        return code;
    }

    @Override
    public Response exportItemNoticeException(String excelCode) {
        AssertUtil.notBlank(excelCode, "查询信息编码不能为空");
        try {
            ExcelException e = new ExcelException();
            e.setExcelCode(excelCode);
            List<ExcelException> list = excelExceptionService.select(e);
            List<CellDefinition> cellDefinitionList = new ArrayList<>();
            if(StringUtils.isEquals(list.get(0).getType(), ZeroToNineEnum.ONE.getCode())){
                CellDefinition skuCode = new CellDefinition("skuCode", TITLE_ONE, CellDefinition.TEXT, null, 8000);
                CellDefinition exception = new CellDefinition("exception", TITLE_THREE, CellDefinition.TEXT, null, 8000);

                cellDefinitionList.add(skuCode);
                cellDefinitionList.add(exception);
            }else{
                CellDefinition skuCode = new CellDefinition("skuCode", TITLE_ONE, CellDefinition.TEXT, null, 8000);
                CellDefinition itemId = new CellDefinition("itemId", TITLE_TWO, CellDefinition.TEXT, null, 8000);
                CellDefinition exception = new CellDefinition("exception", TITLE_THREE, CellDefinition.TEXT, null, 8000);

                cellDefinitionList.add(skuCode);
                cellDefinitionList.add(itemId);
                cellDefinitionList.add(exception);
            }

            String sheetName = "仓库商品信息异常原因";

            HSSFWorkbook hssfWorkbook = ExportExcel.generateExcel(list, cellDefinitionList, sheetName);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            hssfWorkbook.write(stream);

            String fileName = String.valueOf(System.nanoTime())+ SupplyConstants.Symbol.FILE_NAME_SPLIT + XLS;
            return javax.ws.rs.core.Response.ok(stream.toByteArray()).header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename*=utf-8'zh_cn'" + fileName).type(MediaType.APPLICATION_OCTET_STREAM)
                    .header("Cache-Control", "no-cache").build();
        } catch (Exception e) {
            log.error("供应商订单导出异常" + e.getMessage(), e);
            return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.SUPPLIER_ORDER_EXPORT_EXCEPTION.getCode()), ExceptionEnum.SUPPLIER_ORDER_EXPORT_EXCEPTION.getMessage());
        }
    }
    
    /**
     * @Description: 查询退货仓库map集合
     * @Author: hzluoxingcheng
     * @Date: 2018/8/30
     */ 
    @Override
    public Response selectReturnWarehouse() {
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isSupportReturn",1);
        List<WarehouseInfo> list = warehouseInfoService.selectByExample(example);
        List<Map<String,String>> rev = new ArrayList<>();
        for (WarehouseInfo warehouse:list){
            Map<String,String> map = new HashMap<>();
            map.put("name",warehouse.getWarehouseName());
            map.put("code",warehouse.getCode());
            rev.add(map);
        }
        return ResultUtil.createSuccessResult("获取仓库名称成功",rev);
    }

//    private ByteArrayOutputStream saveExceptionExcel(Map<String, String> map, String fileName) throws IOException {
//        //获取所有异常信息
//        List<WarehouseItemInfoException> warehouseItemInfoExceptionList = this.getWarehouseItemInfoExceptionList(map);
//
//        CellDefinition skuCode = new CellDefinition("skuCode", TITLE_ONE, CellDefinition.TEXT, 8000);
//        CellDefinition itemId = new CellDefinition("itemId", TITLE_TWO, CellDefinition.TEXT, 8000);
//        CellDefinition exceptionReason = new CellDefinition("exceptionReason", TITLE_THREE, CellDefinition.TEXT, 8000);
//
//        List<CellDefinition> cellDefinitionList = new ArrayList<>();
//        cellDefinitionList.add(skuCode);
//        cellDefinitionList.add(itemId);
//        cellDefinitionList.add(exceptionReason);
//
//        String sheetName = "仓库商品信息异常原因";
//        //fileName = "仓库商品信息异常原因" + fileName;
//        //fileName = fileName + SupplyConstants.Symbol.FILE_NAME_SPLIT + XLS;
//        //String downloadAddress = EXCEPTION_NOTICE_UPLOAD_ADDRESS + fileName;
//
//        HSSFWorkbook hssfWorkbook = ExportExcel.generateExcel(warehouseItemInfoExceptionList, cellDefinitionList, sheetName);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        hssfWorkbook.write(stream);
//        return stream;
//    }

    private List<WarehouseItemInfoException> getWarehouseItemInfoExceptionList(Map<String, String> map, int length) {
        String value = null;
        String[] values = null;
        List<WarehouseItemInfoException> list = new ArrayList<WarehouseItemInfoException>();
        WarehouseItemInfoException warehouseItemInfoException = null;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            value = entry.getValue();
            values = value.split(SupplyConstants.Symbol.COMMA);
            warehouseItemInfoException = new WarehouseItemInfoException();
            if(length == Integer.parseInt(ZeroToNineEnum.ONE.getCode())){
                warehouseItemInfoException.setExceptionReason(values[2]);
                warehouseItemInfoException.setSkuCode(values[0]);
            }else{
                warehouseItemInfoException.setExceptionReason(values[3]);
                warehouseItemInfoException.setItemId(values[1]);
                warehouseItemInfoException.setSkuCode(values[0]);
            }
            list.add(warehouseItemInfoException);
        }
        return list;
    }

    private Map<String, Object> checkContent(Map<String, String> map, String warehouseInfoId, int length) {
        Map<String, Object> returnMap = new HashMap<>();
        Map<String, String> exceptionContent = new HashMap<>();
        Map<String, Skus> importContent = new HashMap<>();
        boolean flag = true;
        String value = null;
        String[] values = null;
        String skuCode = null;
        WarehouseItemInfo warehouseItemInfo = null;
        Skus skus = null;
        String count = null;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            value = entry.getValue();
            if (ImportExcel.COUNT.equals(key)) {
                count = value;
                continue;
            }
            values = value.split(SupplyConstants.Symbol.COMMA);
            if(length == 2){
                if (StringUtils.isEmpty(values[0])) {
                    flag = false;
                    value += ",商品SKU编号不能为空！";
                    exceptionContent.put(key, value);
                    continue;
                }
                if (StringUtils.isEmpty(values[1])) {
                    flag = false;
                    value += ",仓库商品ID不能为空！";
                    exceptionContent.put(key, value);
                    continue;
                }

                skuCode = values[0];
                skus = new Skus();
                skus.setSkuCode(skuCode);
                skus = skusService.selectOne(skus);
                if(skus == null){
                    flag = false;
                    value += ",请导入自采商品！";
                    exceptionContent.put(key, value);
                    continue;
                }

                if(StringUtils.isEquals(skus.getIsValid(), ZeroToNineEnum.ZERO.getCode())){
                    flag = false;
                    value += ",商品已停用！";
                    exceptionContent.put(key, value);
                    continue;
                }

                warehouseItemInfo = new WarehouseItemInfo();
                warehouseItemInfo.setWarehouseInfoId(Long.valueOf(warehouseInfoId));
                warehouseItemInfo.setSkuCode(skuCode);
                warehouseItemInfo.setIsDelete(Integer.valueOf(ZeroToNineEnum.ZERO.getCode()));
                List<WarehouseItemInfo> warehouseItemInfoList = warehouseItemInfoService.select(warehouseItemInfo);
                if (warehouseItemInfoList != null && warehouseItemInfoList.size() > 0) {
                    Integer oldNoticeStatus = warehouseItemInfoList.get(0).getNoticeStatus();
                    if(oldNoticeStatus == Integer.parseInt(WarehouseItemInfoNoticeStateEnum.NOTICE_.getCode()) ||
                        oldNoticeStatus == Integer.parseInt(WarehouseItemInfoNoticeStateEnum.NOTICE_CANCEL.getCode())){
                        flag = false;
                        value += ",当前通知仓库状态不允许导入！";
                        exceptionContent.put(key, value);
                        continue;
                    }
                }

                if(this.isExeistsQimenItemId(Long.valueOf(warehouseInfoId), values[1])){
                    flag = false;
                    value += ",该仓库商品ID已存在！";
                    exceptionContent.put(key, value);
                    continue;
                }

                if(warehouseItemInfoList != null && warehouseItemInfoList.size() > 0){
                    importContent.put(values[0] + SupplyConstants.Symbol.COMMA + values[1] +
                            SupplyConstants.Symbol.COMMA + "old", skus);
                }else{
                    importContent.put(values[0] + SupplyConstants.Symbol.COMMA + values[1] +
                            SupplyConstants.Symbol.COMMA + "new", skus);
                }
            }else{
                if (StringUtils.isEmpty(values[0])) {
                    flag = false;
                    value += ",商品SKU编号不能为空！";
                    exceptionContent.put(key, value);
                    continue;
                }
                skuCode = values[0];
                skus = new Skus();
                skus.setSkuCode(skuCode);
                skus = skusService.selectOne(skus);
                if(skus == null){
                    flag = false;
                    value += ",请导入自采商品！";
                    exceptionContent.put(key, value);
                    continue;
                }

                if(StringUtils.isEquals(skus.getIsValid(), ZeroToNineEnum.ZERO.getCode())){
                    flag = false;
                    value += ",商品已停用！";
                    exceptionContent.put(key, value);
                    continue;
                }

                warehouseItemInfo = new WarehouseItemInfo();
                warehouseItemInfo.setWarehouseInfoId(Long.valueOf(warehouseInfoId));
                warehouseItemInfo.setSkuCode(skuCode);
                warehouseItemInfo.setIsDelete(Integer.valueOf(ZeroToNineEnum.ZERO.getCode()));
                List<WarehouseItemInfo> warehouseItemInfoList = warehouseItemInfoService.select(warehouseItemInfo);
                if (warehouseItemInfoList != null && warehouseItemInfoList.size() > 0) {
                    flag = false;
                    value += ",商品已存在！";
                    exceptionContent.put(key, value);
                    continue;
                }
                importContent.put(values[0], skus);
            }
        }
        returnMap.put("importContent", importContent);
        returnMap.put("exceptionContent", exceptionContent);
        returnMap.put("count", count);
        returnMap.put("flag", flag);
        return returnMap;
    }

    //是否存在重复
    private boolean isExeistsQimenItemId(Long warehouseInfoId, String warehouseItemId){
        WarehouseItemInfo warehouseItemInfo = new WarehouseItemInfo();
        warehouseItemInfo.setWarehouseInfoId(warehouseInfoId);
        warehouseItemInfo.setWarehouseItemId(warehouseItemId);
        List<WarehouseItemInfo> warehouseItemInfoList = warehouseItemInfoService.select(warehouseItemInfo);
        if(warehouseItemInfoList != null && warehouseItemInfoList.size() > 0){
            return true;
        }
        return false;
    }

    private Map<String, Object> checkTitle(String[] titleResult) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (titleResult.length == Integer.valueOf(ZeroToNineEnum.TWO.getCode()) &&
                (!TITLE_ONE.equals(titleResult[0]) || !TITLE_TWO.equals(titleResult[1]))) {
            this.putMapResult(map, Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(), "导入文件参数错误", "");
        }else if(titleResult.length == Integer.valueOf(ZeroToNineEnum.ONE.getCode()) &&
                !TITLE_ONE.equals(titleResult[0])){
            this.putMapResult(map, Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(), "导入文件参数错误", "");
        }else if(titleResult.length != Integer.valueOf(ZeroToNineEnum.TWO.getCode()) &&
                titleResult.length != Integer.valueOf(ZeroToNineEnum.ONE.getCode())){
            this.putMapResult(map, Response.Status.UNSUPPORTED_MEDIA_TYPE.getStatusCode(), "导入文件参数错误", "");
        }
        return map;
    }

    private void putMapResult(Map<String, Object> map, Integer code, String msg, String url) {
        map.put(CODE, code);
        map.put(MSG, msg);
        map.put(URL, url);
    }





    @Override
    @WarehouseCacheEvict
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response saveOwnerInfo(WarehouseInfo warehouseInfo, AclUserAccreditInfo aclUserAccreditInfo){
//        AssertUtil.notBlank(warehouseInfo.getOwnerName(),"货主姓名不能为空");
        AssertUtil.notNull(warehouseInfo.getId(),"主键不能为空");
        WarehouseInfo warehouse = warehouseInfoService.selectByPrimaryKey(Long.valueOf(warehouseInfo.getId()));
        Integer isNoticeSuccess = warehouse.getIsNoticeSuccess();
        if (isNoticeSuccess == null){
            isNoticeSuccess =0;
        }
        if (isNoticeSuccess.equals(NoticeSuccessEnum.UN_NOTIC.getCode())){
            warehouseInfo.setOwnerWarehouseState(ZeroToNineEnum.ONE.getCode());
            int cout = warehouseInfoService.updateByPrimaryKeySelective(warehouseInfo);
            if (cout==0){
                log.error("保存货主信息失败");
                String msg = "保存货主信息失败";
                throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
            }
        }else {
            log.info("不符合保存操作");
            String msg = "不符合保存操作";
            throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
        }
        String userId = aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(new WarehouseOwner(), warehouse.getId().toString(), userId, LogOperationEnum.UPDATE.getMessage(), null, null);
        return ResultUtil.createSuccessResult("保存货主信息成功","success");
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response deleteWarehouse(String id){
        AssertUtil.notBlank(id,"主键不能为空");
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",id);
        WarehouseInfo warehouseInfo = new WarehouseInfo();
        warehouseInfo.setIsDeleted(ZeroToNineEnum.ONE.getCode());
        int cout = warehouseInfoService.updateByExampleSelective(warehouseInfo,example);
        if (cout==0){
            log.error("删除仓库信息失败");
            String msg = "删除从库信息失败";
            throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
        }
        return ResultUtil.createSuccessResult("删除仓库信息成功","success");
    }

    public void setWmsService(IWarehouseApiService service) {
        this.warehouseApiService = service;
    }


}
