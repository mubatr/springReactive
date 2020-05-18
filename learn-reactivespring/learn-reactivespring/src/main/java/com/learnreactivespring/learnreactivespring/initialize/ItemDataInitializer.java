package com.learnreactivespring.learnreactivespring.initialize;

import com.learnreactivespring.learnreactivespring.document.Item;
import com.learnreactivespring.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.learnreactivespring.repository.ItemReactiveCappedRepository;
import com.learnreactivespring.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

// This class will help in data setup during app startup.
@Component
@Profile("!test")
@Slf4j
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    MongoOperations mongoOperations;

    @Override
    public void run(String... args) throws Exception {
        InitialDataSetup();
        createCappedCollection();
        dataSetupforCappedCollection();
    }

    /**
     * cappedCollection is a terminology which represents a fixed size Collection in MongoDB.
     * cappedCollection preserves Insertion Order.
     * You can Never use capped Collection for permananet storage.
     * On App startup, capped collection will be created in MongoDB.
     */
    private void createCappedCollection() {

        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());
    }

    public List<Item> data() {

        return Arrays.asList(new Item(null,"Samsung TV", 399.0),
                new Item(null,"LG TV", 329.99),
                new Item(null,"Apple Watch", 349.99),
                new Item("ABC","Beats HeadPhones", 19.99));
    }

// During the app startup, this will setup data for capped Collection. Every 1 second, data will be inserted
    // in MongoDB in capped collection.
    public void dataSetupforCappedCollection() {
        // Every 1 second, random number will be emitted which will be used to populate ItemCapped
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "Random Item " + i, (100.00+i)));

        // insert will subscribe to Flux
        itemReactiveCappedRepository
                .insert(itemCappedFlux)
                .subscribe((itemCapped -> {
                    log.info("Inserted item is " + itemCapped);
            }));
    }


    private void InitialDataSetup(){

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                        .flatMap(itemReactiveRepository::save)
                        .thenMany(itemReactiveRepository.findAll())
                        .subscribe(item -> {
                            System.out.println("Item Inserted from command Line Runner");
                        });
    }
}
