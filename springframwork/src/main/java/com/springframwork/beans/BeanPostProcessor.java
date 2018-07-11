package com.springframwork.beans;

/**
 * 用做事件监听的类
 * 
 * @author tanzhe
 *
 */
public class BeanPostProcessor {
	public Object postProcessBeforeInitialization(Object bean, String beanName) {
		return bean;
	}
	
	public Object postProcessAfterInitialization(Object bean, String beanName) {
		return bean;
	}
}
