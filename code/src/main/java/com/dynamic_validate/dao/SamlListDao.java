package com.dynamic_validate.dao;

import com.dynamic_validate.entity.SamlList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SamlListDao extends JpaRepository<SamlList, Integer> {


    @Query(value = "SELECT * FROM saml_list WHERE valid=1", nativeQuery = true)
    List<SamlList> findValid1();

    @Query(value = "SELECT * FROM saml_list WHERE valid=2", nativeQuery = true)
    List<SamlList> findAllValid2();

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_list SET valid=?2 WHERE id = ?1", nativeQuery = true)
    int updateValid(int id, int valid);

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_list SET valid=2", nativeQuery = true)
    int updateAllValid2();

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_list SET valid=0 WHERE member_type = '' OR member_type = ',' OR member_type LIKE '%,0,%' OR member_type LIKE '0,%' OR member_type LIKE '%,0' OR member_type = '0' OR member_type LIKE '%,,%'", nativeQuery = true)
    int updateValid0();

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_list SET valid=1 WHERE id = ?1", nativeQuery = true)
    int updateValid1(int id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_list SET valid=1 WHERE valid = 2", nativeQuery = true)
    int updateValid1First();

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_list SET state = 1 WHERE id=?1", nativeQuery = true)
    int updateState(int id);

    ///**
    // * 问题：
    // * (1)空表不行，所以这个方法行不通。
    // * (2)事务太长容易死锁，所以大厂一般不连表。
    // */
    //@Deprecated
    //@Transactional
    //@Modifying
    //@Query(value = "INSERT INTO saml_list(members,member_classify,list_compound,exp) SELECT ?1,?2,?3,?4 FROM saml_list WHERE NOT EXISTS (" +
    //        "SELECT members,member_classify,list_compound,exp FROM saml_list " +
    //        "WHERE members=?1 AND member_classify=?2 AND list_compound=?3 AND exp=?4) limit 1;", nativeQuery = true)
    //int insertSelect(String members, int listCompound, String exp);

    ///**
    // * 2022年5月6日 20:39:41：不行了要改成longtext，不能设置唯一索引了，算了，不用这个方法。
    // *
    // * saml_list表设置UNIQUE唯一索引：member_type, member_list, list_compound, exp
    // * sql语句用insert ignore
    // * 问题：
    // * (1)返回的是插入条数，而非ID，如何返回id？
    // * (2)自增id会增长！
    // */
    //@Transactional
    //@Modifying
    //@Query(value = "INSERT IGNORE saml_list(member_type,list_compound,exp) VALUES (?1,?2,?3)", nativeQuery = true)
    //int insertIgnore(String member_type, int listCompound, String exp);


    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_list SET member_type = ?2,list_compound=?3,exp = ?4 WHERE id=?1", nativeQuery = true)
    int updateById(int id, String memberType, int listCompound, String exp);

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_list SET member_type = ?2,exp = ?3 WHERE id=?1", nativeQuery = true)
    int updateById(int id, String memberType, String exp);

    /**
     * 根据exp可以唯一找到相应的ListId。
     */
    List<SamlList> findSamlListByExp(String exp);

    /**
     * 根据Id可以唯一找到相应的List。
     */
    SamlList findSamlListById(int id);


    /**
     * 由于限制了limit 1，所以cout(*)，不存在是0，存在是1。
     */
    @Query(value = "SELECT count(*) FROM saml_list WHERE (member_type=?1 AND member_list=?2 AND list_compound=?3 AND exp=?4) limit 1;", nativeQuery = true)
    int isExist(String member_type, String member_list, int listCompound, String exp);

}
