package com.learnreactivespring.learnreactivespring.handler;

import com.learnreactivespring.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.learnreactivespring.document.Item;
import com.learnreactivespring.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext // As this class will contain test case which will do read, write and update operations.
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemHandlerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    public List<Item> data() {

        return Arrays.asList(new Item(null,"Samsung TV", 399.0),
                new Item(null,"LG TV", 329.99),
                new Item(null,"Apple Watch", 349.99),
                new Item("ABC","Beats HeadPhones", 19.99));
    }

    // Method to setup some Test data before invoking Junit Test cases. test data will be pushed into Embeedded Mongo DB.
    @Before
    public void setUp(){

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .doOnNext( (item -> {
                    System.out.println("Inserted item is " + item);
                }))
                .blockLast();
    }


    // Test case to validate the size of API Response Size
    @Test
    public void getAllElements(){

        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()// exchange call actually connects to the Endpoint.
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4); // Assert is done Only on Size
    }

    // Test case - to validate if the Response contains all Non Null Ids
    @Test
    public void getAllitems_approach2(){

        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4)
                .consumeWith((response) -> {
                    List<Item> items = response.getResponseBody();
                    items.forEach((item) -> {
                        assertTrue(item.getId() != null); // If Id is not null, then that means actual value is inserted into the DB.
                    });
                });
    }


    @Test
    public void getAllitems_approach3() {

        Flux<Item> itemsFlux = webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemsFlux.log("Value from network : "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    /**
     *
     * Test case - Passing Valid ItemId and Asserting on API Response body contents.
     * Response will be Only one Item
     */
    @Test
    public void getOneItem(){
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"),"ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price",19.99);
    }

    /**
     * Test case - Passing Invalid Item and Asserting that API should return Not Found.
     */
    @Test
    public void getOneItem_NotFound(){
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"),"XYZ")
                .exchange()
                .expectStatus().isNotFound();
    }

    /**
     * Test Case to create a new Item. Assert on the expected response.
     */
    @Test
    public void createItemTest(){
        Item itm = new Item(null,"IPhone Reazor",13.43);

        webTestClient.post().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(itm),Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("IPhone Reazor")
                .jsonPath("$.price").isEqualTo("13.43");
    }

    @Test
    public void deleteItem(){

        webTestClient.delete().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"),"ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItem(){

        Item itemToBeUpdated = new Item("ABC","Philips Equipment",25.33);

        webTestClient.put().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 +"/{id}","ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(itemToBeUpdated),Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price",25.33);
    }

    @Test
    public void runTimeException(){

        webTestClient.get().uri("/fun/runtimeexception")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.message", "RuntimeException Occurred");
    }


}
