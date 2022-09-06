package com.example.dynamic_validate.dao;

import com.example.dynamic_validate.entity.SamlTypeMacro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Repository
public interface SamlTypeMacroDao extends JpaRepository<SamlTypeMacro, Integer> {
    List<SamlTypeMacro> findByLevelAndPathAndExpAndProject(int level, String path, String exp, int pro);

    @Transactional
    @Modifying
    @Query(value = "TRUNCATE demand_invoke;\n" +
            "TRUNCATE error_report;\n" +
            "TRUNCATE include_import;\n" +
            "TRUNCATE saml_list;\n" +
            "TRUNCATE saml_relation;\n" +
            "TRUNCATE saml_type;\n" +
            "TRUNCATE saml_type_macro;", nativeQuery = true)
    void truncateTable();

    @Transactional
    @Modifying
    //@Query(value = "TRUNCATE saml_list;TRUNCATE saml_relation;TRUNCATE type_lack;", nativeQuery = true)
    @Query(value = "DELETE FROM saml_list;DELETE FROM saml_relation;DELETE FROM type_lack;", nativeQuery = true)
    void initVerify();
}
