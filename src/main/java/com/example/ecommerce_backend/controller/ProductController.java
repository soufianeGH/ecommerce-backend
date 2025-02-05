package com.example.ecommerce_backend.controller;

import com.example.ecommerce_backend.model.Product;
import com.example.ecommerce_backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

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
    public ResponseEntity<Map<String, Object>> createProduct(@RequestBody Product product) {
        String email = getAuthenticatedEmail();
        Product createdProduct = productService.createProduct(email, product);
        return ResponseEntity.ok(Map.of(
                "message", "Product created successfully",
                "product", createdProduct
        ));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(@PathVariable Long id,
                                                             @RequestBody Product product) {
        String email = getAuthenticatedEmail();
        Product updatedProduct = productService.updateProduct(email, id, product);
        return ResponseEntity.ok(Map.of(
                "message", "Product updated successfully",
                "product", updatedProduct
        ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteProduct(@PathVariable Long id) {
        String email = getAuthenticatedEmail();
        productService.deleteProduct(email, id);
        return ResponseEntity.ok(Map.of("message", "Product deleted successfully"));
    }

    private String getAuthenticatedEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (String) authentication.getPrincipal();
    }
}
