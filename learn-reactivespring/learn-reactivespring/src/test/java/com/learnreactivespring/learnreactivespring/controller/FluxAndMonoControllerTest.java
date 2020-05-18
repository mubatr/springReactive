package com.learnreactivespring.learnreactivespring.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@WebFluxTest // Responsible to create instance of webTestclient
public class FluxAndMonoControllerTest {

    // In order to test Non Blocking code, Spring provides Non Blocking client known as WebTestClient. WebTestCLient is a subscriber
    @Autowired
    WebTestClient webTestClient;
    /**
     * Test case vlaidates Events coming from the Flux
     */
    @Test
    public void flux_approach1(){
        Flux<Integer> integerFlux =  webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange() // This will invoke the actual endpoint
                .expectStatus().isOk()// Validatting status to be Ok
                .returnResult(Integer.class)
                .getResponseBody(); // Response body is the one where we get the actual flux.


        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .verifyComplete();
    }

    @Test
    public void flux_approach2(){
            webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange() // This will invoke the actual endpoint
                .expectStatus().isOk()// Validatting status to be Ok
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Integer.class)
                .hasSize(5);
    }

    @Test
    public void flux_approach3(){

        List<Integer> expectedIntegerList = Arrays.asList(1,2,3,4);
        EntityExchangeResult<List<Integer>> entityExchangeResult = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)// converts Flux to a list.`
                .returnResult();

        assertEquals(expectedIntegerList,entityExchangeResult.getResponseBody());
    }


    @Test
    public void flux_approach4(){

        List<Integer> expectedIntegerList = Arrays.asList(1,2,3,4);

        webTestClient
                .get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith((response) -> {
                    assertEquals(expectedIntegerList,response.getResponseBody());
                });
                //.returnResult();

       // assertEquals(expectedIntegerList,entityExchangeResult.getResponseBody());
    }


    /**
     * Writing Test case for an Infinite Stream
     */
    @Test
    public void fluxstream(){
        Flux<Long> longFlux =  webTestClient.get().uri("/fluxstream")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange() // This will invoke the actual endpoint
                .expectStatus().isOk()// Validatting status to be Ok
                .returnResult(Long.class)
                .getResponseBody(); // Response body is the one where we get the actual flux.


        StepVerifier.create(longFlux)
                .expectSubscription()
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .expectNext(3L)
                .expectNext(4L)
                .thenCancel()
                .verify();
    }

    @Test
    public void mono(){

        Integer expectedValue = new Integer(1);

        webTestClient.get().uri("/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith((response) -> {
                   assertEquals(expectedValue, response.getResponseBody());
                });
    }


}
