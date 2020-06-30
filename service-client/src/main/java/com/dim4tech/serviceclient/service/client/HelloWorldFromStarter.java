package com.dim4tech.serviceclient.service.client;

import com.dim4tech.serviceprovider.api.service.HelloWorldReactiveHandler;
import com.dim4tech.serviceprovider.api.service.HelloWorldResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class HelloWorldFromStarter {

    private final HelloWorldReactiveHandler helloWorldReactiveClient;

    public Mono<String> getServiceProviderResponse(String username) {
        return helloWorldReactiveClient.getHelloWorldResponse(username)
                .map(HelloWorldResponse::getMessage);
    }
}
