package com.dim4tech.serviceprovider.api.service;

import reactor.core.publisher.Mono;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/hello")
public interface HelloWorldReactiveHandler {

    @GET
    Mono<HelloWorldResponse> getHelloWorldResponse(@QueryParam("name") String name);
}