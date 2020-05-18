package com.learnreactivespring.learnreactivespring.repository;

import com.learnreactivespring.learnreactivespring.document.Item;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest // Loads all the necessary classes required for this Mongo test case to Run.
@RunWith(SpringRunner.class) // Junit4 annotation
@DirtiesContext // Each and every test will get a New Application Context. when ever state of app context is changed, this needs to be used.
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemsList = Arrays.asList(new Item(null,"Samsung TV", 400.0),
            new Item(null,"LG TV", 420.0),
            new Item(null,"Apple Watch", 299.99),
            new Item(null,"Beats HeadPhones", 149.99),
            new Item("ABC","Bose HeadPhones", 149.99));

    @Before
    public void setup(){
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemsList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext((item -> {
                    System.out.println("Inserted Item is " + item);
                }))
                .blockLast(); // This  method call will wait until all items are saved so that test case below will have necessary data.
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(5)
                //.expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItemById(){
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches((item -> item.getDescription().equals("Bose HeadPhones")))
                .verifyComplete();
    }

    @Test
    public void findItemByDescription(){

        StepVerifier.create(itemReactiveRepository.findByDescription("Bose HeadPhones"))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();

    }

    @Test
    public void saveItem(){
        Item item = new Item(null,"Google Home Mini",30.00);
        Mono<Item> savedItem = itemReactiveRepository.save(item);

        StepVerifier.create(savedItem.log("Saved Item : "))
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getId() != null && item1.getDescription().equals("Google Home Mini"))
                .verifyComplete();
    }

    @Test
    public void updateItem(){
        double newPrice = 520.00;
        Mono<Item> updatedItem = itemReactiveRepository.findByDescription("LG TV")
                .map(item ->{
                    item.setPrice(newPrice);
                    return item;
                })
                .flatMap(item -> {
                    return itemReactiveRepository.save(item);
                });

        //testing whether update happened or not
        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 520)
                .verifyComplete();
    }

    @Test
    public void deleteItemById(){
        Mono<Void> deletedItem = itemReactiveRepository.findById("ABC") // Returns Mono<Item>
        .map(Item::getId)
                .flatMap((id) -> {
                    return itemReactiveRepository.deleteById(id);
                });

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("The new Item List :  "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }


    @Test
    public void deleteItem(){
        Mono<Void> deletedItem = itemReactiveRepository.findByDescription("LG TV") // Returns Mono<Item>
                .flatMap((item) -> {
                    return itemReactiveRepository.delete(item);
                });

        StepVerifier.create(deletedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("The new Item List :  "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }
}
