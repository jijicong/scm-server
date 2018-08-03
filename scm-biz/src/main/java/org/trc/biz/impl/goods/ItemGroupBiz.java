package org.trc.biz.impl.goods;

import com.ecfront.dew.common.$;
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
import org.trc.enums.ExceptionEnum;
import org.trc.enums.LogOperationEnum;
import org.trc.enums.ZeroToNineEnum;
import org.trc.exception.ItemGroupException;
import org.trc.form.goods.*;
import org.trc.service.config.ILogInfoService;
import org.trc.service.goods.IItemGroupService;
import org.trc.service.goods.IItemGroupUserService;
import org.trc.service.impower.IAclUserAccreditInfoService;
import org.trc.service.util.ISerialUtilService;
import org.trc.util.AssertUtil;
import org.trc.util.DateUtils;
import org.trc.util.Pagenation;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
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
    private static final String REGEX_MOBILE = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-9])|(147))\\\\d{8}$";

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
        criteria.andEqualTo("channelCode",aclUserAccreditInfo.getChannelCode());
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
    public void editDetail(ItemGroupForm2 form, AclUserAccreditInfo aclUserAccreditInfo) {
        ItemGroupVo itemGroupVo = form.getItemGroup();
        List<ItemGroupUserVO> groupUserlistVO = form.getList();
        //查询详情只用于记录日志
        ItemGroup orginEntity = queryDetailByCode(itemGroupVo.getItemGroupCode());


        AssertUtil.notNull(itemGroupVo,"根据商品组信息修改商品组失败,商品组信息为null");
        String itemGroupName=itemGroupVo.getItemGroupName();
        AssertUtil.notNull(itemGroupName,"商品组名称为空！");
        //商品组名称非重校验
        ItemGroup temp =findItemGroupByName(itemGroupName);
        if (temp!=null){
            String msg=String.format("商品组名称[itemGroupName=%s]的数据已存在,请使用其他名称",itemGroupVo.getItemGroupName());
            logger.error(msg);
            throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_UPDATE_EXCEPTION,msg);
        }


        String leaderName = itemGroupVo.getLeaderName();

        AssertUtil.notNull(leaderName,"请选择组长！");
        AssertUtil.notNull(groupUserlistVO,"请至少添加一个组员！");

        temp.setCreateTime(DateUtils.parseDateTime(itemGroupVo.getCreateTime()));
        temp.setUpdateTime(Calendar.getInstance().getTime());
        temp.setMemberName(itemGroupVo.getMemberName());
        temp.setLeaderName(itemGroupVo.getLeaderName());
        temp.setIsValid(itemGroupVo.getIsValid());
        temp.setItemGroupName(itemGroupName);
        temp.setRemark(itemGroupVo.getRemark());
        itemGroupService.updateByPrimaryKeySelective(temp);


        //更新用户数据
        String logMsg="";
        List<String> logDetail = new ArrayList<>();
        List<ItemGroupUser> orginlist = queryItemGroupUserListByCode(orginEntity.getItemGroupCode());
        for (ItemGroupUser oldItemGroupUser : orginlist) {
            List<Long> ids = groupUserlistVO.stream().map(e -> e.getId()).collect(Collectors.toList());

            for (ItemGroupUserVO itemGroupUserVO : groupUserlistVO) {

                if(!ids.contains(oldItemGroupUser.getId())){//删除
                    Integer countDel = itemGroupUserService.deleteByPrimaryKey(oldItemGroupUser.getId());
                    logMsg=logMsg+"商品组员手机号为\""+oldItemGroupUser.getPhoneNumber()+"\"的成员被删除了;";
                    logDetail.add(logMsg);
                    if (countDel==null){
                        String msg=String.format("商品组名称[itemGroupName=%s]的手机号码为[phoneNumber=%s]的用户删除失败，数据库操作失败",orginEntity.getItemGroupName(),oldItemGroupUser.getPhoneNumber());
                        logger.error(msg);
                        throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_UPDATE_EXCEPTION,msg);
                    }
                }

                if (StringUtils.isEmpty(itemGroupUserVO.getId().toString())){//id不存在为新增成员
                    ItemGroupUser insertEntity = new ItemGroupUser();
                    insertEntity.setName(itemGroupUserVO.getName());
                    insertEntity.setPhoneNumber(itemGroupUserVO.getPhoneNumber());
                    insertEntity.setIsLeader(itemGroupUserVO.getIsLeader());
                    insertEntity.setItemGroupCode(itemGroupVo.getItemGroupCode());
                    insertEntity.setChannelCode(aclUserAccreditInfo.getChannelCode());
                    insertEntity.setCreateTime(Calendar.getInstance().getTime());
                    insertEntity.setUpdateTime(Calendar.getInstance().getTime());
                    insertEntity.setCreateOperator(aclUserAccreditInfo.getName());
                    insertEntity.setItemGroupCode(orginEntity.getItemGroupCode());
                    Integer countIns = itemGroupUserService.insertSelective(insertEntity);

                    logMsg=logMsg+"商品组员新增了手机号为\""+itemGroupUserVO.getPhoneNumber()+"\"的成员;";
                    logDetail.add(logMsg);
                    if (countIns==null){
                        String msg=String.format("商品组名称[itemGroupName=%s]的手机号码为[phoneNumber=%s]的用户插入失败，数据库操作失败",itemGroupVo.getItemGroupName(),itemGroupUserVO.getPhoneNumber());
                        logger.error(msg);
                        throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_UPDATE_EXCEPTION,msg);
                    }
                }else {//修改操作
                    ItemGroupUser updateEntity = itemGroupUserService.selectByPrimaryKey(itemGroupUserVO.getId());
                    updateEntity.setName(itemGroupUserVO.getName());
                    updateEntity.setPhoneNumber(itemGroupUserVO.getPhoneNumber());
                    updateEntity.setIsLeader(itemGroupUserVO.getIsLeader());
                    updateEntity.setItemGroupCode(itemGroupVo.getItemGroupCode());
                    updateEntity.setUpdateTime(Calendar.getInstance().getTime());
                    Integer countUpd = itemGroupUserService.updateByPrimaryKeySelective(updateEntity);
                    if (countUpd==null){
                        String msg=String.format("商品组名称[itemGroupName=%s]的手机号码为[phoneNumber=%s]的用户修改失败，数据库操作失败",itemGroupVo.getItemGroupName(),oldItemGroupUser.getPhoneNumber());
                        logger.error(msg);
                        throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_UPDATE_EXCEPTION,msg);
                    }
                }

            }

        }

        //记录日志

        String orginItemGroupName = orginEntity.getItemGroupName();
        String orginRemark = orginEntity.getRemark();
        String orginIsValid = orginEntity.getIsValid();
        if (!StringUtils.equals(orginItemGroupName,itemGroupVo.getItemGroupName())){
            logMsg=logMsg+"商品组名称由\""+orginItemGroupName+"\"改为\""+itemGroupName+"\";";
            logDetail.add(logMsg);
        }
        if (!StringUtils.equals(orginRemark,itemGroupVo.getRemark())){
            logMsg=logMsg+"备注由\""+orginRemark+"\"改为\""+itemGroupVo.getRemark()+"\";";
            logDetail.add(logMsg);
        }
        if (!StringUtils.equals(orginIsValid,itemGroupVo.getIsValid())){
            String orginIsValidLog=(orginIsValid==ZeroToNineEnum.ZERO.getCode())?"停用":"启用";
            String newIsValidLog=(itemGroupVo.getIsValid()==ZeroToNineEnum.ZERO.getCode())?"停用":"启用";
            logMsg=logMsg+"状态由\""+orginIsValidLog+"\"改为\""+newIsValidLog+"\";";
            logDetail.add(logMsg);
        }

        String join = StringUtils.join(logDetail, ";");
        logInfoService.recordLog(itemGroupVo,itemGroupVo.getId().toString(),aclUserAccreditInfo.getUserId(),LogOperationEnum.UPDATE.getMessage(),join,null);
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
            String msg=String.format("商品组名称[itemGroupName=%s]的数据已存在,请使用其他名称",itemGroup.getItemGroupName());
            logger.error(msg);
            throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_QUERY_EXCEPTION,msg);
        }
        itemGroup.setChannelCode(aclUserAccreditInfo.getChannelCode());
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
        saveItemGroupUserList(groupUserList,isValid,code,aclUserAccreditInfo.getChannelCode());


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
        ItemGroup itemGroup = queryDetailByCode(itemGroupCode);
        itemGroup.setIsValid(isValid);
        itemGroup.setUpdateTime(Calendar.getInstance().getTime());
        Integer count = itemGroupService.updateByPrimaryKeySelective(itemGroup);
        if (count==null){
            String msg=String.format("更新商品组[itemGroupName=%s]的数据失败,数据库操作失败",itemGroup.getItemGroupName());
            logger.error(msg);
            throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_UPDATE_EXCEPTION,msg);
        }

        //更新对应组员的状态
        Example example = new Example(ItemGroupUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("itemGroupCode",itemGroupCode);
        criteria.andEqualTo("channelCode",aclUserAccreditInfo.getChannelCode());
        List<ItemGroupUser> list = itemGroupUserService.selectByExample(example);
        for (ItemGroupUser itemGroupUser : list) {
            itemGroupUser.setIsValid(isValid);
            itemGroupUser.setUpdateTime(Calendar.getInstance().getTime());
            Integer countUser = itemGroupUserService.updateByPrimaryKey(itemGroupUser);
            if (countUser==null){
                String msg="更新商品组成员信息失败，数据库操作失败";
                logger.error(msg);
                throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_UPDATE_EXCEPTION,msg);
            }
        }

        //记录日志
        String orginIsValid = itemGroup.getIsValid();
        String logMsg="状态由\""+orginIsValid+"\"改为\""+isValid+"\"。";
        logInfoService.recordLog(itemGroup,itemGroup.getId().toString(),aclUserAccreditInfo.getUserId(),LogOperationEnum.UPDATE.getMessage(),logMsg,null);
    }

    @Override
    public List<ItemGroupUser> queryItemGroupUserListByCode(String itemGroupCode) {
        Example example = new Example(ItemGroupUser.class);
        example.createCriteria().andEqualTo("itemGroupCode",itemGroupCode);
        List<ItemGroupUser> list = itemGroupUserService.selectByExample(example);
        return list;
    }

    private void saveItemGroupUserList(List<ItemGroupUser> groupUserList,String isValid,String itemGroupCode,String channelCode){
        List<String> list=new ArrayList<>();

        for (ItemGroupUser itemGroupUser : groupUserList) {
            String phoneNumber = itemGroupUser.getPhoneNumber();
            AssertUtil.notBlank(phoneNumber,"手机号码不能为空！");
            AssertUtil.notBlank(itemGroupUser.getName(),"商品组员名字不能为空！");
            if (Pattern.matches(REGEX_MOBILE,phoneNumber)){
                String msg="手机号码格式错误"+itemGroupUser.getPhoneNumber();
                logger.error(msg);
                throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_SAVE_EXCEPTION,msg);
            }

            itemGroupUser.setChannelCode(channelCode);

            itemGroupUser.setCreateTime(Calendar.getInstance().getTime());
            itemGroupUser.setUpdateTime(Calendar.getInstance().getTime());
            itemGroupUser.setIsValid(isValid);
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
