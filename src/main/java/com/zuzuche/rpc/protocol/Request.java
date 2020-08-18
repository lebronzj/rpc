package com.zuzuche.rpc.protocol;

import lombok.Data;

/**
 * @author zhouj
 * @since 2020-08-03
 */
@Data
public class Request {

    private String requestId;

    private String methodName;

    private String interfaceName;

    private String version;

    /**
     * 调用方法的参数列表类型
     */
    private Class[] paramTypes;
    /**
     * 调用服务传参
     */
    private Object[] params;

    private Long timestamp;

}
