package com.example.ecommerce_backend.controller;

import com.example.ecommerce_backend.model.Product;
import com.example.ecommerce_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    // In-memory wishlist storage (for demonstration):
    // Key: user email, Value: set of product IDs.
    private final Map<String, Set<Long>> wishlistStore = new HashMap<>();

    private final ProductRepository productRepository;

    @GetMapping
    public List<Product> getWishlist() {
        String email = getAuthenticatedEmail();
        Set<Long> productIds = wishlistStore.getOrDefault(email, new HashSet<>());
        return productRepository.findAllById(productIds);
    }

    @PostMapping("/{productId}")
    public List<Product> addToWishlist(@PathVariable Long productId) {
        String email = getAuthenticatedEmail();
        // Validate that the product exists.
        productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        wishlistStore.putIfAbsent(email, new HashSet<>());
        wishlistStore.get(email).add(productId);
        return getWishlist();
    }

    @DeleteMapping("/{productId}")
    public List<Product> removeFromWishlist(@PathVariable Long productId) {
        String email = getAuthenticatedEmail();
        wishlistStore.putIfAbsent(email, new HashSet<>());
        wishlistStore.get(email).remove(productId);
        return getWishlist();
    }

    private String getAuthenticatedEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (String) authentication.getPrincipal();
    }
}
