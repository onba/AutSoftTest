package com.example.autsofttest.Persistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistryRepository extends CrudRepository<Registry,Integer> {
        public List<Registry> findAllByCategoriesContaining(Category category);
}
