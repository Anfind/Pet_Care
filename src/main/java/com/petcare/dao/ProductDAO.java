package com.petcare.dao;

import com.petcare.model.Product;
import java.util.List;

/**
 * DAO interface for Product entity operations.
 */
public interface ProductDAO extends BaseDAO<Product, Long> {
    
    /**
     * Find products by their type.
     * 
     * @param type The product type (e.g., "FOOD", "TOY", "OTHER")
     * @return A list of products with the given type
     */
    List<Product> findByType(String type);
    
    /**
     * Search for products containing the given text in name or description.
     * 
     * @param searchText The text to search for
     * @return A list of products matching the search criteria
     */
    List<Product> searchByNameOrDescription(String searchText);
}