package com.zuzuche.rpc.service;

import com.zuzuche.rpc.client.Client;

/**
 * @author zhouj
 * @since 2020-08-04
 */
@Client
public interface TestService {

    String say();
}
