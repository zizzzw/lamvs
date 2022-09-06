package com.example.dynamic_validate.dao;

import com.example.dynamic_validate.entity.IncludeResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface IncludeResultDao extends JpaRepository<IncludeResult, String> {
    //@Query(value = "SELECT * FROM include_result WHERE file_id=?1", nativeQuery = true)
    IncludeResult findByFileId(int fileId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE include_result SET include_file_list = ?2 WHERE file_id=?1", nativeQuery = true)
    int updateIncludeList(int fileId, String includeList);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO include_result(file_id,include_file_list) VALUES (?1,?2)", nativeQuery = true)
    int insertIncludeList(int fileId, String includeList);
}
