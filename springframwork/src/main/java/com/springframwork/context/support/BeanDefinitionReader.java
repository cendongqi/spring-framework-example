package com.springframwork.context.support;

import com.springframwork.beans.BeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @description:对Spring配置文件进行查找，读取、解析
 * @author:tz
 * @date:Created in 下午1:15 2018/7/9
 */
public class BeanDefinitionReader {
    // 存配置
    private Properties config = new Properties();

    // 注册到spring中的类名
    private List<String> registyBeanClasses = new ArrayList<String>();

    // 配置文件中，配置扫描报名的key
    private final String SCAN_PACKAGE = "scanPackage";

    // 读取配置文件的方法，可以指定多个配置文件
    public BeanDefinitionReader(String...locations) {
        // 通过reader去查找和定位
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if ( is != null) {
                    is.close();
                }
            } catch (Exception e) {

            }
        }

        doScanner(config.getProperty("scanPackage"));
    }

    /**
     * 扫描所有的相关联的class，并且保存到一个list中
     *
     * @param packageName
     */
    private void doScanner(String packageName) {
        // 将扫描包名中的字符.替换成路径/
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));
        File classDir = new File(url.getFile());

        // 循环深入查找所有的class文件，并添加到集合中
        for (File file : classDir.listFiles()) {
            if (file.isDirectory()) {
                doScanner(packageName + "." + file.getName());
            } else {
                registyBeanClasses.add(packageName + "." + file.getName().replace(".class", ""));
            }
        }
    }

    public Properties getConfig() {
        return config;
    }

    public List<String> loadBeanDefinitions() {
        return this.registyBeanClasses;
    }

    public BeanDefinition registerBean(String className) {
        if (this.registyBeanClasses.contains(className)) {
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setBeanClassName(className);
            beanDefinition.setFactoryBeanName(lowerFirstCase(className.substring(className.lastIndexOf("." + 1))));
            return beanDefinition;
        }
        return null;
    }

    private String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
