package com.example.dynamic_validate.dao;

import com.example.dynamic_validate.entity.DemandPathMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemandPathMapDao extends JpaRepository<DemandPathMap, Integer> {


}
