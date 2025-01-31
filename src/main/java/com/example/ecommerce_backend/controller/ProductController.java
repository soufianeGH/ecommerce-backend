package com.example.ecommerce_backend.controller;

import com.example.ecommerce_backend.model.Product;
import com.example.ecommerce_backend.service.ProductService;
import com.example.ecommerce_backend.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) {
        Optional<Product> productOpt = productService.getProductById(id);
        return productOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createProduct(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody Product product) {

        String email = jwtService.extractEmailFromHeader(authHeader);
        Product createdProduct = productService.createProduct(email, product);

        return ResponseEntity.ok(Map.of(
                "message", "Product created successfully",
                "product", createdProduct
        ));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id,
            @RequestBody Product product) {

        String email = jwtService.extractEmailFromHeader(authHeader);
        Product updatedProduct = productService.updateProduct(email, id, product);

        return ResponseEntity.ok(Map.of(
                "message", "Product updated successfully",
                "product", updatedProduct
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long id) {

        String email = jwtService.extractEmailFromHeader(authHeader);
        productService.deleteProduct(email, id);

        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }
}
