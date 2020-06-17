package com.dim4tech.serviceclient.service.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class HelloWorldJaxRsProxyTest {

    @Autowired
    private HelloWorldJaxRsProxy client;

    @Test
    void getServiceProviderResponse() {
        assertEquals("Hello, Dim4Tech", client.getServiceProviderResponse("Dim4Tech"));
    }
}