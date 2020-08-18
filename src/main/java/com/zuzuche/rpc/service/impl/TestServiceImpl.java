package com.zuzuche.rpc.service.impl;

import com.zuzuche.rpc.service.TestService;

/**
 * @author zhouj
 * @since 2020-08-04
 */
public class TestServiceImpl implements TestService {
    @Override
    public String say() {
        return "hello";
    }
}
