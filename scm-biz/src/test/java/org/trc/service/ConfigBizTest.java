package org.trc.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.trc.biz.config.IConfigBiz;
import org.trc.domain.dict.Dict;
import org.trc.domain.dict.DictType;
import org.trc.enums.ZeroToNineEnum;
import org.trc.form.config.DictForm;
import org.trc.form.config.DictTypeForm;
import org.trc.util.Pagenation;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created by hzwdx on 2017/4/21.
 */
@RunWith(SpringJUnit4ClassRunner.class)  //标记测试运行的环境
@ContextConfiguration({"classpath:config/resource-context.xml"}) //配合spring测试  可以引入多个配置文件
@TestExecutionListeners(TransactionalTestExecutionListener.class)
@Transactional
public class ConfigBizTest extends AbstractJUnit4SpringContextTests {

    @Resource
    private IConfigBiz configBiz;

    private DictType createDictType() {
        DictType dictType = new DictType();
        dictType.setCode("testWdx");
        dictType.setName("testWdx");
        //dictType.setIsValid(ZeroToNineEnum.ONE.getCode());
        dictType.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        dictType.setDescription("testWdx");
        dictType.setCreateTime(new Date());
        dictType.setUpdateTime(new Date());
        dictType.setCreateOperator("testWdx");
        return dictType;
    }

    private Dict createDict() {
        Dict dict = new Dict();
        dict.setTypeCode("testWdx");
        dict.setName("testWdx");
        dict.setValue("testWdx");
        //dict.setIsValid(ZeroToNineEnum.ONE.getCode());
        dict.setIsDeleted(ZeroToNineEnum.ZERO.getCode());
        dict.setCreateTime(new Date());
        dict.setUpdateTime(new Date());
        dict.setCreateOperator("testWdx");
        return dict;
    }


    @Test
    public void testDictType() {
        /**
         * 一、正常场景测试
         */
        try {
            DictType dictType = createDictType();
            /**
             * 测试保存字典类型saveDictType
             */
            configBiz.saveDictType(dictType);
            /**
             * 测试字典类型分页查询dictTypePage
             */
            DictTypeForm dictTypeForm = new DictTypeForm();
            dictTypeForm.setName("testWdx");
            //dictType.setIsValid(ZeroToNineEnum.ONE.getCode());
            Pagenation<DictType> page = new Pagenation<DictType>();
            page = configBiz.dictTypePage(dictTypeForm, page);
            Assert.assertEquals(1, page.getTotalCount());
            /**
             *测试查询字典类型列表queryDictTypes
             */
            DictTypeForm dictTypeForm1 = new DictTypeForm();
            dictTypeForm1.setName("textWdx");
            dictTypeForm1.setIsValid("1");
            List<DictType> list = configBiz.queryDictTypes(dictTypeForm1);
            Assert.assertNotEquals(0, list.size());
            /**
             * 测试根据主键查询字典类型findDictTypeById
             */
            DictType dictTypeTmp = configBiz.findDictTypeById(dictType.getId());
            Assert.assertEquals("testWdx", dictTypeTmp.getName());
            /**
             * 测试根据类型编号查询字典类型findDictTypeByTypeNo
             */
            DictType dictTypeTmp2 = configBiz.findDictTypeByTypeNo(dictType.getCode());
            Assert.assertEquals("testWdx", dictTypeTmp2.getName());
            /**
             * 测试修改字典类型updateDictType
             */
            DictType dictType2 = new DictType();
            dictType2.setName("testWdx2");
            dictType2.setUpdateTime(new Date());
            configBiz.updateDictType(dictType2);
            /**
             *测试根据主键删除字典类型deleteDictTypeById
             */
            configBiz.deleteDictTypeById(dictType.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 一、异常场景测试
         */
        /**
         * 测试更新字典类型updateDictType
         */
        try {
            DictType dictType = createDictType();
            configBiz.updateDictType(dictType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 测试按ID查找字典类型findDictTypeById
         */
        try {
            configBiz.findDictTypeById(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 测试按ID查找字典类型findDictTypeById
         */
        try {
            configBiz.findDictTypeById(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 根据类型编码查询字典类型findDictTypeByTypeNo
         */
        try {
            configBiz.findDictTypeByTypeNo("");
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 根据ID删除字典类型deleteDictTypeById
         */
        try {
            configBiz.deleteDictTypeById(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 修改字典参数updateDict
         */
        try {
            configBiz.updateDict(new Dict());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * 根根据ID删除字典deleteDictById
         */
        try {
            configBiz.deleteDictById(null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testDict() {
        try {
            Dict dict = createDict();
            /**
             * 测试保存字典saveDict
             */
            configBiz.saveDict(dict);
            /**
             * 测试字典分页查询dictPage
             */
            DictForm dictForm = new DictForm();
            dictForm.setTypeCode("testWdx");
            dictForm.setName("testWdx");
            dictForm.setIsValid(ZeroToNineEnum.ONE.getCode());
            Pagenation<Dict> page = new Pagenation<Dict>();
            page = configBiz.dictPage(dictForm, page);
            Assert.assertEquals(1, page.getTotalCount());
            /**
             * 测试根据主键查询字典findDictById
             */
            Dict dictTmp = configBiz.findDictById(dict.getId());
            Assert.assertEquals("testWdx", dictTmp.getName());
            /**
             * 测试修改字典updateDict
             */
            Dict dict2 = new Dict();
            dict2.setName("testWdx2");
            dict2.setUpdateTime(new Date());
            configBiz.updateDict(dict2);
            /**
             *测试根据主键删除字典deleteDictById
             */
            configBiz.deleteDictById(dict.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestThread() throws Exception {
        Runnable runnable = () -> {
            try {
                Thread.sleep(2000l);
                System.out.println("123");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread myThread = new Thread(runnable);
        myThread.start();
        System.out.println("536");}

}
