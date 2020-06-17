package com.dim4tech.serviceclient.configuration;

import com.dim4tech.serviceclient.api.controller.HelloWorldHandler;
import org.glassfish.jersey.client.proxy.WebResourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.ws.rs.client.ClientBuilder;

@Configuration
public class SpringConfiguration {

    @Value("${application.serviceprovider.url}")
    private String serviceProviderURL;

    @Bean
    public HelloWorldHandler helloWorldHandler() {
        return WebResourceFactory.newResource(HelloWorldHandler.class, ClientBuilder.newClient().target(serviceProviderURL));
    }

}
