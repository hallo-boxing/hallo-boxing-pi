package org.xiaoheshan.hallo.boxing.pi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * @author : _Chf
 * @since : 03-22-2018
 */
@Component
@lombok.Data
@ConfigurationProperties(prefix = "server")
@PropertySource(value = "classpath:server.properties", encoding = "UTF-8")
public class ServerProperty {

    private String name;

    private String ip;

    private Integer port;
}
