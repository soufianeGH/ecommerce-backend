package com.example.ecommerce_backend.service;

import com.example.ecommerce_backend.exception.UnauthorizedAccessException;
import com.example.ecommerce_backend.model.Product;
import com.example.ecommerce_backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product createProduct(String email, Product product) {
        enforceAdminAccess(email);
        return productRepository.save(product);
    }

    public Product updateProduct(String email, Long id, Product partialUpdate) {
        enforceAdminAccess(email);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (partialUpdate.getName() != null) product.setName(partialUpdate.getName());
        if (partialUpdate.getDescription() != null) product.setDescription(partialUpdate.getDescription());
        if (partialUpdate.getPrice() != null) product.setPrice(partialUpdate.getPrice());
        if (partialUpdate.getQuantity() != null) product.setQuantity(partialUpdate.getQuantity());
        if (partialUpdate.getInventoryStatus() != null) product.setInventoryStatus(partialUpdate.getInventoryStatus());

        return productRepository.save(product);
    }

    public void deleteProduct(String email, Long id) {
        enforceAdminAccess(email);
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(id);
    }

    private void enforceAdminAccess(String email) {
        // For demonstration, only a specific email is allowed admin actions.
        if (!"admin@admin.com".equalsIgnoreCase(email)) {
            throw new UnauthorizedAccessException("Only admin can modify products");
        }
    }
}
