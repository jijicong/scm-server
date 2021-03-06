package org.trc.biz.impl.goods;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.goods.IitemGroupBiz;
import org.trc.domain.goods.ItemGroup;
import org.trc.domain.goods.ItemGroupUser;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.*;
import org.trc.exception.ItemGroupException;
import org.trc.exception.ParamValidException;
import org.trc.form.goods.ItemGroupForm;
import org.trc.form.goods.ItemGroupQuery;
import org.trc.service.config.ILogInfoService;
import org.trc.service.goods.IItemGroupService;
import org.trc.service.goods.IItemGroupUserService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by hzgjl on 2018/7/26.
 */
@Service("itemGroupBiz")
public class ItemGroupBiz implements IitemGroupBiz {
    private Logger logger = LoggerFactory.getLogger(ItemGroupBiz.class);
    @Resource
    private IItemGroupService itemGroupService;
    @Resource
    private IAclUserAccreditInfoService userAccreditInfoService;
    @Resource
    private IItemGroupUserService itemGroupUserService;


    @Resource
    private ISerialUtilService serialUtilService;
    @Resource
    private ILogInfoService logInfoService;

    private static final String  SERIALNAME = "SPZ";
    /**
     * 正则表达式：验证手机号
     */
    private static final String REGEX_MOBILE = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";

    private static final Integer LENGTH = 5;//商品组编号5位流水号


    //商品组分页
    @Override
    public Pagenation<ItemGroup> itemGroupPage(ItemGroupQuery form, Pagenation<ItemGroup> page, AclUserAccreditInfo aclUserAccreditInfo) {
        Example example=new Example(ItemGroup.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(form.getItemGroupName())){
            criteria.andLike("itemGroupName","%"+form.getItemGroupName()+"%");
        }
        if (StringUtil.isNotEmpty(form.getIsValid())) {
            criteria.andEqualTo("isValid", form.getIsValid());
        }
        // criteria.andEqualTo("channelCode",aclUserAccreditInfo.getChannelCode());
        example.orderBy("updateTime").desc();
        Pagenation<ItemGroup> pagenation = itemGroupService.pagination(example, page, form);
        handleUserName(pagenation.getResult());
        return pagenation;
    }

    //设置创建人名字
    private void handleUserName(List list) {
        if(AssertUtil.collectionIsEmpty(list)){
            return;
        }
        Set<String> userIdsSet=new HashSet<>();
        for (Object obj:list) {
            userIdsSet.add(((ItemGroup)obj).getCreateOperator());
        }
        String[] userIdArr=new String[userIdsSet.size()];
        userIdsSet.toArray(userIdArr);
        Map<String,AclUserAccreditInfo> mapTemp = userAccreditInfoService.selectByIds(userIdArr);
        for (Object obj:list) {
            if(!StringUtils.isBlank(((ItemGroup)obj).getCreateOperator())){
                if(mapTemp!=null){
                    AclUserAccreditInfo aclUserAccreditInfo =mapTemp.get(((ItemGroup)obj).getCreateOperator());
                    if(aclUserAccreditInfo !=null){
                        ((ItemGroup)obj).setCreateOperator(aclUserAccreditInfo.getName());
                    }
                }
            }

        }
    }
    //根据商品组编码查询详情
    @Override
    public ItemGroup queryDetailByCode(String code) {
        AssertUtil.notBlank(code,"商品组编码参数code不能为空");
        ItemGroup itemGroupTemp = new ItemGroup();
        itemGroupTemp.setItemGroupCode(code);
        ItemGroup itemGroup = itemGroupService.selectOne(itemGroupTemp);
        return itemGroup;
    }

    //商品组编辑
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void editDetail(ItemGroupForm form,AclUserAccreditInfo aclUserAccreditInfo) {
        ItemGroup itemGroup = form.getItemGroup();
        List<ItemGroupUser> groupUserList = form.getGroupUserList();
        //查询详情便于记录日志
        ItemGroup orginEntity = queryDetailByCode(itemGroup.getItemGroupCode());

        AssertUtil.notNull(itemGroup,"根据商品组信息修改商品组失败,商品组信息为null");
        String itemGroupName=itemGroup.getItemGroupName();
        AssertUtil.notNull(itemGroupName,"商品组名称为空！");
        //商品组名称非重校验
        ItemGroup temp =findItemGroupByName(itemGroupName);
        if(temp!=null&&!StringUtils.equals(temp.getId().toString(),itemGroup.getId().toString())){
            String msg=String.format("商品组名称[itemGroupName=%s]的数据已存在,请使用其他名称",itemGroup.getItemGroupName());
            logger.error(msg);
            throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_UPDATE_EXCEPTION,msg);
        }

