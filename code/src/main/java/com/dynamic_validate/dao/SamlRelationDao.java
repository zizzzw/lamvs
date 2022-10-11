package com.dynamic_validate.dao;

import com.dynamic_validate.entity.SamlRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SamlRelationDao extends JpaRepository<SamlRelation, Integer> {

    @Query(value = "SELECT * FROM saml_relation WHERE relation_classify=?1 AND field1=?2 AND field2=?3", nativeQuery = true)
    List<SamlRelation> findRelation(int relation_classify, int field1, int field2);

    @Query(value = "SELECT * FROM saml_relation WHERE relation_classify=?1 AND field1=?2 AND field2=?3 AND field2_classify=?4", nativeQuery = true)
    List<SamlRelation> findRelation(int relation_classify, int field1, int field2, int field2_classify);

    @Deprecated
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO saml_relation(relation_classify,field1,field2,field2_classify) VALUES(?1,?2,?3,?4)", nativeQuery = true)
    int save(int relationClassify, int field1, int field2, int field2Classify);

    /**
     * 问题：
     * (1)空表不行，所以这个方法行不通。
     * (2)事务太长容易死锁，所以大厂一般不连表。
     */
    @Deprecated
    @Transactional
    @Modifying
    @Query(value = "INSERT INTO saml_relation(relation_classify,field1,field2,field2_classify) " +
            "SELECT ?1,?2,?3,?4 FROM saml_relation " +
            "WHERE NOT EXISTS(" +
            "SELECT 1 FROM saml_relation WHERE relation_classify=?1 AND field1=?2 AND field2=?3 AND field2_classify=?4)" +
            "LIMIT 1", nativeQuery = true)
    int saveNotRepeat(int relationClassify, int field1, int field2, int field2Classify);

    /**
     * mysql设置UNIQUE唯一索引：relation_classify,field1,field2,field2_classify
     * sql语句用insert ignore
     * 问题：
     * (1)返回的是插入条数，而非ID，如何返回id？
     * (2)自增id会增长！
     */
    @Transactional
    @Modifying
    @Query(value = "INSERT IGNORE saml_relation(relation_classify,field1,field2,field2_classify) VALUES (?1,?2,?3,?4)", nativeQuery = true)
    int insertIgnore(int relationClassify, int field1, int field2, int field2Classify);

    @Query(value = "SELECT field1 FROM saml_relation WHERE id = (SELECT MAX(id) FROM saml_relation)", nativeQuery = true)
    int findLastTypeId();
}
