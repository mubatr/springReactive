package fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

import java.time.Duration;

public class ColdandHotPublisherTest {

    /**
     *Cold Publisher - Flux is going to Emit Values from Beginning, every time a new subscriber is added to Flux.
     * @throws InterruptedException
     */
    @Test
    public void coldPublisherTest() throws InterruptedException {

        Flux<String> stringFlux = Flux.just("A","B","C","D","E","F")
                .delayElements(Duration.ofSeconds(1));

        stringFlux.subscribe(s -> System.out.println("Subscriber 1: " + s));
        Thread.sleep(2000);

        stringFlux.subscribe(s -> System.out.println("Subscriber 2 : " + s));
        Thread.sleep(4000);
    }

    /**
     * HotPublisher is not going to Emit values from Begininning for any new Subscriber that gets added to the Flux.
     * @throws InterruptedException
     */
    @Test
    public void hotPublisherTest() throws InterruptedException {

        Flux<String> stringFlux = Flux.just("A","B","C","D","E","F")
                .delayElements(Duration.ofSeconds(1));

        ConnectableFlux<String> connectableFlux = stringFlux.publish();
        connectableFlux.connect();
        connectableFlux.subscribe(s -> System.out.println("Subscriber 1 " + s));
        Thread.sleep(3000); // 3 seconds

        connectableFlux.subscribe(s -> System.out.println("Subscriber 2 " + s)); // does not Emit values from Begining.
        Thread.sleep(4000); // 4 seconds

    }
}
