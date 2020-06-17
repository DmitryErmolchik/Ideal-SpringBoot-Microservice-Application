package com.dim4tech.serviceclient.service.client;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HellWorldJaxRsPlainTest {

    @Autowired
    private HellWorldJaxRsPlain client;

    @Test
    void getServiceProviderResponse() {
        StepVerifier.create(client.getServiceProviderResponse("Dim4Tech"))
                .expectSubscription()
                .expectNext("Hello, Dim4Tech")
                .verifyComplete();
    }
}