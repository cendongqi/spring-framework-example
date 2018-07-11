package com.springframwork.context;

import com.springframwork.annotation.Autowired;
import com.springframwork.annotation.Controller;
import com.springframwork.annotation.Service;
import com.springframwork.aop.AopConfig;
import com.springframwork.beans.BeanDefinition;
import com.springframwork.beans.BeanPostProcessor;
import com.springframwork.beans.BeanWrapper;
import com.springframwork.context.support.BeanDefinitionReader;
import com.springframwork.core.BeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @description:
 * @author:tz
 * @date:Created in 下午1:13 2018/7/9
 */
public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {

    // 配置文件的位置
    private String[] configLocations;

    private BeanDefinitionReader reader;
    
    // 用来保存实例化过（注册式单例）的对象的容器
    private Map<String, Object> beanCacheMap = new HashMap<String, Object>();
    
    // 用来存储所有代理对象
    private Map<String, BeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, BeanWrapper>();

    public ApplicationContext(String[] configLocations) {
        this.configLocations = configLocations;
        refresh();
    }

    public void refresh() {
        // 定位
        this.reader = new BeanDefinitionReader(configLocations);

        // 加载
        List<String> beanDefinitions = reader.loadBeanDefinitions();

        // 注册
        doRegisty(beanDefinitions);
    }

    // 真正将BeanDefinitions注册到beanDefinitionMap中
    private void doRegisty(List<String> beanDefinitions) {
        /**
         * beanName有三种情况
         * 1.默认类名首字母小写
         * 2.自定义名字
         * 3.接口注入
         */
        try {
            for (String className : beanDefinitions) {
                Class<?> beanClass = Class.forName(className);
                // 如果是一个接口，则不能实例化
                // 用它的实现类来实例化
                if (beanClass.isInterface()) {
                    continue;
                }

                BeanDefinition beanDefinition = reader.registerBean(className);
                if (beanDefinition != null) {
                    this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
                }

                Class<?>[] interfaces = beanClass.getInterfaces();
                for (Class<?> i : interfaces) {
                    // 如果是多个实现类，只能覆盖
                    // 为什么？因为Spring没那么智能，就是这么傻
                    // 这个时候，可以自定义名字
                    this.beanDefinitionMap.put(i.getName(), beanDefinition);
                }

                // 到这里容器初始化完毕
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * 依赖注入，从这个地方开始，读取BeanDefinitionMap中的信息
     */
    @Override
    public Object getBean(String beanName) {
    	// 根据bean的名称获取beanDefinition
    	BeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
    	// 获取bean的class名称
    	String className = beanDefinition.getBeanClassName();
    	
    	try {
    		// 生成通知事件
        	BeanPostProcessor beanPostProcessor = new BeanPostProcessor();
        	
        	// 实例化这个对象
        	Object instance = instantionBean(beanDefinition);
        	if (instance == null) {
        		return null;
        	}
        	
        	// 在实例初始化以前调用一次
        	beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
        	
        	// 实例初始化（包装，代理）
        	BeanWrapper beanWrapper = new BeanWrapper(instance);
        	beanWrapper.setAopConfig(instantionAopConfig(beanDefinition));
        	beanWrapper.setPostProcessor(beanPostProcessor);
        	this.beanWrapperMap.put(beanName, beanWrapper);
        	
        	// 在实例初始化后调用一次
        	beanPostProcessor.postProcessAfterInitialization(instance, beanName);
        	
        	// 通过这样一调用，相当于给我们自己留有了可操作的空间
        	return this.beanWrapperMap.get(beanName).getWrapperInstance();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        return null;
    }

    /**
     * 传入一个BeanDefinition，返回一个bean实例
     * @param beanDefinition
     */
	private Object instantionBean(BeanDefinition beanDefinition) {
		Object instance = null;
		String className = beanDefinition.getBeanClassName();
		
		try {
			// beanDefinitionMap是否包含这个类的实例
			if (this.beanDefinitionMap.containsKey(className)) {
				// 这个对象曾经实例化过
				instance = this.beanCacheMap.get(className);
			} else {
				// 这个对象没有被实例化过
				// 获取这个类
				Class<?> clazz = Class.forName(className);
				// 实例化对象
				instance = clazz.newInstance();
			}
			return instance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 读取配置文件初始化config类
	 * 
	 * @param beanDefinition
	 * @return
	 * @throws Exception
	 */
	private AopConfig instantionAopConfig(BeanDefinition beanDefinition) throws Exception {
		AopConfig config = new AopConfig();
		// 读取配置文件中的aop配置
		String expression = reader.getConfig().getProperty("pointCut");
		String[] before = reader.getConfig().getProperty("aspectBefore").split("\\s");
		String[] after = reader.getConfig().getProperty("aspectAfter").split("\\s");
		
		String className = beanDefinition.getBeanClassName();
		Class<?> clazz = Class.forName(className);
		
		Pattern pattern = Pattern.compile(expression);
		
		Class<?> aspectClass = Class.forName(before[0]);
		
		// 在这里得到的方法都是原生的方法
		for (Method m : clazz.getMethods()) {
			Matcher matcher = pattern.matcher(m.toString());
			if (matcher.matches()) {
				// 能满足切面规则的类，添加到aop配置中
				config.put(m, aspectClass.newInstance(), new Method[] {aspectClass.getMethod(before[1]), aspectClass.getMethod(after[1])});
			}
		}
		
		return config;
	}
	
	/**
	 * 依赖注入的方法
	 */
	private void doAutowired() {
		for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : this.beanDefinitionMap.entrySet()) {
			String beanClassName = beanDefinitionEntry.getKey();
			if (beanDefinitionEntry.getValue().isLazyinit()) {
				Object obj = getBean(beanClassName);
			}
		}
		
		for (Map.Entry<String, BeanWrapper> beanWrapperEntry : this.beanWrapperMap.entrySet()) {
			populateBean(beanWrapperEntry.getKey(), beanWrapperEntry.getValue().getOriginalInstance());
		}
	}
	
	/**
	 * 注入依赖的逻辑
	 * @param beanName	
	 * @param instance	要注入依赖的Controller,Service对象
	 */
	public void populateBean(String beanName, Object instance) {
		Class<?> clazz = instance.getClass();
		
		// 不是所有的地方都可以自动注入，只有Controller、Service的注解类才可以自动注入
		if (!(clazz.isAnnotationPresent(Controller.class)
				|| clazz.isAnnotationPresent(Service.class))) {
			return;
		}
		
		// 获取这个类的所有字段
		Field[] fields = clazz.getDeclaredFields();
		
		// 遍历字段，找出贴有Autowired注解的字段
		for (Field field : fields) {
			if (!field.isAnnotationPresent(Autowired.class)) {
				continue;
			}
			
			// 读取Autowired中的value值，此值就是实现类的名称
			Autowired autowired = field.getAnnotation(Autowired.class);
			String autowiredBeanName = autowired.value().trim();
			
			// 如果没有指定这个值，则获取这个字段的类型名称
			// 以此值为key在beanWrapperMap中找到它的代理对象
			if ("".equals(autowiredBeanName)) {
				autowiredBeanName = field.getType().getName();
			}
			// 将该字段设置为可读写
			field.setAccessible(true);
			try {
				// 注入这个类
				field.set(instance, this.beanWrapperMap.get(autowiredBeanName).getWrapperInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 获取注册类的名字
	 * @return
	 */
	public String[] getBeanDefinitionNames() {
		return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
	}
	
	/**
	 * 获取注册类的数量
	 * @return
	 */
	public int getBeanDefinitionCount() {
		return this.beanDefinitionMap.size();
	}
	
	/**
	 * 获取spring配置信息
	 * @return
	 */
	public Properties getConfig() {
		return this.reader.getConfig();
	}
}
