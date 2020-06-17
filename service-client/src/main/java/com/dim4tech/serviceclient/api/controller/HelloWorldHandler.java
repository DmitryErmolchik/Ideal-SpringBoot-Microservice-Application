package com.dim4tech.serviceclient.api.controller;

import com.dim4tech.serviceclient.api.dto.HelloWorldResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

@Path("/hello")
public interface HelloWorldHandler {

    @GET
    HelloWorldResponse getHelloWorldResponse(@QueryParam("name") String name);
}