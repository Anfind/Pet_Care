package com.petcare.service.impl;

import com.petcare.dao.ProductDAO;
import com.petcare.dao.impl.ProductDAOImpl;
import com.petcare.model.Product;
import com.petcare.service.ProductService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of ProductService interface.
 */
public class ProductServiceImpl implements ProductService {
    
    private final ProductDAO productDAO;
    
    public ProductServiceImpl() {
        this.productDAO = new ProductDAOImpl();
    }
    
    @Override
    public Product createProduct(String name, String description, BigDecimal price, String type) {
        validateProductData(name, description, price, type);
        
        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setType(type);
        
        return productDAO.save(product);
    }
    
    @Override
    public Optional<Product> getProductById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid product ID");
        }
        
        return productDAO.findById(id);
    }
    
    @Override
    public List<Product> getAllProducts() {
        return productDAO.findAll();
    }
    
    @Override
    public List<Product> getProductsByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Product type cannot be empty");
        }
        
        return productDAO.findByType(type);
    }
    
    @Override
    public List<Product> searchProducts(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            throw new IllegalArgumentException("Search text cannot be empty");
        }
        
        return productDAO.searchByNameOrDescription(searchText);
    }
    
    @Override
    public Product updateProduct(Long id, String name, String description, BigDecimal price, String type) {
        validateProductData(name, description, price, type);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid product ID");
        }
        
        Optional<Product> productOpt = productDAO.findById(id);
        if (!productOpt.isPresent()) {
            return null;
        }
        
        Product product = productOpt.get();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setType(type);
        
        return productDAO.update(product);
    }
    
    @Override
    public boolean deleteProduct(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid product ID");
        }
        
        return productDAO.deleteById(id);
    }
    
    /**
     * Validate product data for creation or update.
     */
    private void validateProductData(String name, String description, BigDecimal price, String type) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        
        if (description == null) {
            throw new IllegalArgumentException("Product description cannot be null");
        }
        
        if (price == null) {
            throw new IllegalArgumentException("Product price cannot be null");
        }
        
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Product price must be greater than zero");
        }
        
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Product type cannot be empty");
        }
        
        if (!isValidProductType(type)) {
            throw new IllegalArgumentException("Invalid product type. Valid types are: FOOD, TOY, OTHER");
        }
    }
    
    /**
     * Check if the product type is valid.
     */
    private boolean isValidProductType(String type) {
        return "FOOD".equals(type) || "TOY".equals(type) || "OTHER".equals(type);
    }
}