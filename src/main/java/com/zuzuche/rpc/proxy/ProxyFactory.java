package com.zuzuche.rpc.proxy;

import lombok.Data;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * @author zhouj
 * @since 2020-08-03
 */
@Data
public class ProxyFactory implements FactoryBean<Object> {

    public Class<?> type;

    @Override
    public Object getObject() throws Exception {
        return Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new ProxyObject(type));
    }

    @Override
    public Class<?> getObjectType() {
        return type;
    }
}
