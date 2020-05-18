package com.learnreactivespring.learnreactivespring.repository;

import com.learnreactivespring.learnreactivespring.document.Item;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
public interface ItemReactiveRepository extends ReactiveMongoRepository<Item,String> {

    // read item from document based on Description. New method added because there is no existing impl in Parent INterface.
    Mono<Item> findByDescription(String description);
}
