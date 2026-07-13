package com.example.crud_async;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("product")
public class Product {
    @Id
    private Long id;

    private String name;

    private BigDecimal price;

    private Integer quantity;
}
