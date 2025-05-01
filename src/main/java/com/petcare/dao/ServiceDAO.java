package com.petcare.dao;

import com.petcare.model.Service;
import java.util.List;

/**
 * DAO interface for Service entity operations.
 */
public interface ServiceDAO extends BaseDAO<Service, Long> {
    
    /**
     * Find services with duration less than or equal to the specified duration.
     * 
     * @param maxDuration The maximum duration in minutes
     * @return A list of services with duration less than or equal to maxDuration
     */
    List<Service> findByMaxDuration(int maxDuration);
    
    /**
     * Search for services containing the given text in name or description.
     * 
     * @param searchText The text to search for
     * @return A list of services matching the search criteria
     */
    List<Service> searchByNameOrDescription(String searchText);
    
    /**
     * Find services sorted by price (ascending or descending).
     * 
     * @param ascending true for ascending order, false for descending
     * @return A list of services sorted by price
     */
    List<Service> findAllSortedByPrice(boolean ascending);
}