package com.example.dynamic_validate.dao;

import com.example.dynamic_validate.entity.SamlRelationClassify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SamlRelationClassifyDao extends JpaRepository<SamlRelationClassify, Integer> {

}
