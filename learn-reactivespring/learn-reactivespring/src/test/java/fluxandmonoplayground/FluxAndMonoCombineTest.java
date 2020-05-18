package fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class FluxAndMonoCombineTest {

    @Test
    public void combineUsingMerge() {
        Flux<String> flux1 = Flux.just("A","B","C");
        Flux<String> flux2 = Flux.just("D","E","F");

        Flux<String> mergeFlux = Flux.merge(flux1,flux2);

        StepVerifier.create(mergeFlux.log())
                .expectSubscription()
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();

    }


    @Test
    public void combineUsingMerge_WithDelay() {
        Flux<String> flux1 = Flux.just("A","B","C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D","E","F").delayElements(Duration.ofSeconds((1)));

        // Merge never gives the elements in the order that we expect them.
        Flux<String> mergeFlux = Flux.merge(flux1,flux2);

        StepVerifier.create(mergeFlux.log())
                .expectSubscription()
                .expectNextCount(6)
                //.expectNext("A","B","C","D","E","F")
                .verifyComplete();

    }

    @Test
    public void combineUsingConcat() {
        Flux<String> flux1 = Flux.just("A","B","C");
        Flux<String> flux2 = Flux.just("D","E","F");

        Flux<String> mergeFlux = Flux.concat(flux1,flux2);

        StepVerifier.create(mergeFlux.log())
                .expectSubscription()
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat_WithDelay() {

        VirtualTimeScheduler.getOrSet(); // Enabling Virtual Time for this test case.
        Flux<String> flux1 = Flux.just("A","B","C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D","E","F").delayElements(Duration.ofSeconds((1)));

        // WIth Concat, flux2 will not Emit elements until flux1 is complete.
        Flux<String> mergeFlux = Flux.concat(flux1,flux2);

        // virtualize time in Junit
        StepVerifier.withVirtualTime(()->mergeFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(6))// 6 seconds
                .expectNextCount(6)
                .verifyComplete();

        // Scenario without Virtual Time
        /*StepVerifier.create(mergeFlux.log())
                .expectSubscription()
                //.expectNextCount(6)
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();*/

    }


    /**
     * If you have a use case where you want to combine 1 element of Flux1 with 1 element of Flux2 .then you should use
     * zip methods
     */
    @Test
    public void combineUsingZip() {

        Flux<String> flux1 = Flux.just("A","B","C");
        Flux<String> flux2 = Flux.just("D","E","F");

        Flux<String> mergeFlux = Flux.zip(flux1,flux2, (t1,t2) -> {
            return t1.concat(t2);
        });

        StepVerifier.create(mergeFlux.log())
                .expectSubscription()
                .expectNext("AD","BE","CF")
                .verifyComplete();
    }

}
