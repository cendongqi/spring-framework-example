package com.springframwork.aop;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 对application中的expression的封装
 * 1.目标代理对象的一个方法要增强
 * 2.自己实现的业务逻辑去增强
 * 3.配置文件的目的：告诉spring，哪些类的哪些方法需要增强，增强的内容是什么
 * 4.对配置文件中所体现的内容进行封装
 * 
 * @author tanzhe
 *
 */
public class AopConfig {
	
	// 以目标对象需要增强的method作为key，需要增强的代码内容作为value
	private Map<Method, Aspect> points = new HashMap<Method, Aspect>();
	
	/**
	 * 代理配置
	 * @param target	目标对象的方法
	 * @param aspect	增强对象
	 * @param points	增强对象的方法
	 */
	public void put(Method target, Object aspect, Method[] points) {
		this.points.put(target, new Aspect(aspect, points));
	}
	
	/**
	 * 获取增强对象
	 * @param method	目标对象的方法
	 * @return
	 */
	public Aspect get(Method method) {
		return this.points.get(method);
	}
	
	public boolean contains(Method method) {
		return this.points.containsKey(method);
	}
	
	public class Aspect {
		private Object aspect;
		
		private Method[] points;
		
		public Aspect(Object aspect, Method[] points) {
			this.aspect = aspect;
			this.points = points;
		}

		public Object getAspect() {
			return aspect;
		}

		public Method[] getPoints() {
			return points;
		}
	}
}
