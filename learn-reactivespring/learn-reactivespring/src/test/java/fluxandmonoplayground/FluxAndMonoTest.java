package fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    @Test
    public void fluxtest()
    {
        // Flux is implementation of Publisher Interface that can Emit 0 to N Elements.
        Flux<String> stringFlux = Flux.just("Spring","Spring Boot","Reactive Spring")
               // .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .concatWith(Flux.just("After Error"))
                .log();

        stringFlux.
                subscribe(System.out::println,
                        (e) -> System.err.println("Exception is " + e)
                ,() -> System.out.println("Completed"));
    }

    @Test
    public void fluxTestElements_WithoutError()
    {
        Flux<String> stringFlux = Flux.just("Spring","Spring Boot","Reactive Spring")
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                .verifyComplete();  // always Make sure to end test case with Verify call.
    }

    @Test
    public void fluxTestElements_WithError()
    {
        Flux<String> stringFlux = Flux.just("Spring","Spring Boot","Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                //.expectError(RuntimeException.class)
             .expectErrorMessage("Exception occurred")
                .verify();
    }

    @Test
    public void fluxTestElementsCount_WithError()
    {
        Flux<String> stringFlux = Flux.just("Spring","Spring Boot","Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectErrorMessage("Exception occurred")
                .verify();
    }

    @Test
    public void fluxTestElements_WithError1()
    {
        Flux<String> stringFlux = Flux.just("Spring","Spring Boot","Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception occurred")))
                .log();

        StepVerifier.create(stringFlux)
                .expectNext("Spring","Spring Boot","Reactive Spring")
                .expectErrorMessage("Exception occurred")
                .verify();
    }

    @Test
    public void monoTest()
    {
        // Mono is an implementation of Publisher that can Emit at most 1 Element.
        Mono<String> stringMono = Mono.just("Spring");

        StepVerifier.create(stringMono.log())
                .expectNext("Spring")
                .verifyComplete();
    }

    @Test
    public void monoTest_Error()
    {
        //Mono<String> stringMono = Mono.just("Spring");

        StepVerifier.create(Mono.error(new RuntimeException("Exception occurred")).log())
                .expectError(RuntimeException.class)
                .verify();
    }

}
