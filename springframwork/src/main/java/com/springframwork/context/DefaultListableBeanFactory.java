package com.springframwork.context;

import com.springframwork.beans.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:最基本的IOC容器，Spring注册及加载的默认实现
 * @author:tz
 * @date:Created in 下午1:00 2018/7/9
 */
public class DefaultListableBeanFactory extends AbstractApplicationContext {
    // 保存bean的信息
    protected Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();


    @Override
    protected void onRefresh() {

    }

    @Override
    protected void refreshBeanFactory() {

    }
}
