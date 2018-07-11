package com.springframwork.beans;

import com.springframwork.aop.AopConfig;
import com.springframwork.aop.AopProxy;
import com.springframwork.core.FactoryBean;

/**
 * 包装类，提供了一系列操作JavaBean的方法
 * 
 * @author tanzhe
 *
 */
public class BeanWrapper extends FactoryBean {
	// 代理对象
	private AopProxy aopProxy = new AopProxy();
	
	private BeanPostProcessor postProcessor;
	
	private Object wrapperInstance;
	// 原始对象通过反射创建出来，再包装一层，这一层就是代理
	private Object originalInstance;
	
	public BeanWrapper(Object instance) {
		this.wrapperInstance = aopProxy.getProxy(instance);
		this.originalInstance = instance;
	}
	
	public BeanPostProcessor getPostProcessor() {
		return postProcessor;
	}
	
	public void setPostProcessor(BeanPostProcessor postProcessor) {
		this.postProcessor = postProcessor;
	}
	
	/**
	 * 返回包装后的对象（代理对象）
	 * @return
	 */
	public Object getWrapperInstance() {
		return this.wrapperInstance;
	}
	
	// 返回代理以后的class
	public Class<?> getWrapperClass() {
		return this.wrapperInstance.getClass();
	}
	
	public void setAopConfig(AopConfig config) {
		aopProxy.setConfig(config);
	}
	
	/**
	 * 返回原始对象
	 * @return
	 */
	public Object getOriginalInstance() {
		return this.originalInstance;
	}
}
