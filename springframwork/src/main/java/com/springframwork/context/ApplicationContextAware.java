package com.springframwork.context;

/**
 * ApplicationContext感知接口，实现该接口可获得Applicontext对象
 * @author tanzhe
 *
 */
public interface ApplicationContextAware {
	void setApplicationContext(ApplicationContext applicationContext);
}
