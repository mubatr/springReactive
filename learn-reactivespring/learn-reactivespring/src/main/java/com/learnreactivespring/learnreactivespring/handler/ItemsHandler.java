package com.learnreactivespring.learnreactivespring.handler;

import com.learnreactivespring.learnreactivespring.document.Item;
import com.learnreactivespring.learnreactivespring.document.ItemCapped;
import com.learnreactivespring.learnreactivespring.repository.ItemReactiveCappedRepository;
import com.learnreactivespring.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

/**
 * Handler Function which will process request and return responses
 */
@Component
public class ItemsHandler {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    //Incoming requests handled by Handler Function
    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getOneItem(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");
        Mono<Item> itemMono = itemReactiveRepository.findById(id);
        return itemMono.flatMap(item ->
                ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(item)))
                    .switchIfEmpty(notFound);
    }

    /**
     * REST API to Create an Item
     * @param serverRequest
     * @return
     */
    public Mono<ServerResponse> createItem(ServerRequest serverRequest){
        Mono<Item> itemToBeInserted = serverRequest.bodyToMono(Item.class);
        return itemToBeInserted.flatMap(item -> ServerResponse.ok().
                    contentType(MediaType.APPLICATION_JSON)
                    .body(itemReactiveRepository.save(item),Item.class));
    }

    /**
     * REST Endpoint to Delete an Item.
     * @param serverRequest
     * @return
     */
    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");
        Mono<Void> deletedItem = itemReactiveRepository.deleteById(id);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(deletedItem,Void.class);
    }

    /**
     * REST API to update Item.
     * PUT HTTP Verb is used for updation.
     * If item exists, then it overirdes it with a new version.
     * If item does not exist, then it creates it.
     * @param serverRequest
     * @return
     */
    public Mono<ServerResponse> updateItem(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");

        Mono<Item> updatedItem = serverRequest.bodyToMono(Item.class)
                .flatMap(item -> {
                    Mono<Item> itemMono = itemReactiveRepository.findById(id)
                            .flatMap(currentItem -> {
                                currentItem.setDescription(item.getDescription());
                                currentItem.setPrice(item.getPrice());
                                return itemReactiveRepository.save(currentItem);
                            });
                    return itemMono;
                });
        return updatedItem.flatMap(item -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(item)))
                .switchIfEmpty(notFound);
    }

    /**
     * Default Exception handling is done by abstractErrorwebExceptionHandler.
     * @param serverRequest
     * @return
     */
    public Mono<ServerResponse> itemsEx(ServerRequest serverRequest){
        throw new RuntimeException("RuntimeException Occurred");
    }

    // Streaming Endpoint implementation
    public Mono<ServerResponse> itemsStream(ServerRequest serverRequest){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(itemReactiveCappedRepository.findItemsBy(), ItemCapped.class);
    }
}


