package com.example.crud_async;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;


@Configuration
public class ProductRouter {

    @Bean
    public RouterFunction<ServerResponse> productRouterFunction(ProductHandler handler) {
        return RouterFunctions.route(POST("/api/products"),
                                        // .and(accept(org.springframework.http.MediaType.APPLICATION_JSON)), // use it when you expect a specific content types with/without json to be returned as a response
                                    handler::create)
                .andRoute(GET("/api/products/{id}"), handler::getById)
                .andRoute(GET("/api/products"), handler::getAll)
                .andRoute(PUT("/api/products/{id}"), handler::update)
                .andRoute(DELETE("/api/products/{id}"), handler::delete);
    }
}
