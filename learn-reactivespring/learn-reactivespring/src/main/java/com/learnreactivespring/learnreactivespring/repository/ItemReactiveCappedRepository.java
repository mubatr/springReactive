package com.learnreactivespring.learnreactivespring.repository;

import com.learnreactivespring.learnreactivespring.document.Item;
import com.learnreactivespring.learnreactivespring.document.ItemCapped;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ItemReactiveCappedRepository extends ReactiveMongoRepository<ItemCapped,String> {

    @Tailable // This will open tailable cursor open and then its going to keep sending items
    Flux<ItemCapped> findItemsBy();
}
