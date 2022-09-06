package com.example.dynamic_validate.dao;

import com.example.dynamic_validate.entity.SamlTypeDepend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SamlTypeDependDao extends JpaRepository<SamlTypeDepend, Integer> {

    //@Transactional
    //@Modifying
    //@Query(value = "UPDATE saml_type_depend SET (id,`level`,path,`name`,exp,father,list1_exp,list2_exp,list1,list2,project,state,static_mark,valid) = (SELECT id,`level`,path,`name`,exp,father,list1_exp,list2_exp,list1,list2,project,state,static_mark,valid FROM saml_type WHERE id=?)", nativeQuery = true)
    //int updateFromType(int id);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO saml_type_depend(id,`level`,path,`name`,exp,father,list1_exp,list2_exp,list1,list2,project,state,static_mark,valid) SELECT id,`level`,path,`name`,exp,father,list1_exp,list2_exp,list1,list2,project,state,static_mark,valid FROM saml_type WHERE id=?", nativeQuery = true)
    int insertFromType(int id);

    @Query(value = "SELECT GROUP_CONCAT(t.list1_exp) FROM (SELECT list1_exp FROM saml_type_depend) t", nativeQuery = true)
    String findAllList1Exp();

    @Query(value = "SELECT GROUP_CONCAT(t.list2_exp) FROM (SELECT list2_exp FROM saml_type_depend) t", nativeQuery = true)
    String findAllList2Exp();

    SamlTypeDepend findSamlTypeDependById(int id);
}
