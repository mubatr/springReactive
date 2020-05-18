package fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

    @Test
    public void backPressureTest() {

        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        // stepVerifier acting as subscriber
        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();

    }

    @Test
    public void backPressure() {

        // create a publisher.
        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        // subscribing to publisher
        finiteFlux.subscribe((element) -> System.out.println("Element is " + element)
                , (e) -> System.err.println("Exception is : " + e) // In case of Exception, this Exception Handler will execute.
                , () -> System.out.println("done") // if Whole flow is complete, then this will be executed.
                , (subscription -> subscription.request(2))); // Acutal Subscription
    }

    @Test
    public void backPressure_Cancel() {

        // create a publisher.
        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        // subscribing to publisher
        finiteFlux.subscribe((element) -> System.out.println("Element is " + element)
                , (e) -> System.err.println("Exception is : " + e) // In case of Exception, this Exception Handler will execute.
                , () -> System.out.println("done") // if Whole flow is complete, then this will be executed.
                , (subscription -> subscription.cancel())); // cancel the subscription
    }


    @Test
    public void customized_backPressure() {

        // create a publisher.
        Flux<Integer> finiteFlux = Flux.range(1, 10)
                .log();

        finiteFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                super.hookOnNext(value);
                request(1);
                System.out.println("value received is " + value);
                if (value == 4)
                    cancel();
            }
        });

    }
}