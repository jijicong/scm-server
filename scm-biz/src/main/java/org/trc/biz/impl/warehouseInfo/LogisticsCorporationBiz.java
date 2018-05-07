package org.trc.biz.impl.warehouseInfo;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.warehouseInfo.ILogisticsCorporationBiz;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.domain.warehouseInfo.LogisticsCorporation;
import org.trc.enums.*;
import org.trc.exception.LogisticsCorporationException;
import org.trc.form.warehouseInfo.LogisticsCorporationForm;
import org.trc.service.config.ILogInfoService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.warehouseInfo.ILogisticsCorporationService;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import org.trc.util.ResultUtil;
import tk.mybatis.mapper.entity.Example;

import javax.ws.rs.core.Response;
import java.util.Calendar;
import java.util.List;

/**
 * Created by hzcyn on 2018/5/3.
 */
@Service("logisticsCorporationBiz")
public class LogisticsCorporationBiz implements ILogisticsCorporationBiz {

    private Logger logger = LoggerFactory.getLogger(LogisticsCorporationBiz.class);

    private final static String SERIALNAME = "WL";

    private final static Integer LENGTH = 3;

    @Autowired
    private ILogisticsCorporationService logisticsCorporationService;
    @Autowired
    private ISerialUtilService serialUtilService;
    @Autowired
    private ILogInfoService logInfoService;

