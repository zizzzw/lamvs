package com.example.dynamic_validate.dao;

import com.example.dynamic_validate.entity.BaseType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BaseTypeDao extends JpaRepository<BaseType, Integer> {

    BaseType findBaseTypeByName(String name);
    
}
