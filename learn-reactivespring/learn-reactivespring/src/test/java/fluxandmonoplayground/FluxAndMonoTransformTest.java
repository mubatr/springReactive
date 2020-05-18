package fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam","anna","jack","jenny");

    @Test
    public void transformUsingMap()
    {
        Flux<String> nameFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(nameFlux).expectNext("ADAM","ANNA","JACK","JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length()
    {
        Flux<Integer> nameFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .log();

        StepVerifier.create(nameFlux).expectNext(4,4,4,5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length_repeat()
    {
        //Flux is a Publisher and data Emitter
        Flux<Integer> nameFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .repeat(1)
                .log();

        StepVerifier.create(nameFlux)
                .expectNext(4,4,4,5,4,4,4,5)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap()
    {
        //Flux is a Publisher and data Emitter
        Flux<String> names = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .flatMap(s ->{

                    return Flux.fromIterable(convertToList(s));
                });// Flat Map is used when DB call/External service call is made that Returns a flux


        StepVerifier.create(names.log())
                .expectNextCount(12)
                .verifyComplete();
    }

     public List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        }catch(InterruptedException e)
        {
            e.printStackTrace();
        }
        return Arrays.asList(s,"newValue");

    }

    @Test
    public void transformUsingFlatMap_usingParallel()
    {
        //Flux is a Publisher and data Emitter
        Flux<String> names = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .window(2) //Flux<Flux<String>>
                .flatMap((s) ->
                    s.map(this::convertToList).subscribeOn(parallel())
                            .flatMap(p-> Flux.fromIterable(p)
                            .log()));
                    //return Flux.fromIterable(convertToList(s));
                //});// Flat Map is used when DB call/External service call is made that Returns a flux


        StepVerifier.create(names)
                .expectNextCount(12)
                .verifyComplete();
    }



    @Test
    public void transformUsingFlatMap_usingParallel_maintain_order()
    {
        //Flux is a Publisher and data Emitter
        Flux<String> names = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .window(2) //Flux<Flux<String>>
               // .concatMap((s) ->
                    .flatMapSequential((s) ->
                            s.map(this::convertToList).subscribeOn(parallel())
                                .flatMap(p-> Flux.fromIterable(p)
                                        .log()));

        //return Flux.fromIterable(convertToList(s));
        //});// Flat Map is used when DB call/External service call is made that Returns a flux


        StepVerifier.create(names)
                .expectNextCount(12)
                .verifyComplete();
    }


}
