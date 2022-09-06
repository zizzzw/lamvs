package com.example.dynamic_validate.dao;

import com.example.dynamic_validate.entity.Project;
import com.example.dynamic_validate.entity.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SourceDao extends JpaRepository<Source, Integer> {
}
