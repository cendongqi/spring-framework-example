package com.springframwork.core;

/**
 * @description:
 * @author:tz
 * @date:Created in 下午12:45 2018/7/9
 */
public interface BeanFactory {

    /**
     * 根据名称从IOC容器中获取示例
     *
     * @param beanName
     * @return
     */
    Object getBean(String beanName);
}
