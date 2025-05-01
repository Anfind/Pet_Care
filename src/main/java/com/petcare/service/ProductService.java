package com.petcare.service;

import com.petcare.model.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Product business operations.
 */
public interface ProductService {
    
    /**
     * Create a new product.
     * 
     * @param name Product name
     * @param description Product description
     * @param price Product price
     * @param type Product type
     * @return The created product
     */
    Product createProduct(String name, String description, BigDecimal price, String type);
    
    /**
     * Get a product by ID.
     * 
     * @param id Product ID
     * @return Optional containing the product if found
     */
    Optional<Product> getProductById(Long id);
    
    /**
     * Get all products.
     * 
     * @return List of all products
     */
    List<Product> getAllProducts();
    
    /**
     * Get products by type.
     * 
     * @param type Product type
     * @return List of products with the specified type
     */
    List<Product> getProductsByType(String type);
    
    /**
     * Search products by name or description.
     * 
     * @param searchText Text to search for
     * @return List of products matching the search criteria
     */
    List<Product> searchProducts(String searchText);
    
    /**
     * Update a product.
     * 
     * @param id Product ID
     * @param name New product name
     * @param description New product description
     * @param price New product price
     * @param type New product type
     * @return Updated product, or null if product not found
     */
    Product updateProduct(Long id, String name, String description, BigDecimal price, String type);
    
    /**
     * Delete a product.
     * 
     * @param id Product ID
     * @return true if deleted, false if not found
     */
    boolean deleteProduct(Long id);
}