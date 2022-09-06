package com.example.dynamic_validate.dao;

import com.example.dynamic_validate.entity.SamlType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Repository
public interface SamlTypeDao extends JpaRepository<SamlType, Integer> {

    @Query(value = "SELECT * FROM saml_type WHERE list1_exp LIKE '%\\\\{%' OR  list1_exp LIKE '%\\\\}%' OR list2_exp LIKE '%\\\\{%' OR  list2_exp LIKE '%\\\\}%'", nativeQuery = true)
    List<SamlType> findWrongExp();

    @Query(value = "SELECT t.ids FROM (select GROUP_CONCAT(id) ids,count(*) c from saml_type group by path,exp ORDER BY id ASC ) t WHERE t.c>1", nativeQuery = true)
    List<String> findDistinctByExpPath();

    @Query(value = "SELECT t.ids FROM (select GROUP_CONCAT(id) ids,count(*) c from saml_type group by `level`,path,`name`) t WHERE t.c>1", nativeQuery = true)
    List<String> findDistinctIds();

    @Query(value = "SELECT MAX(id) FROM saml_type", nativeQuery = true)
    int findMaxId();

    List<SamlType> findByNameAndPathAndProject(String name, String path, int pro);

    List<SamlType> findByPathAndExpAndProject(String path, String exp, int pro);

    List<SamlType> findByLevelAndPathAndExpAndProject(int level, String path, String exp, int pro);

    List<SamlType> findByLevelAndNameAndPathAndProject(int level, String name, String path, int pro);


    List<SamlType> findByNameAndProject(String name, int pro);

    List<SamlType> findByLevelAndProject(int level, int pro);

    @Query("SELECT t FROM SamlType t WHERE t.project=?1 AND 2 <= t.level AND t.level <= 6 AND  (t.name='open.c' OR t.path LIKE 'fs%open.c' OR t.path = '/')")
    Page<SamlType> findOpen(int pro, Pageable pageable);


    @Query(value = "select * from saml_type WHERE level =?1 AND valid=2 AND project = ?2", nativeQuery = true)
    List<SamlType> findByLevelAndProjectValid2(int level, int pro);


    @Query(value = "select * from saml_type WHERE level >=?1 AND level<=?2 AND project = ?3", nativeQuery = true)
    List<SamlType> findByLevelAndProject(int level1, int level2, int pro);

    @Query(value = "select * from saml_type WHERE level NOT IN (1,5,6,11,12) AND project = ?1", nativeQuery = true)
    List<SamlType> findByLevelAndProject(int pro);

    /**
     * 这次先用0凑合用，之后father也默认改成一个特殊的数再说。
     */
    @Query(value = "select * from saml_type WHERE level >=?1 AND level<=?2 AND father=0 AND project = ?3", nativeQuery = true)
    List<SamlType> findByLevelAndProjectNotdeal(int level1, int level2, int pro);

    @Query(value = "select * from saml_type WHERE level >=?1 AND level<=?2 AND path = ?3 AND project = ?4", nativeQuery = true)
    List<SamlType> findByLevelAndPathAndProject(int level1, int level2, String path, int pro);

    List<SamlType> findByLevelAndStateAndProject(int level, int state, int pro);

    List<SamlType> findByLevelAndState(int level, int state);

    @Query(value = "SELECT * FROM saml_type WHERE id >=?3 AND level =?1 AND state = ?2", nativeQuery = true)
    List<SamlType> findByLevelAndState(int level, int state, int minId);


    List<SamlType> findByLevel(int level);

    List<SamlType> findByName(String name);

    List<SamlType> findByNameAndLevel(String name, int level);

    List<SamlType> findByNameAndLevelAndProject(String name, int level, int pro);

    SamlType findSamlTypeById(int Id);

    @Query(value = "select group_concat(id) as ids, group_concat(name) as exp from saml_type WHERE path=?1 and level=?2 and project=?3", nativeQuery = true)
    Map<String, String> getListByLevel(String file, int level, int pro);

    //@Query(value = "select path from saml_type WHERE project = ?1 AND `level`!=1 AND `level`!=4 AND `level`!=5 AND `level`!=6 group by path", nativeQuery = true)
    @Query(value = "select path from saml_type WHERE project = ?1 AND `level` NOT IN (1,4,5,6) group by path", nativeQuery = true)
    List<String> getAllFilePath(int pro);

    @Query(value = "select path from saml_type WHERE project = ?1 and level=4 group by path", nativeQuery = true)
    List<String> getAllDentryPath(int pro);

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_type SET list1_exp = ?2 WHERE list1=?1", nativeQuery = true)
    int updateList1exp(int l1d, String l1exp);

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_type SET list2_exp = ?2 WHERE list2=?1", nativeQuery = true)
    int updateList2exp(int l2d, String l2exp);

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_type SET valid = 1 WHERE id=?1", nativeQuery = true)
    int updateValid(int id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_type SET valid = ?2 WHERE id=?1", nativeQuery = true)
    int updateValid(int id, int vali);

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_type SET exp = ?2 WHERE id=?1", nativeQuery = true)
    int updateExp(int id, String exp);

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_type SET name = ?2 WHERE id=?1", nativeQuery = true)
    int updateName(int id, String name);

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_type SET father = ?2 WHERE id=?1", nativeQuery = true)
    int updateFather(int id, int fatherId);

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_type SET list1_exp = ?2,list2_exp = ?3 WHERE id = ?1", nativeQuery = true)
    int updateListExp(int id, String list1_exp, String list2_exp);

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_type SET list1 = ?2,list2 = ?3 WHERE id = ?1", nativeQuery = true)
    int updateList(int id, int list1_id, int list2_id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_type SET static_mark = 1 WHERE exp LIKE 'static%' AND exp NOT LIKE 'static inline%'", nativeQuery = true)
    void updateStatic();

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_type SET static_mark = ?2 WHERE id=?1", nativeQuery = true)
    void updateStatic(int id, int val);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO saml_type(id,`level`,`name`,exp) SELECT id,1,`name`,`name` FROM base_type", nativeQuery = true)
    void insertBase();

    @Transactional
    @Modifying
    @Query(value = "UPDATE saml_type SET father = 0 WHERE level=6", nativeQuery = true)
    void updateSubsyFather();

    @Query(value = "SELECT COUNT (*) FROM saml_type WHERE level=?2 AND project=?1", nativeQuery = true)
    int findCount(int pro, int level);


    //List<SamlType> findSamlTypesByExpRemark

    // // @Transactional
    // // @Modifying
    // // @Query(value = "DELETE FROM saml_type WHERE id=?", nativeQuery = true)
    // int deleteBatchByIds(List<Integer> ids);

}
