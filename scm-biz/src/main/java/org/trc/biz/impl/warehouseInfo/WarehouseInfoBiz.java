package org.trc.biz.impl.warehouseInfo;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.trc.biz.category.ICategoryBiz;
import org.trc.biz.warehouseInfo.IWarehouseInfoBiz;
import org.trc.cache.CacheEvit;
import org.trc.cache.Cacheable;
import org.trc.domain.System.Warehouse;
import org.trc.domain.category.Brand;
import org.trc.domain.category.Category;
import org.trc.domain.goods.Items;
import org.trc.domain.goods.Skus;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.WarehouseInfo;
import org.trc.enums.*;
import org.trc.domain.warehouseInfo.WarehouseItemInfo;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ValidStateEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.ConfigException;
import org.trc.exception.WarehouseInfoException;
import org.trc.form.liangyou.LyStatement;
import org.trc.form.warehouseInfo.*;
import org.trc.service.System.IWarehouseService;
import org.trc.service.category.IBrandService;
import org.trc.service.category.ICategoryService;
import org.trc.service.goods.IItemsService;
import org.trc.service.goods.ISkusService;
import org.trc.service.warehouseInfo.IWarehouseInfoService;
import org.trc.service.warehouseInfo.IWarehouseItemInfoService;
import org.trc.util.*;
import tk.mybatis.mapper.entity.Example;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created by wangyz on 2017/11/15.
 */
@Service("warehouseInfoBiz")
public class WarehouseInfoBiz implements IWarehouseInfoBiz {

    private Logger log = LoggerFactory.getLogger(WarehouseInfoBiz.class);

    @Autowired
    private IWarehouseService warehouseService;

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
    private ICategoryBiz categoryBiz;

    //错误信息
    public final static String BAR = "-";

    //错误信息
    public final static String EXCEL = ".xls";

