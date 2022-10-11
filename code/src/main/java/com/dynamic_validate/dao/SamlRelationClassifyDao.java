package com.dynamic_validate.dao;

import com.dynamic_validate.entity.SamlRelationClassify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SamlRelationClassifyDao extends JpaRepository<SamlRelationClassify, Integer> {

}
