package com.springframwork.context;

/**
 * @description:Spring应用上下文中最重要的一个类，这个抽象类中提供了几乎ApplicationContext的所有操作
 * @author:tz
 * @date:Created in 下午12:53 2018/7/9
 */
public abstract class AbstractApplicationContext {

    /**
     * 子类重写
     * 创建加载Spring容器配置（包括.xml配置，property文件和数据库模式等）
     */
    protected void onRefresh() {

    }

    /**
     * 构造bean的方法
     */
    protected abstract void refreshBeanFactory();
}
