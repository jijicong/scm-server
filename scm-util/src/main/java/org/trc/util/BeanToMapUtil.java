package org.trc.util;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.trc.enums.CommonExceptionEnum;
import org.trc.exception.DataHandlerException;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 吴东雄
 * @ClassName: BeanToMapUtil
 * @Description: TODO
 * @date 2015年11月18日 下午3:00:41
 */
public class BeanToMapUtil {

    private final static Logger log = LoggerFactory.getLogger(BeanToMapUtil.class);

    /**
     * 将一个 Map 对象转化为一个 JavaBean
     *
     * @param type 要转化的类型
     * @param map  包含属性值的 map
     * @return 转化出来的 JavaBean 对象
     * @throws IntrospectionException    如果分析类属性失败
     * @throws IllegalAccessException    如果实例化 JavaBean 失败
     * @throws InstantiationException    如果实例化 JavaBean 失败
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
     */
    public static Object convertMapToBean(Class<?> type, Map<String, Object> map) throws Exception {
        Object obj = new Object();
        String propName = "";
        Object propVal = "";
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(type); // 获取类属性
            obj = type.newInstance();
            // 创建 JavaBean 对象
            // 给 JavaBean 对象的属性赋值
            PropertyDescriptor[] propertyDescriptors = beanInfo
                    .getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                propName = descriptor.getName();
                if (map.containsKey(propName)) {
                    // 下面一句可以 try 起来，这样当一个属性赋值失败的时候就不会影响其他属性赋值。
                    propVal = map.get(propName);
                    //处理数字类型
                    if (StringUtils.equals(Double.class.getName(), descriptor.getPropertyType().getName())) {
                        if (propVal instanceof String) {
                            if (StringUtils.isNotEmpty(propVal.toString()))
                                propVal = Double.parseDouble((String) propVal);
                            else
                                propVal = new Double(0);
                        } else if (propVal instanceof Integer)
                            propVal = ((Integer) propVal).doubleValue();
                        else if (propVal instanceof Float)
                            propVal = ((Float) propVal).doubleValue();
                    }

                    //处理字符串类型
                    if (StringUtils.equals(String.class.getName(), descriptor.getPropertyType().getName())) {
                        propVal = String.valueOf(propVal);
                    }

                    if (null != propVal && StringUtils.isNotEmpty(propVal.toString())) {
                        Object[] args = new Object[1];
                        args[0] = propVal;
                        descriptor.getWriteMethod().invoke(obj, args);
                    }
                }
            }
        } catch (Exception e) {
            StringBuilder builder = new StringBuilder();
            builder.append("map对象 ").append(map).append("转java对象")
                    .append(type.getName()).append("设置属性").append(propName)
                    .append("的属性值").append(propVal)
                    .append("异常,异常信息:").append(e.getMessage());
            log.error(builder.toString());
            throw new Exception(e);
        }
        return obj;
    }

    /**
     * 将一个 JavaBean 对象转化为一个  Map
     *
     * @param bean 要转化的JavaBean 对象
     * @return 转化出来的  Map 对象
     * @throws IntrospectionException    如果分析类属性失败
     * @throws IllegalAccessException    如果实例化 JavaBean 失败
     * @throws InvocationTargetException 如果调用属性的 setter 方法失败
     */
    public static Map<String, Object> convertBeanToMap(Object bean) {
        Map<String, Object> returnMap = new HashMap<String, Object>();
        try {
            if (null == bean)
                return returnMap;
            if (bean instanceof List) {
                if (CollectionUtils.isEmpty((List<?>) bean))
                    return returnMap;
            }
            if (bean.getClass().isArray()) {
                List<?> list = Arrays.asList(bean);
                if (list.size() == 0)
                    return returnMap;
            }
            Class<?> type = bean.getClass();
            returnMap = new HashMap<String, Object>();
            BeanInfo beanInfo = Introspector.getBeanInfo(type);
            PropertyDescriptor[] propertyDescriptors = beanInfo
                    .getPropertyDescriptors();
            for (int i = 0; i < propertyDescriptors.length; i++) {
                PropertyDescriptor descriptor = propertyDescriptors[i];
                String propertyName = descriptor.getName();
                if (!propertyName.equals("class")) {
                    Method readMethod = descriptor.getReadMethod();
                    Object result = readMethod.invoke(bean);
                    if (result != null) {
                        returnMap.put(propertyName, result);
                    } else {
                        returnMap.put(propertyName, "");
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new DataHandlerException(CommonExceptionEnum.JAVA_BEAN_TO_MAP_EXCEPTION, e.getMessage());
        } catch (InvocationTargetException e) {
            throw new DataHandlerException(CommonExceptionEnum.JAVA_BEAN_TO_MAP_EXCEPTION, e.getMessage());
        } catch (IntrospectionException e) {
            throw new DataHandlerException(CommonExceptionEnum.JAVA_BEAN_TO_MAP_EXCEPTION, e.getMessage());
        }
        return returnMap;
    }

    public static Map<String, String> mapObjToMapStr(Map<String, Object> parmMap) {
        Map<String, String> map = new HashMap<String, String>();
        for (Map.Entry<String, Object> entry : parmMap.entrySet()) {
            map.put(entry.getKey(), String.valueOf(entry.getValue() == null ? "" : entry.getValue()));
        }
        return map;
    }


}


