package com.dim4tech.serviceprovider.service.controller;

import com.dim4tech.serviceprovider.api.service.HelloWorldReactiveHandler;
import com.dim4tech.serviceprovider.api.service.HelloWorldResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/hello")
public class HelloWorldReactiveHandlerImpl implements HelloWorldReactiveHandler {

    @GetMapping
    public Mono<HelloWorldResponse> getHelloWorldResponse(@RequestParam(name = "name", required = false) String name) {
        return Mono.just(new HelloWorldResponse(String.format("Hello, %s", name)));
    }
}