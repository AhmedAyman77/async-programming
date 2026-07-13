package com.example.async;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
public class GreetingController {

    private static final Logger log = LoggerFactory.getLogger(GreetingController.class.getName());

    @GetMapping("/mono")
    public Mono<Greeting> sendMonoGreeting() {
        log.info("Handling /mono on thread: {}", Thread.currentThread().getName());

        return Mono.just(new Greeting("Hello from Mono!", System.currentTimeMillis()));
    }
    

    @GetMapping(value="/flux", produces="text/event-stream")
    public Flux<Greeting> sendFluxGreeting() {
        log.info("Handling /flux on thread: {}", Thread.currentThread().getName());

        return Flux.interval(Duration.ofSeconds(1))
                .map(i -> new Greeting("Hello from Flux! " + i, System.currentTimeMillis()))
                .take(10)
                .doOnNext(g -> log.info("Emitting: {} on thread: {}", g, Thread.currentThread().getName()));
    }
    

    @GetMapping("/flux-json")
    public Flux<Greeting> sendFluxJsonGreeting() {
        log.info("Handling /flux-json on thread: {}", Thread.currentThread().getName());

        return Flux.just(
                new Greeting("Item 1", System.currentTimeMillis()),
                new Greeting("Item 2", System.currentTimeMillis()),
                new Greeting("Item 3", System.currentTimeMillis())
        );
    }
}
