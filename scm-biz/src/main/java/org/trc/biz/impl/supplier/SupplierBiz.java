package org.trc.biz.impl.supplier;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.trc.biz.supplier.ISupplierBiz;
import org.trc.domain.supplier.Supplier;
import org.trc.enums.CommonExceptionEnum;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.SupplierException;
import org.trc.exception.ParamValidException;
import org.trc.form.supplier.SupplierForm;
import org.trc.service.supplier.ISupplierService;
import org.trc.util.CommonUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
import org.trc.util.ParamsUtil;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by hzwdx on 2017/5/5.
 */
@Service("supplierBiz")
public class SupplierBiz implements ISupplierBiz {

    private final static Logger log = LoggerFactory.getLogger(SupplierBiz.class);

    @Autowired
    private ISupplierService supplierService;

    @Override
    public Pagenation<Supplier> SupplierPage(SupplierForm queryModel, Pagenation<Supplier> page) throws Exception {
        Example example = new Example(Supplier.class);
        Example.Criteria criteria = example.createCriteria();
        if(StringUtil.isNotEmpty(queryModel.getSupplierName())) {//供应商名称
            criteria.andLike("supplierName", "%" + queryModel.getSupplierName() + "%");
        }
        if(StringUtil.isNotEmpty(queryModel.getSupplierCode())) {//供应商编码
            criteria.andLike("supplierCode", "%" + queryModel.getSupplierCode() + "%");
        }
        if(StringUtil.isNotEmpty(queryModel.getContact())) {//联系人
            criteria.andLike("contact", "%" + queryModel.getContact() + "%");
        }
        if(StringUtil.isNotEmpty(queryModel.getSupplierKindCode())) {//供应商性质
            criteria.andEqualTo("supplierKindCode", queryModel.getSupplierKindCode());
        }
        if(StringUtil.isNotEmpty(queryModel.getStartDate())) {//开始日期
            criteria.andGreaterThanOrEqualTo("updateTime", DateUtils.parseDate(queryModel.getStartDate()));
        }
        if(StringUtil.isNotEmpty(queryModel.getEndDate())) {//截止日期
            Date endDate = DateUtils.parseDate(queryModel.getEndDate());
            criteria.andLessThan("updateTime", DateUtils.addDays(endDate,1));
        }
        if(StringUtil.isNotEmpty(queryModel.getIsValid())) {
            criteria.andEqualTo("isValid", queryModel.getIsValid());
        }
        example.orderBy("isValid").desc();
        //分页查询
        return supplierService.pagination(example, page, queryModel);
    }

    @Override
    public List<Supplier> querySuppliers(SupplierForm supplierForm) throws Exception {
        Supplier supplier = new Supplier();
        BeanUtils.copyProperties(supplierForm,supplier);
        if(StringUtils.isEmpty(supplierForm.getIsValid())){
            supplier.setIsValid(ZeroToNineEnum.ONE.getCode());
        }
        supplier.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        return supplierService.select(supplier);
    }

    @Override
    public int saveSupplier(Supplier supplier) throws Exception {
        int count = 0;
        if(null != supplier.getId()){
            //修改
            supplier.setUpdateTime(Calendar.getInstance().getTime());
            count = supplierService.updateByPrimaryKeySelective(supplier);
        }else{
            //新增
            ParamsUtil.setBaseDO(supplier);
            count = supplierService.insert(supplier);
        }
        if(count == 0){
            String msg = CommonUtil.joinStr("保存供应商", JSON.toJSONString(supplier),"到数据库失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_SAVE_EXCEPTION,msg);
        }
        return count;
    }

    @Override
    public int updateSupplier(Supplier supplier, Long id) throws Exception {
        if(null == id){
            String msg = CommonUtil.joinStr("修改供应商参数ID为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        int count = 0;
        supplier.setId(id);
        supplier.setUpdateTime(Calendar.getInstance().getTime());
        count = supplierService.updateByPrimaryKeySelective(supplier);
        if(count == 0){
            String msg = CommonUtil.joinStr("修改供应商",JSON.toJSONString(supplier),"数据库操作失败").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_UPDATE_EXCEPTION, msg);
        }
        return count;
    }

    @Override
    public Supplier findSupplierById(Long id) throws Exception {
        if(null == id){
            String msg = CommonUtil.joinStr("根据ID查询供应商参数ID为空").toString();
            log.error(msg);
            throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION, msg);
        }
        Supplier supplier = new Supplier();
        supplier.setId(id);
        supplier = supplierService.selectOne(supplier);
        if(null == supplier){
            String msg = CommonUtil.joinStr("根据主键ID[id=",id.toString(),"]查询供应商为空").toString();
            log.error(msg);
            throw new SupplierException(ExceptionEnum.SUPPLIER_QUERY_EXCEPTION,msg);
        }
        return supplier;
    }
}
