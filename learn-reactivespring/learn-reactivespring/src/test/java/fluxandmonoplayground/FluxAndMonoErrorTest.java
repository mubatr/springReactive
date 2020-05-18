package fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    /**
     * SHows One of the Approach for Exception Handling in Reactive Streams.
     * using onErrorResume which returns a Flux
     */
    @Test
    public void fluxErrorHandling()
    {
        Flux<String> stringFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorResume((e) -> { // whenever there is an Error, onErrorResume block will be Executed
                   System.out.println("Exception is " + e);
                   return Flux.just("default","default1");
                });

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")// asserting that the values sent in Flux matches with the result.
                //.expectError(RuntimeException.class)
                //.verify();
                .expectNext("default","default1")
                .verifyComplete();
    }

    /**
     * Error
     */
    @Test
    public void fluxErrorHandling_OnErrorReturn()
    {
        Flux<String> stringFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("default");

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")// asserting that the values sent in Flux matches with the result.
                //.expectError(RuntimeException.class)
                //.verify();
                .expectNext("default")
                .verifyComplete();
    }

    /**
     * Assigning an Exception from one Type to another type (CustomException).
     */
    @Test
    public void fluxErrorHandling_OnErrorMap()
    {
        Flux<String> stringFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap((e) -> new CustomException(e));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")// asserting that the values sent in Flux matches with the result.
                .expectError(CustomException.class)
                .verify();
    }

    /**
     * Scenario - wheever there is an exception, I want to Retry the DB call before Ending the Flux.
     */
    @Test
    public void fluxErrorHandling_OnErrorMap_WithRetry()
    {
        Flux<String> stringFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap((e) -> new CustomException(e))
                .retry(2);

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")// asserting that the values sent in Flux matches with the result.
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                .expectError(CustomException.class)
                .verify();
    }


    /**
     * Perform a 5 sec backoff before  reTry
     * you want to perform backoff before you do a Retry.
     */
  /*  @Test
    public void fluxErrorHandling_OnErrorMap_WithRetryBackOff()
    {
        Flux<String> stringFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap((e) -> new CustomException(e));
               // .retryBackoff(2, Duration.ofSeconds(5));

        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B","C")// asserting that the values sent in Flux matches with the result.
                .expectNext("A","B","C")
                .expectNext("A","B","C")
                .expectError(CustomException.class)
                .verify();
    }*/

}
