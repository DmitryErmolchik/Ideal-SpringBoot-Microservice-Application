package com.dim4tech.serviceprovider.api.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/hello")
public interface HelloWorldHandler {

    @GET
    HelloWorldResponse getHelloWorldResponse(@QueryParam("name") String name);
}