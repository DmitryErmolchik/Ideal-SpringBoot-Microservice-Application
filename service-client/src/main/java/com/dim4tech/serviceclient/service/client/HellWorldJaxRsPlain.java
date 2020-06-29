package com.dim4tech.serviceclient.service.client;

import com.dim4tech.serviceprovider.api.service.HelloWorldResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class HellWorldJaxRsPlain {

    @Value("${application.serviceprovider.url}")
    private String serviceProviderURL;

    private Client client;

    @PostConstruct
    public void initialize() {
        client = ClientBuilder.newClient();
    }

    public Mono<String> getServiceProviderResponse(String username) {
        return Mono.fromFuture(() ->
                CompletableFuture.supplyAsync(() -> {
                    Future<HelloWorldResponse> future = client
                            .target(serviceProviderURL)
                            .path("/hello")
                            .queryParam("name", username)
                            .request(MediaType.APPLICATION_JSON_TYPE)
                            .buildGet()
                            .submit(HelloWorldResponse.class);
                    while (!future.isDone()) {
                        Thread.yield();
                    }
                    try {
                        return future.get();
                    } catch (InterruptedException | ExecutionException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                }))
                .map(HelloWorldResponse::getMessage);
    }

}