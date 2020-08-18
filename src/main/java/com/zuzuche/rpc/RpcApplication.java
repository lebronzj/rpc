package com.zuzuche.rpc;

import com.zuzuche.rpc.registry.ServerRegister;
import com.zuzuche.rpc.server.RpcServer;
import com.zuzuche.rpc.service.TestService;
import com.zuzuche.rpc.service.impl.TestServiceImpl;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author zhouj
 * @since 2020-08-04
 */
@SpringBootApplication
public class RpcApplication implements ApplicationContextAware {


    public static void main(String[] args) {
        ServerRegister serverRegister = new ServerRegister("127.0.0.1:2181");
        RpcServer rpcServer = new RpcServer(serverRegister, "127.0.0.1:18866");
        rpcServer.addService(TestService.class.getName(), new TestServiceImpl());
        SpringApplication springApplication = new SpringApplication(RpcApplication.class);
        springApplication.run(args);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        for (String s : applicationContext.getBeanDefinitionNames()) {
            if (s.equals("com.zuzuche.rpc.service.TestService")) {
                Object o = applicationContext.getBean(s);
                System.out.println(o.getClass().getCanonicalName());
            }
            System.out.println(s);
        }
    }
}