    @Override
    public Response saveWarehouse(String code,AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notBlank(code,"奇门仓库编号不能为空");
        log.info("查询符合条件的仓库=====》");
        Example example = new Example(Warehouse.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("code", code);
        List<Warehouse> list = warehouseService.selectByExample(example);
        if (list.size()>1){
            log.info("一个奇门仓库编号取到多条数据");
        }
        Warehouse warehouse = list.get(0);
        WarehouseInfo warehouseInfo = new WarehouseInfo();
        warehouseInfo.setWarehouseName(warehouse.getName());
        warehouseInfo.setType(warehouse.getWarehouseTypeCode());
        warehouseInfo.setQimenWarehouseCode(warehouse.getQimenWarehouseCode());
        //warehouseInfo.setSkuNum();
        warehouseInfo.setOwnerId(aclUserAccreditInfo.getChannelCode());

        if (warehouse.getIsNoticeSuccess()!=null && warehouse.getIsNoticeSuccess().equals(NoticeSuccessEnum.NOTIC.getCode())){
            //调用仓库接口获取仓库货主ID
            //warehouseInfo.setWarehouseOwnerId();
        }else {
            warehouseInfo.setWarehouseOwnerId(null);
        }
        warehouseInfo.setOwnerName(aclUserAccreditInfo.getChannelName());
        warehouseInfo.setOwnerWarehouseState(WarehouseStateEnum.UN_NOTIC.getCode());
        warehouseInfo.setIsDelete(Integer.valueOf(ZeroToNineEnum.ZERO.getCode()));
        log.info("保存仓库到数据库=====》");
        try {
            int count = warehouseInfoService.insert(warehouseInfo);
            if (count == 0) {
                String msg = "仓库信息管理添加新仓库到数据库失败";
                log.error(msg);
                throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
            }
            log.info("《===========保存到数据库成功");
            return ResultUtil.createSuccessResult("保存仓库成功","success");
        }catch (DuplicateKeyException e){
            log.error("重复插入仓库到数据失败，开始更新数据库",e);
            Example example1 = new Example(WarehouseInfo.class);
            Example.Criteria criteria1 = example1.createCriteria();
            criteria1.andEqualTo("qimenWarehouseCode",warehouse.getQimenWarehouseCode());
            warehouseInfoService.updateByExampleSelective(warehouseInfo,example1);
            return ResultUtil.createSuccessResult("更新仓库成功","success");
        }

    }

    @Override
    public Response selectWarehouseNotInLocation() {
        //1、首先查出本地存在的仓库
        log.info("查询符合条件的仓库===》");
        Example example1 = new Example(WarehouseInfo.class);
        Example.Criteria criteria1 = example1.createCriteria();
        criteria1.andEqualTo("isDelete",ZeroToNineEnum.ZERO.getCode());
        List<WarehouseInfo> resultList = warehouseInfoService.selectByExample(example1);
        List<String> warehouseCodeList = new ArrayList<>();
        for (WarehouseInfo warehouseInfo:resultList){
            warehouseCodeList.add(warehouseInfo.getQimenWarehouseCode());
        }
        //2、查出我们未被添加的仓库
        log.info("去除已经添加的仓库=========》");
        Example example = new Example(Warehouse.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isValid", ValidStateEnum.ENABLE.getCode());
        if (warehouseCodeList.size()!=0){
            criteria.andNotIn("qimenWarehouseCode",warehouseCodeList);
        }
        List<Warehouse> list = warehouseService.selectByExample(example);
        List<Map<String,String>> rev = new ArrayList<>();
        for (Warehouse warehouse:list){
            Map<String,String> map = new HashMap<>();
            map.put("name",warehouse.getName());
            map.put("code",warehouse.getCode());
            rev.add(map);
        }
        log.info("《==========返回符合条件的仓库名称");
        return ResultUtil.createSuccessResult("获取仓库名称成功",rev);
    }

    @Override
    public Response selectWarehouse() {
        //1、首先查出所有的启动仓库
        log.info("开始查询启用的仓库====》");
        Example example = new Example(Warehouse.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isValid", ValidStateEnum.ENABLE.getCode());
        List<Warehouse> list = warehouseService.selectByExample(example);
        List<Map<String,String>> rev = new ArrayList<>();
        for (Warehouse warehouse:list){
            Map<String,String> map = new HashMap<>();
            map.put("name",warehouse.getName());
            map.put("code",warehouse.getCode());
            rev.add(map);
        }
        log.info("<======返回仓库名称");
        return ResultUtil.createSuccessResult("获取仓库名称成功",rev);
    }

    @Override
    public Pagenation<WarehouseInfoResult> selectWarehouseInfoByPage(WarehouseInfoForm query, Pagenation<WarehouseInfo> page) {
        AssertUtil.notNull(page.getPageNo(),"分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(),"分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(),"分页查询参数start不能为空");
        log.info("开始查询符合条件的仓库信息===========》");
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isBlank(query.getWarehouseName())){
            criteria.andLike("warehouseName","%"+query.getWarehouseName()+"%");
        }
        example.orderBy("createTime").desc();
        Pagenation<WarehouseInfo> pagenation = warehouseInfoService.pagination(example,page,query);
        log.info("《==========查询结束，开始组装返回结果");
        List<WarehouseInfo> list = pagenation.getResult();
        List<WarehouseInfoResult> newList = new ArrayList<>();
        for (WarehouseInfo warehouseInfo:list){
            WarehouseInfoResult result = new WarehouseInfoResult();
            result.setId(warehouseInfo.getId());
            result.setWarehouseId(warehouseInfo.getWarehouseId());
            result.setWarehouseName(warehouseInfo.getWarehouseName());
            result.setType(warehouseInfo.getType());
            result.setQimenWarehouseCode(warehouseInfo.getQimenWarehouseCode());
            result.setSkuNum(warehouseInfo.getSkuNum());
            String state = convertWarehouseState(warehouseInfo.getOwnerWarehouseState());
            result.setOwnerWarehouseState(state);
            result.setCreateTime(DateUtils.formatDateTime(warehouseInfo.getCreateTime()));
            result.setUpdateTime(DateUtils.formatDateTime(warehouseInfo.getUpdateTime()));
            result.setIsDelete(convertDeleteState(warehouseInfo));
            result.setOwnerId(warehouseInfo.getOwnerId());
            result.setOwnerName(warehouseInfo.getOwnerName());
            result.setWarehouseOwnerId(warehouseInfo.getWarehouseOwnerId());
            result.setRemark(warehouseInfo.getRemark());
            newList.add(result);
        }

        Pagenation<WarehouseInfoResult> resultPagenation = new Pagenation<>();
        resultPagenation.setResult(newList);
        resultPagenation.setPageNo(pagenation.getPageNo());
        resultPagenation.setPageSize(pagenation.getPageSize());
        resultPagenation.setTotalCount(pagenation.getTotalCount());
        resultPagenation.setStart(pagenation.getStart());
        log.info("组装数据完成《=============");
        return resultPagenation;
    }

    @Override
    @Cacheable(key = "#form.toString()+#page.pageNo+#page.pageSize+#warehouseInfoId", isList = true)
    public Pagenation<WarehouseItemInfo> queryWarehouseItemInfoPage(WarehouseItemInfoForm form, Long warehouseInfoId, Pagenation<WarehouseItemInfo> page) {
        AssertUtil.notNull(form, "查询仓库商品信息分页参数form不能为空");
        AssertUtil.notNull(warehouseInfoId, "查询仓库商品信息分页参数warehouseInfoId不能为空");
        AssertUtil.notNull(page.getPageNo(), "分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(), "分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(), "分页查询参数start不能为空");

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
        criteria.andEqualTo("isDeleted", "0");
        example.orderBy("noticeStatus").asc();
        example.orderBy("updateTime").desc();
        page = warehouseItemInfoService.pagination(example, page, form);
        log.info("《==========查询结束，开始组装返回结果");
        return page;
    }

    @Override
    @CacheEvit
    public void deleteWarehouseItemInfoById(Long id) {
        AssertUtil.notNull(id, "仓库商品信息ID不能为空");
        WarehouseItemInfo tmp = new WarehouseItemInfo();
        tmp.setId(id);
        tmp.setIsDelete(Integer.valueOf(ZeroToNineEnum.ONE.getCode()));
        tmp.setUpdateTime(Calendar.getInstance().getTime());
        int count = warehouseItemInfoService.updateByPrimaryKeySelective(tmp);
        if (count == 0) {
            String msg = CommonUtil.joinStr("根据主键ID[id=", id.toString(), "]删除仓库商品信息失败").toString();
            log.error(msg);
            throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
        }
    }

    @Override
    @CacheEvit
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
            CellDefinition skuCode = new CellDefinition("skuCode", "商品SKU编号", CellDefinition.TEXT, 4000);
            CellDefinition itemName = new CellDefinition("itemName", "商品SKU名称", CellDefinition.TEXT, 4000);
            CellDefinition specNatureInfo = new CellDefinition("specNatureInfo", "规格", CellDefinition.TEXT, 4000);
            CellDefinition isValid = new CellDefinition("isValid", "商品状态", CellDefinition.TEXT, 4000);
            CellDefinition warehouseItemId = new CellDefinition("warehouseItemId", "仓库商品ID", CellDefinition.TEXT, 4000);
            CellDefinition noticeStatus = new CellDefinition("noticeStatus", "通知仓库状态", CellDefinition.TEXT, 4000);
            CellDefinition updateTime = new CellDefinition("updateTime", "最近更新时间", CellDefinition.TEXT, 4000);

            List<CellDefinition> cellDefinitionList = new ArrayList<>();
            cellDefinitionList.add(skuCode);
            cellDefinitionList.add(itemName);
            cellDefinitionList.add(specNatureInfo);
            cellDefinitionList.add(isValid);
            cellDefinitionList.add(warehouseItemId);
            cellDefinitionList.add(noticeStatus);
            cellDefinitionList.add(updateTime);
            String sheetName = "仓库信息管理-商品信息报表";
            String fileName = "仓库信息管理-商品信息报表-" + form.getStartDate() + BAR + form.getEndDate() + EXCEL;
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
    public Response saveWarehouseItemsSku(List<Skus> itemsList,Long warehouseInfoId) {
        AssertUtil.notNull(warehouseInfoId,"仓库的主键不能为空");
        if (itemsList.size()==0){
            log.info("至少选择一件商品");
            return ResultUtil.createfailureResult(Integer.parseInt(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION.getCode()),"至少选择一件商品");
        }
        List<WarehouseItemInfo> list = new ArrayList<>();
        Warehouse warehouse = warehouseService.selectByPrimaryKey(warehouseInfoId);
        String warehouseItemId = null;
        if (warehouse.getIsNoticeSuccess()!=null && warehouse.getIsNoticeSuccess().equals(NoticeSuccessEnum.NOTIC.getCode())){
            //对接仓库后添加

        }
        for (Skus sku:itemsList){
            WarehouseItemInfo warehouseItemInfo = new WarehouseItemInfo();
            warehouseItemInfo.setSkuCode(sku.getSkuCode());
            warehouseItemInfo.setItemName(sku.getSkuName());
            warehouseItemInfo.setSpecNatureInfo(sku.getSpecInfo());
            warehouseItemInfo.setIsValid(Integer.valueOf(sku.getIsValid()));
            warehouseItemInfo.setWarehouseItemId(warehouseItemId);
            warehouseItemInfo.setNoticeStatus(NoticsWarehouseStateEnum.UN_NOTICS.getCode());
            list.add(warehouseItemInfo);
        }
        warehouseItemInfoService.insertList(list);
        return ResultUtil.createSuccessResult("添加新商品成功","success");
    }

    @Override
    public Pagenation<ItemsResult> queryWarehouseItemsSku(SkusForm form, Pagenation<Skus> page, Long warehouseInfoId) {
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
        Example example1 = new Example(Skus.class);
        Example.Criteria criteria1 = example.createCriteria();
        criteria1.andEqualTo("isValid",ZeroToNineEnum.ONE.getCode());
        criteria1.andNotIn("skuCode",excludeSkuCode);
        Pagenation<Skus> pageTem = skusService.pagination(example1,page,form);
        List<Skus> includeList = pageTem.getResult();
        log.info("开始补全未添加过的sku信息===============》");
        Map<String,BrandCategoryForm> map = completionData(includeList);
        List<ItemsResult> newList = new ArrayList<>();
        for (Skus sku:includeList){
            ItemsResult itemsResult = new ItemsResult();
            itemsResult.setSkuCode(sku.getSkuCode());
            itemsResult.setSkuName(sku.getSkuName());
            itemsResult.setSpuCode(sku.getSpuCode());
            itemsResult.setSpecInfo(sku.getSpecInfo());
            BrandCategoryForm BrandCategoryForm = map.get(sku.getSkuCode());
            itemsResult.setBrandName(BrandCategoryForm.getBrandName());
            itemsResult.setCategoryName(BrandCategoryForm.getCategoryName());
            newList.add(itemsResult);
        }
        Pagenation<ItemsResult> pagenation = new Pagenation<>();
        pagenation.setStart(pageTem.getStart());
        pagenation.setTotalCount(pageTem.getTotalCount());
        pagenation.setPageSize(pageTem.getPageSize());
        pagenation.setPageNo(pageTem.getPageNo());
        pagenation.setResult(newList);
        log.info("补全数据结束，返回结果");
        return pagenation;
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
        }else if (state == ZeroToNineEnum.ONE.getCode()){
            str = "停用";
        }else if (state == ZeroToNineEnum.TWO.getCode()){
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

    private String convertWarehouseState(String ownerWarehouseState) {
        String state = null;
        if(StringUtils.isBlank(ownerWarehouseState)){
            state = "通知仓库状态为空";
        } else if (StringUtils.isEquals(ownerWarehouseState, ZeroToNineEnum.ZERO.getCode())){
            state = "待通知";
        }else if (StringUtils.isEquals(ownerWarehouseState, ZeroToNineEnum.ONE.getCode())){
            state = "通知成功";
        }else if (StringUtils.isEquals(ownerWarehouseState, ZeroToNineEnum.TWO.getCode())){
            state = "通知失败";
        }else if (StringUtils.isEquals(ownerWarehouseState, ZeroToNineEnum.THREE.getCode())){
            state = "通知中";
        }
        return state;
    }

    private Integer convertDeleteState(WarehouseInfo warehouseInfo){
        Integer count = 0;
        if (StringUtils.isEquals(warehouseInfo.getOwnerWarehouseState(), ZeroToNineEnum.ZERO.getCode()) ||
                StringUtils.isEquals(warehouseInfo.getOwnerWarehouseState(), ZeroToNineEnum.TWO.getCode())){
            count = 1;
        }
        if (warehouseInfo.getSkuNum()==null || warehouseInfo.getSkuNum() == 0 ){
            count = 1;
        }
        return count;
    }

    @Override
    public Response saveOwnerInfo(WarehouseInfo warehouseInfo){
        AssertUtil.notBlank(warehouseInfo.getOwnerName(),"货主姓名不能为空");
        AssertUtil.notNull(warehouseInfo.getId(),"主键不能为空");
        AssertUtil.notBlank(warehouseInfo.getWarehouseId(),"仓库主键不能为空");
        Warehouse warehouse = warehouseService.selectByPrimaryKey(Long.valueOf(warehouseInfo.getWarehouseId()));
        if (warehouse.getIsNoticeSuccess() != null && warehouse.getIsNoticeSuccess().equals(NoticeSuccessEnum.UN_NOTIC.getCode())){
            Example example = new Example(WarehouseInfo.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("id",warehouseInfo.getId());
            warehouseInfo.setOwnerWarehouseState(ZeroToNineEnum.ONE.getCode());
            int cout = warehouseInfoService.updateByExampleSelective(warehouseInfo,example);
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
        return ResultUtil.createSuccessResult("保存货主信息成功","success");
    }

    @Override
    public Response deleteWarehouse(String id){
        AssertUtil.notBlank(id,"主键不能为空");
        Example example = new Example(WarehouseInfo.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",id);
        WarehouseInfo warehouseInfo = new WarehouseInfo();
        warehouseInfo.setIsDelete(Integer.valueOf(ZeroToNineEnum.ONE.getCode()));
        int cout = warehouseInfoService.updateByExampleSelective(warehouseInfo,example);
        if (cout==0){
            log.error("删除仓库信息失败");
            String msg = "删除从库信息失败";
            throw new WarehouseInfoException(ExceptionEnum.WAREHOUSE_INFO_EXCEPTION, msg);
        }
        return ResultUtil.createSuccessResult("删除仓库信息成功","success");
    }
}
