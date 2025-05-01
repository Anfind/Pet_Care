package com.petcare.dao;

import java.util.List;
import java.util.Optional;

/**
 * Base DAO interface that defines common CRUD operations.
 * @param <T> The entity type
 * @param <K> The primary key type
 */
public interface BaseDAO<T, K> {
    
    /**
     * Save an entity to the database. If entity has an ID, update existing entity, otherwise create new.
     * @param entity The entity to save
     * @return The saved entity with generated ID if created
     */
    T save(T entity);
    
    /**
     * Find an entity by its primary key.
     * @param id The primary key
     * @return An Optional containing the entity if found, or empty if not found
     */
    Optional<T> findById(K id);
    
    /**
     * Find all entities.
     * @return A list of all entities
     */
    List<T> findAll();
    
    /**
     * Delete an entity by its primary key.
     * @param id The primary key
     * @return true if deleted, false if not found
     */
    boolean deleteById(K id);
    
    /**
     * Update an existing entity.
     * @param entity The entity to update
     * @return The updated entity
     */
    T update(T entity);
}