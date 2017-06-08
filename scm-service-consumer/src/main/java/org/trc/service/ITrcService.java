package org.trc.service;


/**
 * 通知泰然城
 * Created by hzdzf on 2017/6/6.
 */
public interface ITrcService {


    /**
     * @param brandUrl
     * @param params
     * @return
     * @throws Exception
     */
    String sendBrandNotice(String brandUrl, String params) throws Exception;

    /**
     * @param categoryPropertyUrl
     * @param params
     * @return
     * @throws Exception
     */
    String sendCategoryPropertyList(String categoryPropertyUrl, String params) throws Exception;

    /**
     * @param categoryBrandUrl
     * @param params
     * @return
     * @throws Exception
     */
    String sendCategoryBrandList(String categoryBrandUrl, String params) throws Exception;

    /**
     * @param categoryUrl
     * @param params
     * @return
     * @throws Exception
     */
    String sendCategoryToTrc(String categoryUrl, String params) throws Exception;

    /**
     * @param propertyUrl
     * @param params
     * @return
     * @throws Exception
     */
    String sendPropertyNotice(String propertyUrl, String params) throws Exception;
}
