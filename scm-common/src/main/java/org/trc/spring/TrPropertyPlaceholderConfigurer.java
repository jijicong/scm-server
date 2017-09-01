package org.trc.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.trc.util.DESUtil;

import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by george on 2016/11/16.
 */
public class TrPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
    private final static Logger log = LoggerFactory.getLogger(TrPropertyPlaceholderConfigurer.class);
    private Properties props;

    public TrPropertyPlaceholderConfigurer() {
    }

    public Properties getProps() {
        return this.props;
    }

    public void setProps(Properties props) {
        this.props = props;
    }

    protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
        this.props = props;
        //将Property做改进
        Enumeration<String> enumeration = (Enumeration<String>) props.propertyNames();

        while (enumeration.hasMoreElements()) {
            try {
                String name = enumeration.nextElement();
                String value = (String) props.get(name);
                value = handleDecryptString(value);
                props.setProperty(name, value);
            } catch (Exception e) {
                log.warn("properties初始化赋值异常,e:{},message:{}", e, e.getMessage());
            }
        }
        super.processProperties(beanFactoryToProcess, props);
    }

    /**
     * 根据正则表达式匹配properties 中的值是否需要被解密
     *
     * @param value
     * @return
     */
    private String handleDecryptString(String value) throws Exception {
        Pattern p = Pattern.compile("!(.*?)!");
        Matcher m = p.matcher(value);
        while (m.find()) {
            return DESUtil.decrypt(m.group(1), ConsumerConstant.DES_KEY);
        }
        return value;
    }
}
