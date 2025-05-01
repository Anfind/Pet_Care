package com.petcare.service;

import com.petcare.model.Service;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for Service business operations.
 */
public interface ServiceService {
    
    /**
     * Create a new service.
     * 
     * @param name Service name
     * @param description Service description
     * @param price Service price
     * @param durationMinutes Service duration in minutes
     * @return The created service
     */
    Service createService(String name, String description, BigDecimal price, int durationMinutes);
    
    /**
     * Get a service by ID.
     * 
     * @param id Service ID
     * @return Optional containing the service if found
     */
    Optional<Service> getServiceById(Long id);
    
    /**
     * Get all services.
     * 
     * @return List of all services
     */
    List<Service> getAllServices();
    
    /**
     * Get services by maximum duration.
     * 
     * @param maxDuration Maximum duration in minutes
     * @return List of services with duration less than or equal to maxDuration
     */
    List<Service> getServicesByMaxDuration(int maxDuration);
    
    /**
     * Search services by name or description.
     * 
     * @param searchText Text to search for
     * @return List of services matching the search criteria
     */
    List<Service> searchServices(String searchText);
    
    /**
     * Get services sorted by price.
     * 
     * @param ascending true for ascending order, false for descending
     * @return List of services sorted by price
     */
    List<Service> getServicesSortedByPrice(boolean ascending);
    
    /**
     * Update a service.
     * 
     * @param id Service ID
     * @param name New service name
     * @param description New service description
     * @param price New service price
     * @param durationMinutes New service duration in minutes
     * @return Updated service, or null if service not found
     */
    Service updateService(Long id, String name, String description, BigDecimal price, int durationMinutes);
    
    /**
     * Delete a service.
     * 
     * @param id Service ID
     * @return true if deleted, false if not found
     */
    boolean deleteService(Long id);
}