package com.zuzuche.rpc.client;

import com.zuzuche.rpc.proxy.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * @author zhouj
 * @since 2020-08-04
 */
@Component
public class ClientRegistry implements BeanDefinitionRegistryPostProcessor {
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        ClassPathScanningCandidateComponentProvider classPathScanningCandidateComponentProvider = new ClassPathScanningCandidateComponentProvider(false) {
            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                if (beanDefinition.getMetadata().isIndependent()) {
                    // 判断接口是否继承了 Annotation注解
                    if (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata()
                            .getInterfaceNames().length == 1 && Annotation.class.getName().equals(beanDefinition.getMetadata().getInterfaceNames()[0])) {
                        try {
                            Class<?> target = ClassUtils.forName(beanDefinition.getMetadata().getClassName(),
                                    this.getClass().getClassLoader());
                            return !target.isAnnotation();
                        } catch (Exception ex) {
                            this.logger.error(
                                    "Could not load target class: " + beanDefinition.getMetadata().getClassName(), ex);
                        }
                    }
                    return true;
                }
                return false;
            }
        };
        TypeFilter typeFilter = new AnnotationTypeFilter(Client.class);
        classPathScanningCandidateComponentProvider.addIncludeFilter(typeFilter);
        Set<BeanDefinition> beanDefinitionSet = classPathScanningCandidateComponentProvider.findCandidateComponents("*");
        if (beanDefinitionSet.size() > 0) {
            beanDefinitionSet.forEach(beanDefinition -> {
                BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(ProxyFactory.class);
                beanDefinitionBuilder.addPropertyValue("type", beanDefinition.getBeanClassName());
                beanDefinitionBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
                beanDefinitionBuilder.setPrimary(true);
                beanDefinitionRegistry.registerBeanDefinition(beanDefinition.getBeanClassName(), beanDefinitionBuilder.getBeanDefinition());
            });
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
