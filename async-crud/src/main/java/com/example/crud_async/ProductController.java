package com.example.crud_async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    private final ProductRepository productRepository;

    @PostMapping
    public Mono<ResponseEntity<Product>> create(@RequestBody Product product) {
        log.info("Creating product on thread: {}", Thread.currentThread().getName());
        return productRepository.save(product)
                .map(saved -> ResponseEntity.status(HttpStatus.CREATED).body(saved));
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Product>> getById(@PathVariable Long id) {
        log.info("Fetching product {} on thread: {}", id, Thread.currentThread().getName());
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping
    public Flux<Product> getAll() { // did not use ResponseEntity here because it always returns 200 OK with an empty list if there are no products
        log.info("Fetching all products on thread: {}", Thread.currentThread().getName());
        return productRepository.findAll();
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Product>> update(@PathVariable Long id, @RequestBody Product updated) {
        log.info("Updating product {} on thread: {}", id, Thread.currentThread().getName());
        return productRepository.findById(id)
                .flatMap(existing -> {
                    existing.setName(updated.getName());
                    existing.setPrice(updated.getPrice());
                    existing.setQuantity(updated.getQuantity());
                    return productRepository.save(existing);
                })
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> delete(@PathVariable Long id) {
        log.info("Deleting product {} on thread: {}", id, Thread.currentThread().getName());
        return productRepository.findById(id)
                .flatMap(existing -> productRepository.delete(existing)
                        .then(Mono.just(ResponseEntity.noContent().<Void>build())))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}