        String leaderName = itemGroup.getLeaderName();
        AssertUtil.notNull(leaderName,"请选择组长！");
        AssertUtil.notNull(groupUserList,"请至少添加一个组员！");
        List<String> phoneListNew=groupUserList.stream().map(e -> e.getPhoneNumber()).collect(Collectors.toList());
        List<String> phoneListCheck=new ArrayList<>();
        for (String phone : phoneListNew) {
            if (phoneListCheck.contains(phone)){
                  String msg= String.format("该手机号[%s]在组内已存在！",phone);
              logger.error(msg);
              throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_UPDATE_EXCEPTION,msg);
            }
            AssertUtil.notBlank(phone,"手机号码不能为空！");
            if (!Pattern.compile(REGEX_MOBILE).matcher(phone).matches()){
                String msg="手机号码"+phone+"格式错误";
                logger.error(msg);
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION,msg);
            }
            phoneListCheck.add(phone);
        }
        for (ItemGroupUser itemGroupUser : groupUserList) {
            AssertUtil.notBlank(itemGroupUser.getName(),"商品组员名字不能为空！");
        }
        itemGroup.setUpdateTime(Calendar.getInstance().getTime());
        itemGroupService.updateByPrimaryKeySelective(itemGroup);


        //更新用户数据
        String logMsg="";
        List<ItemGroupUser> orginlist = queryItemGroupUserListByCode(itemGroup.getItemGroupCode());
        List<Long> ids = groupUserList.stream().map(e -> e.getId()).collect(Collectors.toList());
        for (ItemGroupUser oldItemGroupUser : orginlist) {
            if(!ids.contains(oldItemGroupUser.getId())){//删除
                Integer countDel = itemGroupUserService.deleteByPrimaryKey(oldItemGroupUser.getId());
                logMsg=logMsg+"商品组员手机号为\""+oldItemGroupUser.getPhoneNumber()+"\"的成员被删除了;";
                if (countDel==null){
                    String msg=String.format("商品组名称[itemGroupName=%s]的手机号码为[phoneNumber=%s]的用户删除失败，数据库操作失败",itemGroup.getItemGroupName(),oldItemGroupUser.getPhoneNumber());
                    logger.error(msg);
                    throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_UPDATE_EXCEPTION,msg);
                }
            }
        }
        for (ItemGroupUser itemGroupUser : groupUserList) {
            if (itemGroupUser.getId()==null){//id不存在为新增成员
                String name=itemGroupUser.getName();
                ItemGroupUser insertEntity = new ItemGroupUser();
                insertEntity.setName(name);
                insertEntity.setPhoneNumber(itemGroupUser.getPhoneNumber());
                insertEntity.setIsLeader(itemGroupUser.getIsLeader());
                insertEntity.setItemGroupCode(itemGroup.getItemGroupCode());
                insertEntity.setChannelCode(aclUserAccreditInfo.getChannelCode());
                insertEntity.setCreateTime(Calendar.getInstance().getTime());
                insertEntity.setUpdateTime(Calendar.getInstance().getTime());
                insertEntity.setCreateOperator(aclUserAccreditInfo.getName());
                insertEntity.setIsValid(itemGroup.getIsValid());
                Integer countIns = itemGroupUserService.insertSelective(insertEntity);

                logMsg=logMsg+"商品组员新增了手机号为\""+itemGroupUser.getPhoneNumber()+"\"的成员;";
                if (countIns==null){
                    String msg=String.format("商品组名称[itemGroupName=%s]的手机号码为[phoneNumber=%s]的用户插入失败，数据库操作失败",itemGroup.getItemGroupName(),itemGroupUser.getPhoneNumber());
                    logger.error(msg);
                    throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_UPDATE_EXCEPTION,msg);
                }
            }else {//修改操作
                ItemGroupUser updateEntity = itemGroupUserService.selectByPrimaryKey(itemGroupUser.getId());
                if (!StringUtils.equals(itemGroupUser.getName(),updateEntity.getName())){
                    updateEntity.setName(itemGroupUser.getName());
                    logMsg=logMsg+"商品组员名称由\""+updateEntity.getName()+"\"改为\""+itemGroupUser.getName()+"\";";
                }
                if (!StringUtils.equals(itemGroupUser.getPhoneNumber(),updateEntity.getPhoneNumber())){
                    updateEntity.setPhoneNumber(itemGroupUser.getPhoneNumber());
                    logMsg=logMsg+"商品组员手机号码由\""+updateEntity.getPhoneNumber()+"\"改为\""+itemGroupUser.getPhoneNumber()+"\";";
                }
                if (!StringUtils.equals(itemGroupUser.getIsLeader(),updateEntity.getIsLeader())){
                    updateEntity.setIsLeader(itemGroupUser.getIsLeader());
                    if (StringUtils.equals(itemGroupUser.getIsLeader(),ZeroToNineEnum.ZERO.getCode())){
                        logMsg=logMsg+"原组员名字：\""+updateEntity.getName()+"\"由组长改为组员;";
                    }else {
                        logMsg=logMsg+"原组员名字：\""+updateEntity.getName()+"\"由组员改为组长;";
                    }
                }
                updateEntity.setItemGroupCode(itemGroup.getItemGroupCode());
                updateEntity.setCreateTime(itemGroup.getCreateTime());
                updateEntity.setUpdateTime(Calendar.getInstance().getTime());
                Integer countUpd = itemGroupUserService.updateByPrimaryKeySelective(updateEntity);
                if (countUpd==null){
                        String msg=String.format("商品组员手机号码为[%s]的用户信息修改失败，数据库操作失败",itemGroupUser.getPhoneNumber());
                        logger.error(msg);
                        throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_UPDATE_EXCEPTION,msg);
                }
            }
        }

        //记录日志
        String orginItemGroupName = orginEntity.getItemGroupName();
        String orginRemark = orginEntity.getRemark();
        String orginIsValid = orginEntity.getIsValid();
        if (!StringUtils.equals(orginItemGroupName,itemGroup.getItemGroupName())){
            logMsg=logMsg+"商品组名称由\""+orginItemGroupName+"\"改为\""+itemGroupName+"\";";
        }
        if (!StringUtils.equals(orginRemark.trim(),itemGroup.getRemark().trim())){
            logMsg=logMsg+"备注由\""+orginRemark.trim()+"\"改为\""+itemGroup.getRemark().trim()+"\";";
        }
        if (!StringUtils.equals(orginIsValid,itemGroup.getIsValid())){
            logMsg=logMsg+"状态由\""+ValidEnum.getValidEnumByCode(orginIsValid).getName()+"\"改为\""+ValidEnum.getValidEnumByCode(itemGroup.getIsValid()).getName()+"\";";
        }
        if (!StringUtils.equals(logMsg,"")){
            logMsg=logMsg.substring(0,logMsg.lastIndexOf(";"))+"。";
            logInfoService.recordLog(itemGroup,itemGroup.getId().toString(),aclUserAccreditInfo.getUserId(),LogOperationEnum.UPDATE.getMessage(),logMsg,null);
        }
    }




    //添加商品组
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void itemGroupSave(ItemGroupForm form, AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(form,"商品组form信息为null");
        ItemGroup itemGroup = form.getItemGroup();
        List<ItemGroupUser> groupUserList = form.getGroupUserList();
        //校验信息
        AssertUtil.notNull(form.getItemGroup(),"商品组管理模块新增商品组信息失败,商品组信息为null");
        AssertUtil.notNull(itemGroup.getItemGroupName(),"商品组名称为空");
        AssertUtil.notNull(groupUserList,"请至少添加一个组员！");
        AssertUtil.notNull(itemGroup.getIsValid(),"启停用状态没有选择！");

        ItemGroup temp =findItemGroupByName(itemGroup.getItemGroupName());
        if (temp!=null){
            String msg="当前商品组名称已存在,请使用其他名称！";
            logger.error(msg);
            throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_QUERY_EXCEPTION,msg);
        }
        String channelCode = aclUserAccreditInfo.getChannelCode();
        itemGroup.setChannelCode(channelCode);
        //公共字段更新
        itemGroup.setCreateTime(Calendar.getInstance().getTime());
        itemGroup.setUpdateTime(Calendar.getInstance().getTime());
        itemGroup.setCreateOperator(aclUserAccreditInfo.getName());

        String code = serialUtilService.generateCode(LENGTH, SERIALNAME);
        itemGroup.setItemGroupCode(code);
        Integer count = itemGroupService.insertSelective(itemGroup);
        if (count==null){
            String msg="商品组保存失败，数据库操作失败";
            logger.error(msg);
            throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_SAVE_EXCEPTION,msg);
        }


        //商品组启用，则组长为启用，否则视同组员
        String isValid = itemGroup.getIsValid();
        //保存商品组员列表数据（对手机号校验）
        saveItemGroupUserList(groupUserList,isValid,code,channelCode,aclUserAccreditInfo.getName());


        //记录日志
        logInfoService.recordLog(itemGroup,itemGroup.getId().toString(),aclUserAccreditInfo.getUserId(), LogOperationEnum.ADD.getMessage(),"",null);

    }


    //需要实时查询
    @Override
    public ItemGroup findItemGroupByName(String name) {
        AssertUtil.notBlank(name,"根据商品组名称查询采购组的参数itemGroupName为空");
        ItemGroup itemGroup = new ItemGroup();
        itemGroup.setItemGroupName(name);
        return itemGroupService.selectOne(itemGroup);
    }

    //启停用
    @Override
    public void updateStatus(String isValid, String itemGroupCode,AclUserAccreditInfo aclUserAccreditInfo) {
        ItemGroup temp = new ItemGroup();
        temp.setItemGroupCode(itemGroupCode);
        temp = itemGroupService.selectOne(temp);
        if (StringUtils.equals(temp.getIsValid(),isValid)){
            if (StringUtils.equals(isValid,ZeroToNineEnum.ZERO.getCode())){
                throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_UPDATE_EXCEPTION,"当前状态已经是停用状态，不能再停用");
            }else {
                throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_UPDATE_EXCEPTION,"当前状态已经是启用状态，不能再启用");
            }

        }
        ItemGroup itemGroup = queryDetailByCode(itemGroupCode);
        itemGroup.setIsValid(isValid);
        itemGroup.setUpdateTime(Calendar.getInstance().getTime());
        Integer count = itemGroupService.updateByPrimaryKeySelective(itemGroup);
        if (count==null){
            String msg="更新商品组信息失败,数据库操作失败！";
            logger.error(msg);
            throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_UPDATE_EXCEPTION,msg);
        }

        //更新对应组员的状态
        Example example = new Example(ItemGroupUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("itemGroupCode",itemGroupCode);
        //criteria.andEqualTo("channelCode",aclUserAccreditInfo.getChannelCode());
        List<ItemGroupUser> list = itemGroupUserService.selectByExample(example);
        for (ItemGroupUser itemGroupUser : list) {
            itemGroupUser.setIsValid(isValid);
            itemGroupUser.setUpdateTime(Calendar.getInstance().getTime());
            Integer countUser = itemGroupUserService.updateByPrimaryKey(itemGroupUser);
            if (countUser==null){
                String msg="更新商品组成员信息失败，数据库操作失败！";
                logger.error(msg);
                throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_UPDATE_EXCEPTION,msg);
            }
        }

        //记录日志
        String orginIsValid = temp.getIsValid();
        String logMsg="状态由\""+ValidEnum.getValidEnumByCode(orginIsValid).getName()+"\"改为\""+ValidEnum.getValidEnumByCode(isValid).getName()+"\"。";
        logInfoService.recordLog(itemGroup,itemGroup.getId().toString(),aclUserAccreditInfo.getUserId(),LogOperationEnum.UPDATE.getMessage(),logMsg,null);
    }

    @Override
    public List<ItemGroupUser> queryItemGroupUserListByCode(String itemGroupCode) {
        Example example = new Example(ItemGroupUser.class);
        example.createCriteria().andEqualTo("itemGroupCode",itemGroupCode);
        List<ItemGroupUser> list = itemGroupUserService.selectByExample(example);
        return list;
    }

    private void saveItemGroupUserList(List<ItemGroupUser> groupUserList,String isValid,String itemGroupCode,String channelCode,String name){
        List<String> list=new ArrayList<>();

        for (ItemGroupUser itemGroupUser : groupUserList) {
            String phoneNumber = itemGroupUser.getPhoneNumber();
            AssertUtil.notBlank(phoneNumber,"手机号码不能为空！");
            AssertUtil.notBlank(itemGroupUser.getName(),"商品组员名字不能为空！");
            if (!Pattern.compile(REGEX_MOBILE).matcher(phoneNumber).matches()){
                String msg="手机号码"+phoneNumber+"格式错误";
                logger.error(msg);
                throw new ParamValidException(CommonExceptionEnum.PARAM_CHECK_EXCEPTION,msg);
            }
            itemGroupUser.setChannelCode(channelCode);
            itemGroupUser.setCreateTime(Calendar.getInstance().getTime());
            itemGroupUser.setUpdateTime(Calendar.getInstance().getTime());
            itemGroupUser.setIsValid(isValid);
            itemGroupUser.setCreateOperator(name);
            itemGroupUser.setItemGroupCode(itemGroupCode);
            //手机号添加之前判断是否存在
            if (list.contains(phoneNumber)){
                String msg=itemGroupUser.getPhoneNumber()+"该手机号在组内已存在！";
                logger.error(msg);
                throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_SAVE_EXCEPTION,msg);
            }
            list.add(phoneNumber);
        }

        itemGroupUserService.insertList(groupUserList);
    }


}
