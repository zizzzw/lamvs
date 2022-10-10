package com.dynamic_validate.dao;

import com.dynamic_validate.entity.TypeLack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TypeLackDao extends JpaRepository<TypeLack, Integer> {

    List<TypeLack> findByNameAndErrClassifyAndFatherId(String name, String errClassify, long fatherId);


    @Transactional
    @Modifying
    @Query(value = "INSERT INTO type_lack(type_table,father_id,`name`,info,err_classify) VALUES (?1,?2,?3,?4,?5) ", nativeQuery = true)
    void insert(String typeTable, long fatherId, String name, String info, String errClassify);

    @Transactional
    @Modifying
    @Query(value = "UPDATE type_lack SET type_table = ?2,father_id=?3,`name`=?4,info = ?5,err_classify=?6 WHERE id=?1", nativeQuery = true)
    int updateById(long id, String typeTable, long fatherId, String name, String info, String errClassify);
}
