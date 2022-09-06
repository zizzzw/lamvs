package com.example.dynamic_validate.dao;

import com.example.dynamic_validate.data.Data;
import com.example.dynamic_validate.entity.DemandInvoke;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DemandInvokeDao extends JpaRepository<DemandInvoke, Integer> {
    DemandInvoke findById(int id);

    @Query(value = "SELECT * FROM demand_invoke WHERE project=?1 AND func1 != 0 AND func2 !=0 AND valid=1", nativeQuery = true)
    List<DemandInvoke> findByValid1(int pro);

    @Query(value = "SELECT * FROM demand_invoke WHERE project=?1 AND func1 != 0 AND func2 !=0 AND valid=0", nativeQuery = true)
    List<DemandInvoke> findByValid0(int pro);

    @Query(value = "SELECT * FROM demand_invoke WHERE project=?1 AND func1 != 0 AND func2 !=0", nativeQuery = true)
    List<DemandInvoke> findByProject(int pro);


    @Query("SELECT d FROM DemandInvoke d WHERE d.project=?1 AND d.func1 <> 0 AND d.func2 <> 0")
    Page<DemandInvoke> findByProject(int pro, Pageable pageable);

    @Query(value = "SELECT * FROM demand_invoke WHERE project=?1 AND func1 != 0 AND func2 !=0 AND valid=2", nativeQuery = true)
    List<DemandInvoke> findByProjectLastWrong(int pro);

    @Query(value = "SELECT * FROM demand_invoke WHERE project=?2 AND exp= ?1", nativeQuery = true)
    List<DemandInvoke> findByExpAndProject(String exp, int pro);

    /**
     * 永远是state=1。
     *
     * 根据demand_path_list的Id查找所有的demand_path。
     *
     */
    @Query(value = "SELECT * FROM demand_invoke WHERE id IN (" +
            "SELECT t.id FROM demand_invoke t LEFT JOIN demand_path_map t1 on t.id = t1.demand_invoke_id " +
            "LEFT JOIN demand_path t2 on t1.demand_path_id = t2.id " +
            "WHERE t2.id= ?1 ) AND state=" + Data.State, nativeQuery = true)
    List<DemandInvoke> findByDemandPath(int demandPathId);

    /**
     * 寻找State=Data.State=1的数据。
     *
     *      注意！state只是用户删除与否的问题，不是valid，默认肯定是1的！！
     */
    @Query(value = "SELECT * FROM demand_invoke WHERE relation_classify = ?1 AND state=" + Data.State, nativeQuery = true)
    List<DemandInvoke> findByRelationClassify(int relationClassify);

    /**
     * 寻找valid=1 且 State=Data.State=1的数据。
     *
     */
    @Query(value = "SELECT * FROM demand_invoke WHERE relation_classify = ?1 AND valid = ?2 AND state=" + Data.State, nativeQuery = true)
    List<DemandInvoke> findByRelationClassifyAndValid(int relationClassify, int valid);


    @Transactional
    @Modifying
    @Query(value = "UPDATE demand_invoke SET func1 = ?2, f1_exp=?3,func2=?4,f2_exp=?5 WHERE id=?1", nativeQuery = true)
    int updateFuncs(int id, int func1, String f1exp, int func2, String f2exp);

    @Transactional
    @Modifying
    @Query(value = "UPDATE demand_invoke SET valid = ?2 WHERE id=?1", nativeQuery = true)
    int updateValid(int id, int vali);

    @Transactional
    @Modifying
    @Query(value = "UPDATE demand_invoke SET valid_ir = ?2 WHERE id=?1", nativeQuery = true)
    int updateValidIr(int id, int vali);

    @Query(value = "SELECT t.ids FROM (select GROUP_CONCAT(id) ids,count(*) c from demand_invoke group by exp) t WHERE t.c>1", nativeQuery = true)
    List<String> findDistinctIds();

    @Query(value = "SELECT GROUP_CONCAT(t.func1) FROM (SELECT func1 FROM demand_invoke WHERE func1!=0 AND func2!=0 GROUP BY func1) t", nativeQuery = true)
    String findAllFunc1();

    @Query(value = "SELECT GROUP_CONCAT(t.func2) FROM (SELECT func2 FROM demand_invoke WHERE func1!=0 AND func2!=0 GROUP BY func2) t", nativeQuery = true)
    String findAllFunc2();

    @Query(value = "SELECT GROUP_CONCAT(t.f1_exp) FROM (SELECT f1_exp FROM demand_invoke WHERE func1!=0 AND func2!=0 GROUP BY func1) t", nativeQuery = true)
    String findAllFunc1Name();
    @Query(value = "SELECT GROUP_CONCAT(t.f2_exp) FROM (SELECT f2_exp FROM demand_invoke WHERE func1!=0 AND func2!=0 GROUP BY func2) t", nativeQuery = true)
    String findAllFunc2Name();
}
