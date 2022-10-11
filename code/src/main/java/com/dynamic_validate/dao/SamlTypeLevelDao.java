package com.dynamic_validate.dao;

import com.dynamic_validate.entity.SamlTypeLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SamlTypeLevelDao extends JpaRepository<SamlTypeLevel, Integer> {

}
