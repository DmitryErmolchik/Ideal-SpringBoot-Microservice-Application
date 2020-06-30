package com.dim4tech.serviceprovider.configuration;

import com.dim4tech.serviceprovider.api.service.HelloWorldReactiveHandler;
import feign.jaxrs.JAXRSContract;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactivefeign.webclient.WebReactiveFeign;

@RequiredArgsConstructor
@Configuration
@ConditionalOnClass(HelloWorldReactiveHandler.class)
@EnableConfigurationProperties(ServiceProviderConfigurationProperties.class)
public class ServiceProviderStarterConfiguration {

    private final ServiceProviderConfigurationProperties serviceProviderConfigurationProperties;

    @Bean
    public HelloWorldReactiveHandler helloWorldClient() {
        return WebReactiveFeign.<HelloWorldReactiveHandler>builder()
                .contract(new JAXRSContract())
                .target(HelloWorldReactiveHandler.class, serviceProviderConfigurationProperties.getUrl());
    }
}