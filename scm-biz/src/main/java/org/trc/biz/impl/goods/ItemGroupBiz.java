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

    private final static Integer LENGTH = 5;//商品组编号后5位流水号


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

    @Override
    public ItemGroup queryDetailByCode(String code) {
        AssertUtil.notBlank(code,"根据商品组编码查询商品组的参数code为空");
        ItemGroup itemGroup = new ItemGroup();
        itemGroup.setItemGroupCode(code);
        return itemGroupService.selectOne(itemGroup);
        //TODO
    }

    @Override
    public void editDetail(ItemGroup itemGroup) {
        AssertUtil.notNull(itemGroup,"根据商品组信息修改商品组失败,商品组信息为null");
          //TODO

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor = Exception.class)
    public void itemGroupSave(ItemGroup itemGroup,List<ItemGroupUser> groupUserList,AclUserAccreditInfo aclUserAccreditInfo) {
        AssertUtil.notNull(itemGroup,"商品组管理模块新增商品组信息失败,商品组信息为null");
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
        int count = itemGroupService.insertSelective(itemGroup);
        if (count==0){
            String msg="商品组保存失败，数据库操作失败";
            logger.error(msg);
            throw new ItemGroupException(ExceptionEnum.ITEM_GROUP_SAVE_EXCEPTION,msg);
        }

        //保存商品组员列表数据
        saveItemGroupUserList(groupUserList,aclUserAccreditInfo.getChannelCode());



        //保存商品组与授权用户关系
        String leaderName = itemGroup.getLeaderName();
        String memberUserId = itemGroup.getMemberUserId();//组员id1，id2,id3
        //商品组启用，则组长为启用，否则视同组员
        String isValid = itemGroup.getIsValid();

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
        //TODO

    }

    @Override
    @Cacheable(value = SupplyConstants.Cache.ITEM_GROUP)
    public ItemGroup findItemGroupByName(String name) {
        AssertUtil.notBlank(name,"根据商品组名称查询采购组的参数itemGroupName为空");
        ItemGroup itemGroup = new ItemGroup();
        itemGroup.setItemGroupName(name);
        return itemGroupService.selectOne(itemGroup);
    }

    private void saveItemGroupUserList(List<ItemGroupUser> groupUserList,String channelCode){

    }


}
