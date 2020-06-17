package com.dim4tech.serviceclient.service.client;

import com.dim4tech.serviceclient.api.controller.HelloWorldHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HelloWorldJaxRsProxy {

    private final HelloWorldHandler helloWorldHandler;

    public String getServiceProviderResponse(String username) {
        return helloWorldHandler.getHelloWorldResponse(username).getMessage();
    }
}
