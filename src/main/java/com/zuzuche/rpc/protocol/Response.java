package com.zuzuche.rpc.protocol;

import lombok.Data;

/**
 * @author zhouj
 * @since 2020-08-03
 */
@Data
public class Response {

    private String requestId;

    private String error;

    private Object result;

    private Long timestamp;
}
