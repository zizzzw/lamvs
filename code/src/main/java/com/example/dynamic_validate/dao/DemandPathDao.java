package com.example.dynamic_validate.dao;

import com.example.dynamic_validate.entity.DemandPath;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DemandPathDao extends JpaRepository<DemandPath, Integer> {

    List<DemandPath> findByProjectAndState(int pro, int state);

    List<DemandPath> findByProject(int pro);
}
