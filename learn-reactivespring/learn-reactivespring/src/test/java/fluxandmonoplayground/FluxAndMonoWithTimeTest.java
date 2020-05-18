package fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException{

        // creating a Flux that emits long values starting with 0 and incrementing it every 200 ms.
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200))
                .log();
        infiniteFlux.subscribe((element) -> System.out.println("value is " + element)); // subscribing to the Flux and request unbounded demand
        Thread.sleep((3000));
    }

    @Test
    public void infiniteSequenceTest() throws InterruptedException{

        // creating a Flux that emits long values starting with 0 and incrementing it every 200 ms.
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .take(3) // flux will generate elemnts 0,1,2
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L,1L,2L)
                .verifyComplete(); // its kind of subscribe call . It will wait until all events are published.
    }

    @Test
    public void infiniteSequenceMap() throws InterruptedException{

        // creating a Flux that emits long values starting with 0 and incrementing it every 200 ms.
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(200))
           .delayElements(Duration.ofSeconds(1))
           .map(l -> new Integer(l.intValue()))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0,1,2)
                .verifyComplete(); // its kind of subscribe call . It will wait until all events are published.
    }
}
