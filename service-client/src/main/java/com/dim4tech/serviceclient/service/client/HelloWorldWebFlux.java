package com.dim4tech.serviceclient.service.client;

import com.dim4tech.serviceprovider.api.service.HelloWorldResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class HelloWorldWebFlux {
    @Value("${application.serviceprovider.url}")
    private String serviceProviderURL;

    public Mono<String> getServiceProviderResponse(String username) {
        WebClient client = WebClient.create(serviceProviderURL);
        return client.get().uri("/hello?name={name}", Map.of("name", username))
                .retrieve()
                .bodyToMono(HelloWorldResponse.class)
                .map(HelloWorldResponse::getMessage);
    }
}
