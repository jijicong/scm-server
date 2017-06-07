package org.trc.biz.impower;

import com.tairanchina.md.account.user.model.UserDO;
import org.trc.domain.System.Channel;
import org.trc.domain.impower.Role;
import org.trc.domain.impower.UserAccreditInfo;
import org.trc.domain.impower.UserAddPageDate;
import org.trc.form.impower.UserAccreditInfoForm;
import org.trc.util.Pagenation;

import java.util.List;

/**
 * Created by sone on 2017/5/11.
 */
public interface IUserAccreditInfoBiz {
    /**
     * 分页查询授权信息
     *
     * @param form 授权信息查询条件
     * @param page 分页信息
     * @return 分页信息及当前页面的数据
     */
    Pagenation<UserAddPageDate> userAccreditInfoPage(UserAccreditInfoForm form, Pagenation<UserAddPageDate> page) throws Exception;

    /**
     * 查询拥有采购员角色的用户
     *
     * @return 采购员列表
     * @throws Exception
     */
    List<UserAccreditInfo> findPurchase() throws Exception;

    /**
     * 修改授权用户的状态
     *
     * @param
     * @throws Exception
     */
    void updateUserAccreditInfoStatus(UserAccreditInfo userAccreditInfo) throws Exception;

    /**
     * 根据名称查询用户授权信息
     *
     * @param name 用户姓名
     * @return 用户授权信息
     * @throws Exception
     */
    int checkUserByName(Long id, String name) throws Exception;

    /**
     * 处理用户显示页面的角色拼接和用户显示页面的对象的转换
     *
     * @param list
     * @return
     * @throws Exception
     */
    List<UserAddPageDate> handleRolesStr(List<UserAccreditInfo> list) throws Exception;

    /**
     * 查询已启用的渠道
     *
     * @return
     * @throws Exception
     */
    List<Channel> findChannel() throws Exception;

    /**
     * 查询渠道角色或者全局角色
     *
     * @return
     * @throws Exception
     */
    List<Role> findChannelOrWholeJur(String roleType) throws Exception;

    /**
     * 新增授权
     */
    void saveUserAccreditInfo(UserAddPageDate userAddPageDate) throws Exception;

    /**
     * 根据ID查询用户
     *
     * @param id
     * @return
     * @throws Exception
     */
    UserAccreditInfo findUserAccreditInfoById(Long id) throws Exception;

    /**
     * 修改授权
     *
     * @param userAddPageDate
     * @throws Exception
     */
    void updateUserAccredit(UserAddPageDate userAddPageDate) throws Exception;

    /**
     * 手机号校验
     *
     * @param phone
     * @return
     * @throws Exception
     */
    String checkPhone(String phone) throws Exception;

    /**
     * 新增时用户姓名是否已经被使用
     */
//    int checkName(String name) throws Exception;
}
