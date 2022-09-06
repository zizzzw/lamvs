package com.example.dynamic_validate.dao;

import com.example.dynamic_validate.entity.ErrorClassify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorClassifyDao extends JpaRepository<ErrorClassify, String> {
}
