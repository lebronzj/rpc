package com.zuzuche.rpc.client;

import java.lang.annotation.*;

/**
 * @author zhouj
 * @since 2020-08-04
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Client {
}
