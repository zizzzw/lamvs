package com.example.dynamic_validate.dao;

import com.example.dynamic_validate.entity.IncludeImport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface IncludeImportDao extends JpaRepository<IncludeImport, String> {
    List<IncludeImport> findByLevelAndPathAndExpAndPro(int level, String path, String exp, int pro);

    @Query(value = "SELECT exp FROM include_import WHERE `level`= 8 AND path =?1", nativeQuery = true)
    List<String> findByPath(String path);

    /**
     * exp作为path，寻找exp的集合
     */
    @Query(value = "SELECT exp FROM include_import WHERE `level`= 8 AND path = ?1", nativeQuery = true)
    List<String> getIncByExp(String exp);

    @Query(value = "SELECT exp FROM include_import WHERE `level`= 8 AND pro = ?1 GROUP BY exp", nativeQuery = true)
    List<String> getInclude(int pro);

    @Query(value = "SELECT exp FROM include_import WHERE `level`= 8 AND pro = ?1 AND exp NOT IN (\n" +
            "SELECT path FROM saml_type WHERE project = ?1 AND 2<=`level`<=3 GROUP BY path\n" +
            ") GROUP BY exp", nativeQuery = true)
    List<String> getLackInclude(int pro);

    @Query(value = "SELECT * FROM include_import WHERE exp LIKE '?%'", nativeQuery = true)
    List<IncludeImport> getQuestInclude();

    @Query(value = "SELECT * FROM include_import WHERE exp = ?1 AND pro = ?2", nativeQuery = true)
    List<IncludeImport> getByExpAndPro(String exp, int pro);

    @Transactional
    @Modifying
    @Query(value = "UPDATE include_import SET exp = ?2 WHERE id=?1", nativeQuery = true)
    int updateExp(int id, String exp);

    List<IncludeImport> findByLevelAndPro(int level, int pro);

    @Query(value = "SELECT path FROM include_import WHERE level = ?1 AND origin_type = ?2 AND path LIKE '%.h'", nativeQuery = true)
    List<String> findHFiles(int level, int originType);

    List<IncludeImport> findByLevelAndOriginType(int level, int originType);


    List<IncludeImport> findByLevelAndPathAndOriginType(int level, String path, int ori);

    List<IncludeImport> findByLevelAndPathAndExp(int level, String path, String exp);

    IncludeImport findById(int id);

    @Transactional
    @Modifying
    @Query(value = "UPDATE include_import SET name = ?2 WHERE id=?1", nativeQuery = true)
    void updateName(int id, String name);

    @Transactional
    @Modifying
    @Query(value = "UPDATE include_import SET origin_type = ?2 WHERE id=?1", nativeQuery = true)
    void updateOriginType(int id, int origin);

    @Transactional
    @Modifying
    @Query(value = "UPDATE include_import SET origin_type = ?2 WHERE exp=?1", nativeQuery = true)
    void updateIncludeOrigin(String exp, int origin);

    @Query(value = "SELECT t.ids FROM (select GROUP_CONCAT(id) ids,count(*) c from include_import group by path,`name`,exp) t WHERE t.c>1", nativeQuery = true)
    List<String> findDistinctIds();

    @Query(value = "SELECT t.ids FROM (select GROUP_CONCAT(id) ids,count(*) c from include_import WHERE (`level`= 9 OR `level`=14) group by `level`,path,`name`) t WHERE t.c>1", nativeQuery = true)
    List<String> findDistinctLevel914Ids();

    @Query(value = "SELECT * FROM include_import WHERE `level`=?1 OR `level`=?2", nativeQuery = true)
    List<IncludeImport> findByLevel(int level1, int level2);
}