    @Override
    public Pagenation<LogisticsCorporation> selectLogisticsCorporationByPage(LogisticsCorporationForm query, Pagenation<LogisticsCorporation> page) {
        AssertUtil.notNull(page.getPageNo(),"分页查询参数pageNo不能为空");
        AssertUtil.notNull(page.getPageSize(),"分页查询参数pageSize不能为空");
        AssertUtil.notNull(page.getStart(),"分页查询参数start不能为空");
        logger.info("开始查询符合条件的信息===========》");
        Example example = new Example(LogisticsCorporation.class);
        Example.Criteria criteria = example.createCriteria();
        if(!StringUtils.isBlank(query.getLogisticsCorporationName())){
            criteria.andLike("logisticsCorporationName","%"+query.getLogisticsCorporationName()+"%");
        }
        if(!StringUtils.isBlank(query.getLogisticsCode())){
            criteria.andLike("logisticsCode","%"+query.getLogisticsCode()+"%");
        }
        if(!StringUtils.isBlank(query.getLogisticsCorporationType())){
            criteria.andEqualTo("logisticsCorporationType",query.getLogisticsCorporationType());
        }
        if(!StringUtils.isBlank(query.getIsValid())){
            criteria.andEqualTo("isValid",query.getIsValid());
        }
        criteria.andEqualTo("isDeleted", ZeroToNineEnum.ZERO.getCode());
        example.orderBy("updateTime").desc();
        Pagenation<LogisticsCorporation> pagenation = logisticsCorporationService.pagination(example,page,query);
        logger.info("查询结束===========》");
        return pagenation;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public Response saveLogisticsCorporation(LogisticsCorporation logisticsCorporation, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(logisticsCorporation.getLogisticsCorporationName(),"物流公司名称不能为空");
        AssertUtil.notNull(logisticsCorporation.getLogisticsCorporationCode(),"物流公司编码不能为空");
        AssertUtil.notNull(logisticsCorporation.getLogisticsCorporationType(),"物流公司类型不能为空");
        AssertUtil.notNull(logisticsCorporation.getIsValid(),"物流公司状态不能为空");
        LogisticsCorporation logisticsCorporationOnly = new LogisticsCorporation();
        logisticsCorporationOnly.setLogisticsCorporationCode(logisticsCorporation.getLogisticsCorporationCode());
        List<LogisticsCorporation> logisticsCorporationList = logisticsCorporationService.select(logisticsCorporationOnly);
        if(logisticsCorporationList.size() > 0){
            String msg = "物流公司编码不唯一";
            logger.error(msg);
            throw new LogisticsCorporationException(ExceptionEnum.LOGISTICS_CORPORATION_SAVE_EXCEPTION, msg);
        }
        String userId = aclUserAccreditInfo.getUserId();
        AssertUtil.notBlank(userId, "获取当前登录的userId失败");
        logisticsCorporation.setCreateOperator(userId);
        logisticsCorporation.setLogisticsCode(serialUtilService.generateCode(LENGTH, SERIALNAME));
        logisticsCorporation.setUpdateTime(Calendar.getInstance().getTime());
        logisticsCorporation.setCreateTime(Calendar.getInstance().getTime());
        logisticsCorporation.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        logger.info("保存物流信息到数据库=====》");
        int count = logisticsCorporationService.insert(logisticsCorporation);
        if (count == 0) {
            String msg = "新增物流公司信息失败";
            logger.error(msg);
            throw new LogisticsCorporationException(ExceptionEnum.LOGISTICS_CORPORATION_SAVE_EXCEPTION, msg);
        }
        logger.info("《===========保存物流信息成功");
        logInfoService.recordLog(logisticsCorporation, logisticsCorporation.getId().toString(), userId, LogOperationEnum.ADD.getMessage(), null, null);
        return ResultUtil.createSuccessResult("保存物流公司信息成功","success");
    }

    @Override
    public void updateLogisticsCorporation(LogisticsCorporation logisticsCorporation, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(logisticsCorporation.getId(), "根据ID修改物流公司，参数ID为空");
        AssertUtil.notNull(logisticsCorporation.getLogisticsCorporationName(),"物流公司名称不能为空");
        AssertUtil.notNull(logisticsCorporation.getLogisticsCorporationCode(),"物流公司编码不能为空");
        AssertUtil.notNull(logisticsCorporation.getLogisticsCorporationType(),"物流公司类型不能为空");
        AssertUtil.notNull(logisticsCorporation.getIsValid(),"物流公司状态不能为空");

        LogisticsCorporation logisticsCorporationOnly = new LogisticsCorporation();
        logisticsCorporationOnly.setLogisticsCorporationCode(logisticsCorporation.getLogisticsCorporationCode());
        List<LogisticsCorporation> logisticsCorporationList = logisticsCorporationService.select(logisticsCorporationOnly);
        if(logisticsCorporationList.size() > 0){
            String msg = "物流公司编码不唯一";
            logger.error(msg);
            throw new LogisticsCorporationException(ExceptionEnum.LOGISTICS_CORPORATION_UPDATE_EXCEPTION, msg);
        }

        logisticsCorporation.setUpdateTime(Calendar.getInstance().getTime());
        LogisticsCorporation _logisticsCorporation = logisticsCorporationService.selectByPrimaryKey(logisticsCorporation.getId());
        String remark = "";
        AssertUtil.notNull(_logisticsCorporation, "根据id查询物流公司信息为空");
        int count = logisticsCorporationService.updateByPrimaryKeySelective(logisticsCorporation);
        if (count == 0) {
            String msg = String.format("修改物流公司%s数据库操作失败", JSON.toJSONString(logisticsCorporation));
            logger.error(msg);
            throw new LogisticsCorporationException(ExceptionEnum.LOGISTICS_CORPORATION_UPDATE_EXCEPTION, msg);
        }
        if (!_logisticsCorporation.getIsValid().equals(logisticsCorporation.getIsValid())) {
            if (logisticsCorporation.getIsValid().equals(ValidEnum.VALID.getCode())) {
                remark = remarkEnum.VALID_ON.getMessage();
            } else {
                remark = remarkEnum.VALID_OFF.getMessage();
            }
        }

        String userId = aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(logisticsCorporation, logisticsCorporation.getId().toString(), userId, LogOperationEnum.UPDATE.getMessage(), remark, null);
    }

    @Override
    public void updateLogisticsCorporationState(LogisticsCorporation logisticsCorporation, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(logisticsCorporation.getId(), "根据ID修改物流公司，参数ID为空");
        AssertUtil.notNull(logisticsCorporation.getLogisticsCorporationName(),"物流公司名称不能为空");
        AssertUtil.notNull(logisticsCorporation.getLogisticsCorporationCode(),"物流公司编码不能为空");
        AssertUtil.notNull(logisticsCorporation.getLogisticsCorporationType(),"物流公司类型不能为空");
        AssertUtil.notNull(logisticsCorporation.getIsValid(),"物流公司状态不能为空");

        LogisticsCorporation updateLogisticsCorporation = new LogisticsCorporation();
        updateLogisticsCorporation.setId(logisticsCorporation.getId());
        String remark = "";
        if (logisticsCorporation.getIsValid().equals(ValidEnum.VALID.getCode())) {
            logisticsCorporation.setIsValid(ValidEnum.NOVALID.getCode());
            remark = remarkEnum.VALID_OFF.getMessage();
        } else {
            logisticsCorporation.setIsValid(ValidEnum.VALID.getCode());
        }
        logisticsCorporation.setUpdateTime(Calendar.getInstance().getTime());
        int count = logisticsCorporationService.updateByPrimaryKeySelective(logisticsCorporation);
        if (count == 0) {
            String msg = String.format("修改物流公司%s数据库操作失败", JSON.toJSONString(updateLogisticsCorporation));
            logger.error(msg);
            throw new LogisticsCorporationException(ExceptionEnum.LOGISTICS_CORPORATION_UPDATE_EXCEPTION, msg);
        }
        String userId = aclUserAccreditInfo.getUserId();
        logInfoService.recordLog(logisticsCorporation, logisticsCorporation.getId().toString(), userId, LogOperationEnum.UPDATE.getMessage(), remark, null);
    }

}
