package com.springframwork.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.springframwork.aop.AopConfig.Aspect;

/**
 * 动态代理类，默认使用jdk动态代理
 * @author tanzhe
 *
 */
public class AopProxy implements InvocationHandler {
	
	// 动态代理的配置信息：目标对象，增强对象，增强方法
	private AopConfig config;
	// 这是要代理的对象
	private Object target;
	
	/**
	 * 获取代理对象
	 * 
	 * @param instance
	 * @return
	 */
	public Object getProxy(Object instance) {
		this.target = instance;
		Class<?> clazz = instance.getClass();
		return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);
	}
	
	public void setConfig(AopConfig config) {
		this.config = config;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 这里是代理的实现逻辑
		
		// 获取目标对象的要实现代理的方法
		Method m = this.target.getClass().getMethod(method.getName(), method.getParameterTypes());
		
		// 第一步：在调用目标对象的方法之前，执行增强的代码
		// 这里需要用原生方法去找，以代理方法去map中是找不到的
		if (config.contains(m)) {
			Aspect aspect = config.get(m);
			aspect.getPoints()[0].invoke(aspect.getAspect());
		}
		
		// 第二步：反射调用原始的方法
		Object obj = method.invoke(this.target, args);
		System.out.println(args);
		
		// 第三步：在原始方法调用之后要执行增强的代码
		if (config.contains(m)) {
			Aspect aspect = config.get(m);
			aspect.getPoints()[1].invoke(aspect.getAspect());
		}
	
		// 将原始返回值返回出去
		return obj;
	}
}
