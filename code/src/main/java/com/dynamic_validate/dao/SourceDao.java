package com.dynamic_validate.dao;

import com.dynamic_validate.entity.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceDao extends JpaRepository<Source, Integer> {
}
