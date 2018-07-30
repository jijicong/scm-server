package org.trc.biz.impl.goods;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.goods.IitemGroupBiz;
import org.trc.constants.SupplyConstants;
import org.trc.domain.goods.ItemGroup;
import org.trc.domain.goods.ItemGroupUser;
import org.trc.domain.goods.ItemGroupUserRelation;
import org.trc.domain.impower.AclUserAccreditInfo;
import org.trc.enums.ExceptionEnum;
import org.trc.enums.LogOperationEnum;
import org.trc.exception.ItemGroupException;
import org.trc.form.goods.ItemGroupForm;
import org.trc.service.config.ILogInfoService;
import org.trc.service.goods.IItemGroupService;
import org.trc.service.goods.IItemGroupUserRelationService;
import org.trc.service.goods.IItemGroupUserService;
import org.trc.service.util.ISerialUtilService;
import org.trc.service.util.IUserNameUtilService;
import org.trc.util.AssertUtil;
import org.trc.util.Pagenation;
import org.trc.util.ParamsUtil;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.util.StringUtil;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by hzgjl on 2018/7/26.
 */
@Service("itemGroupBiz")
public class ItemGroupBiz implements IitemGroupBiz {
    private Logger logger = LoggerFactory.getLogger(ItemGroupBiz.class);
    @Resource
    private IItemGroupService itemGroupService;
    @Resource
    private IUserNameUtilService userNameUtilService;
    @Resource
    private IItemGroupUserService itemGroupUserService;
    @Resource
    private IItemGroupUserRelationService iItemGroupUserRelationService;



    @Resource
    private ISerialUtilService serialUtilService;
    @Resource
    private ILogInfoService logInfoService;

    private final static String  SERIALNAME = "SPZ";
    /**
     * 正则表达式：验证手机号
     */
    private final static String REGEX_MOBILE = "^((13[0-9])|(15[^4])|(18[0,2,3,5-9])|(17[0-9])|(147))\\\\d{8}$";

    private final static Integer LENGTH = 5;//商品组编号5位流水号


    //商品组分页
    @Override
    public Pagenation itemGroupPage(ItemGroupForm form, Pagenation<ItemGroup> page,AclUserAccreditInfo aclUserAccreditInfo) {
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
        userNameUtilService.handleUserName(pagenation.getResult());
        return pagenation;
    }

    //根据商品组编码查询详情
    @Override
    public ItemGroup queryDetailByCode(String code) {
        AssertUtil.notBlank(code,"商品组编码参数code不能为空");
        ItemGroup itemGroup = new ItemGroup();
        itemGroup.setItemGroupCode(code);
        return itemGroupService.selectOne(itemGroup);
    }

    //商品组编辑
    @Override
    public void editDetail(ItemGroup itemGroup) {
        //查询详情便于记录日志
        ItemGroup entity = queryDetailByCode(itemGroup.getItemGroupCode());
        String logMsg="";
        AssertUtil.notNull(itemGroup,"根据商品组信息修改商品组失败,商品组信息为null");
        AssertUtil.notNull(itemGroup.getItemGroupName(),"商品组名称为空！");
        String leaderName = itemGroup.getLeaderName();
        AssertUtil.notNull(leaderName,"请选择组长！");

        ItemGroup temp =findItemGroupByName(itemGroup.getItemGroupName());
        if (temp!=null){
            String msg=String.format("商品组名称[itemGroupName=%s]的数据已存在,请使用其他名称",itemGroup.getItemGroupName());
            logger.error(msg);
            throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_UPDATE_EXCEPTION,msg);
        }

        String memberUserId = itemGroup.getMemberUserId();
        AssertUtil.notNull(memberUserId,"请至少添加一个组员！");
          //TODO

    }




    //添加商品组
    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void itemGroupSave(ItemGroup itemGroup,List<ItemGroupUser> groupUserList,AclUserAccreditInfo aclUserAccreditInfo) {
        //校验信息
        AssertUtil.notNull(itemGroup,"商品组管理模块新增商品组信息失败,商品组信息为null");
        String leaderName = itemGroup.getLeaderName();
        AssertUtil.notNull(leaderName,"请选择组长！");
        AssertUtil.notNull(groupUserList,"请至少添加一个组员！");

        ItemGroup temp =findItemGroupByName(itemGroup.getItemGroupName());
        if (temp!=null){
            String msg=String.format("商品组名称[itemGroupName=%s]的数据已存在,请使用其他名称",itemGroup.getItemGroupName());
            logger.error(msg);
            throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_QUERY_EXCEPTION,msg);
        }
        itemGroup.setChannelCode(aclUserAccreditInfo.getChannelCode());
        ParamsUtil.setBaseDO(itemGroup);
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
        saveItemGroupUserList(groupUserList,isValid,aclUserAccreditInfo.getChannelCode());



        //保存商品组与授权用户关系
        String memberUserId = itemGroup.getMemberUserId();//组员id1，id2,id3
        List<ItemGroupUserRelation> itemGroupUserRelationList=new ArrayList<>();
        //添加组长
        ItemGroupUserRelation itemGroupUserRelation = new ItemGroupUserRelation();
        itemGroupUserRelation.setItemGroupCode(itemGroup.getItemGroupName());
        itemGroupUserRelation.setUserId(leaderName);
        ParamsUtil.setBaseDO(itemGroupUserRelation);
        itemGroupUserRelation.setIsValid(isValid);
        itemGroupUserRelationList.add(itemGroupUserRelation);
        //添加组员
        for (String memberId: memberUserId.split(SupplyConstants.Symbol.COMMA)) {
             itemGroupUserRelation = new ItemGroupUserRelation();
            itemGroupUserRelation.setItemGroupCode(itemGroup.getItemGroupName());
            itemGroupUserRelation.setUserId(memberId);
            ParamsUtil.setBaseDO(itemGroupUserRelation);
            itemGroupUserRelation.setIsValid(isValid);
            itemGroupUserRelationList.add(itemGroupUserRelation);
        }
        Integer countList=iItemGroupUserRelationService.insertList(itemGroupUserRelationList);
        if (countList==null){
            String msg="保存商品组成员失败";
            logger.error(msg);
            throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_SAVE_EXCEPTION,msg);
        }

        //记录日志
        logInfoService.recordLog(itemGroup,itemGroup.getId().toString(),aclUserAccreditInfo.getUserId().toString(), LogOperationEnum.ADD.getMessage(),"","");

    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.ITEM_GROUP)
    public ItemGroup findItemGroupByName(String name) {
        AssertUtil.notBlank(name,"根据商品组名称查询采购组的参数itemGroupName为空");
        ItemGroup itemGroup = new ItemGroup();
        itemGroup.setItemGroupName(name);
        return itemGroupService.selectOne(itemGroup);
    }

    private void saveItemGroupUserList(List<ItemGroupUser> groupUserList,String isValid,String channelCode){
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
            ParamsUtil.setBaseDO(itemGroupUser);
            itemGroupUser.setIsValid(isValid);
            //手机号添加之前判断是否存在
            if (list.contains(phoneNumber)){
                String msg=itemGroupUser.getPhoneNumber()+"该手机号在组内已存在！";
                logger.error(msg);
                throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_SAVE_EXCEPTION,msg);
            }
            list.add(phoneNumber);
        }

        itemGroupUserService.insertList(groupUserList);
        list.clear();
    }


}
