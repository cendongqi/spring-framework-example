package com.springframwork.context;

import com.springframwork.beans.BeanDefinition;
import com.springframwork.context.support.BeanDefinitionReader;
import com.springframwork.core.BeanFactory;

import java.util.List;

/**
 * @description:
 * @author:tz
 * @date:Created in 下午1:13 2018/7/9
 */
public class ApplicationContext extends DefaultListableBeanFactory implements BeanFactory {

    // 配置文件的位置
    private String[] configLocations;

    private BeanDefinitionReader reader;

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

    @Override
    public Object getBean(String beanName) {
        return null;
    }
}
