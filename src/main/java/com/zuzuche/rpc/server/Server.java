package com.zuzuche.rpc.server;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author zhouj
 * @since 2020-08-04
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Server {
}
