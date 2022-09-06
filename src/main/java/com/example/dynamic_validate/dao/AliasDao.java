package com.example.dynamic_validate.dao;

import com.example.dynamic_validate.entity.Alias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AliasDao extends JpaRepository<Alias, Integer> {


    List<Alias> findAliasByAlias(String alias);
    
}
