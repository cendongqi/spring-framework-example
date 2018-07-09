package com.springframwork.beans;

/**
 * @description:用来存储配置文件中的信息，相当于保存在内存中的配置
 * @author:tz
 * @date:Created in 下午12:44 2018/7/9
 */
public class BeanDefinition {
    private String beanClassName;

    private boolean lazyinit = false;

    private String factoryBeanName;

    public String getBeanClassName() {
        return beanClassName;
    }

    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
    }

    public boolean isLazyinit() {
        return lazyinit;
    }

    public void setLazyinit(boolean lazyinit) {
        this.lazyinit = lazyinit;
    }

    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    public void setFactoryBeanName(String factoryBeanName) {
        this.factoryBeanName = factoryBeanName;
    }
}
