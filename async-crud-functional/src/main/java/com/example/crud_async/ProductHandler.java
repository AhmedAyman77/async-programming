package com.example.crud_async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;


@Component
@RequiredArgsConstructor
public class ProductHandler {

    private static final Logger log = LoggerFactory.getLogger(ProductHandler.class);

    private final ProductRepository productRepository;

    public Mono<ServerResponse> create(ServerRequest request) {
        log.info("Creating product on thread: {}", Thread.currentThread().getName());

        return request.bodyToMono(Product.class)
                        .flatMap(productRepository::save)
                        .flatMap(saved -> ServerResponse
                                            .created(request.uri())
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .bodyValue(saved)
                                );
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        log.info("Fetching product {} on thread: {}", id, Thread.currentThread().getName());

        return productRepository.findById(id)
                                .flatMap(product -> ServerResponse.ok()
                                                                    .contentType(MediaType.APPLICATION_JSON)
                                                                    .bodyValue(product))
                                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getAll(ServerRequest request) {
        log.info("Fetching all products on thread: {}", Thread.currentThread().getName());
        return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(productRepository.findAll(), Product.class);
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        log.info("Updating product {} on thread: {}", id, Thread.currentThread().getName());

        return request.bodyToMono(Product.class)
                        .flatMap(updatedProduct -> productRepository.findById(id)
                                                                    .flatMap(product -> {
                                                                        product.setName(updatedProduct.getName());
                                                                        product.setPrice(updatedProduct.getPrice());
                                                                        product.setQuantity(updatedProduct.getQuantity());
                                                                        return productRepository.save(product);
                                                                    })
                                )
                        .flatMap(saved -> ServerResponse.ok()
                                                        .contentType(MediaType.APPLICATION_JSON)
                                                        .bodyValue(saved)
                                )
                        .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        Long id = Long.valueOf(request.pathVariable("id"));
        log.info("Deleting product {} on thread: {}", id, Thread.currentThread().getName());
        
        return productRepository.findById(id)
                .flatMap(existing -> productRepository.delete(existing).then(ServerResponse.noContent().build()))
                .switchIfEmpty(ServerResponse.notFound().build());
    }
}