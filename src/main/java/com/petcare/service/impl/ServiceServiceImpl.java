package com.petcare.service.impl;

import com.petcare.dao.ServiceDAO;
import com.petcare.dao.impl.ServiceDAOImpl;
import com.petcare.model.Service;
import com.petcare.service.ServiceService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of ServiceService interface.
 */
public class ServiceServiceImpl implements ServiceService {
    
    private final ServiceDAO serviceDAO;
    
    public ServiceServiceImpl() {
        this.serviceDAO = new ServiceDAOImpl();
    }
    
    @Override
    public Service createService(String name, String description, BigDecimal price, int durationMinutes) {
        validateServiceData(name, description, price, durationMinutes);
        
        Service service = new Service();
        service.setName(name);
        service.setDescription(description);
        service.setPrice(price);
        service.setDurationMinutes(durationMinutes);
        
        return serviceDAO.save(service);
    }
    
    @Override
    public Optional<Service> getServiceById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid service ID");
        }
        
        return serviceDAO.findById(id);
    }
    
    @Override
    public List<Service> getAllServices() {
        return serviceDAO.findAll();
    }
    
    @Override
    public List<Service> getServicesByMaxDuration(int maxDuration) {
        if (maxDuration <= 0) {
            throw new IllegalArgumentException("Maximum duration must be greater than zero");
        }
        
        return serviceDAO.findByMaxDuration(maxDuration);
    }
    
    @Override
    public List<Service> searchServices(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            throw new IllegalArgumentException("Search text cannot be empty");
        }
        
        return serviceDAO.searchByNameOrDescription(searchText);
    }
    
    @Override
    public List<Service> getServicesSortedByPrice(boolean ascending) {
        return serviceDAO.findAllSortedByPrice(ascending);
    }
    
    @Override
    public Service updateService(Long id, String name, String description, BigDecimal price, int durationMinutes) {
        validateServiceData(name, description, price, durationMinutes);
        
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid service ID");
        }
        
        Optional<Service> serviceOpt = serviceDAO.findById(id);
        if (!serviceOpt.isPresent()) {
            return null;
        }
        
        Service service = serviceOpt.get();
        service.setName(name);
        service.setDescription(description);
        service.setPrice(price);
        service.setDurationMinutes(durationMinutes);
        
        return serviceDAO.update(service);
    }
    
    @Override
    public boolean deleteService(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid service ID");
        }
        
        return serviceDAO.deleteById(id);
    }
    
    /**
     * Validate service data for creation or update.
     */
    private void validateServiceData(String name, String description, BigDecimal price, int durationMinutes) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Service name cannot be empty");
        }
        
        if (description == null) {
            throw new IllegalArgumentException("Service description cannot be null");
        }
        
        if (price == null) {
            throw new IllegalArgumentException("Service price cannot be null");
        }
        
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Service price must be greater than zero");
        }
        
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("Service duration must be greater than zero");
        }
    }
}