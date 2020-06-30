package com.dim4tech.serviceprovider.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "application.serviceprovider")
public class ServiceProviderConfigurationProperties {

    private String url;

}
