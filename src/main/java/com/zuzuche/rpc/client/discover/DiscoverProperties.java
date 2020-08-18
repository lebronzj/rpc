package com.zuzuche.rpc.client.discover;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouj
 * @since 2020-08-03
 */
@Data
@ConfigurationProperties("rpc.zookeeper")
@Configuration
public class DiscoverProperties {

    private String address;

}
