package org.snipsnap.container;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import java.util.Collection;

public class SpringContainer implements Container {
    private DefaultListableBeanFactory factory;

    public SpringContainer() {
    }

    public void init() {
        ClassPathResource res = new ClassPathResource("conf/beans.xml");
        factory = new XmlBeanFactory(res);
    }

    public Object getComponent(Class c) {
        return factory.getBean(c.getName());
    }

    public Collection findComponents(Class c) {
        return factory.getBeansOfType(c).values();
    }

    public boolean containsComponent(Class c) {
        return factory.containsBean(c.getName());
    }

    public void addComponent(Class c) {
        RootBeanDefinition definition = new RootBeanDefinition(c);
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_CONSTRUCTOR);
        factory.registerBeanDefinition(c.getName(), definition);
    }

     public Object getComponent(String id) {
         return factory.getBean(id);
    }
}

