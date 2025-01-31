package com.example.ecommerce_backend.controller;

import com.example.ecommerce_backend.model.Product;
import com.example.ecommerce_backend.repository.ProductRepository;
import com.example.ecommerce_backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    // For demonstration only: storing wishlist in memory
    // Key: userEmail, Value: set of product IDs
    private final Map<String, Set<Long>> wishlistStore = new HashMap<>();

    private final ProductRepository productRepository;
    private final JwtUtil jwtUtil;

    @GetMapping
    public List<Product> getWishlist(@RequestHeader("Authorization") String authHeader) {
        String email = jwtUtil.extractEmailFromHeader(authHeader);
        Set<Long> productIds = wishlistStore.getOrDefault(email, new HashSet<>());
        return productRepository.findAllById(productIds);
    }

    @PostMapping("/{productId}")
    public List<Product> addToWishlist(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long productId) {

        String email = jwtUtil.extractEmailFromHeader(authHeader);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        wishlistStore.putIfAbsent(email, new HashSet<>());
        wishlistStore.get(email).add(product.getId());
        return getWishlist(authHeader);
    }

    @DeleteMapping("/{productId}")
    public List<Product> removeFromWishlist(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long productId) {

        String email = jwtUtil.extractEmailFromHeader(authHeader);
        wishlistStore.putIfAbsent(email, new HashSet<>());
        wishlistStore.get(email).remove(productId);
        return getWishlist(authHeader);
    }
}

