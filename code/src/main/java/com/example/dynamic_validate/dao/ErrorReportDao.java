package com.example.dynamic_validate.dao;

import com.example.dynamic_validate.data.Data;
import com.example.dynamic_validate.entity.ErrorReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ErrorReportDao extends JpaRepository<ErrorReport, String> {
    @Query(value = "SELECT report_id FROM error_report WHERE id = (SELECT MAX(id) FROM error_report) ", nativeQuery = true)
    Long findMaxRepId();

    @Transactional
    @Modifying
    @Query(value = "UPDATE error_report SET  start_id= ?2 WHERE id=?1", nativeQuery = true)
    void updateStartId(long id, long startId);

    ErrorReport findByTypeIdAndTypeNameAndReportId(int typeId, String typeName, String reportId);

    Page<ErrorReport> findByReportId(String reportId, Pageable pageable);

    @Query(value = "select * from error_report WHERE report_id = ?3 AND id>=?1 AND id<=?2 AND title LIKE '2%'", nativeQuery = true)
    List<ErrorReport> findTitle2(long start, long end, String reportId);

    @Query(value = "select * from error_report WHERE report_id = ?1 AND title=" + Data.DITitle, nativeQuery = true)
    List<ErrorReport> findDemand(String reportId);

    @Query(value = "select * from error_report WHERE id>=?1 AND id<=?2 AND report_id = ?3 AND title=" + Data.FuncTitle, nativeQuery = true)
    List<ErrorReport> find2Func(long start, long end, String reportId);

    @Query(value = "select * from error_report WHERE  id>=?1 AND id<=?2 AND report_id = ?3 AND title=" + Data.FuncDepTitle, nativeQuery = true)
    List<ErrorReport> findFuncDeps(long start, long end, String reportId);

    @Query(value = "select * from error_report WHERE  id>=?1 AND id<=?2 AND report_id = ?3 AND title=" + Data.FuncInvTitle, nativeQuery = true)
    List<ErrorReport> findFuncInv(long start, long end, String reportId);

    @Query(value = "select * from error_report WHERE  id>=?1 AND id<=?2 AND report_id = ?3 AND title=" + Data.TypeTitle, nativeQuery = true)
    List<ErrorReport> findType(long start, long end, String reportId);

    @Query(value = "select * from error_report WHERE  id>=?1 AND id<=?2 AND report_id = ?3 AND error_classify LIKE '%_1' AND title=" + Data.TypeTitle, nativeQuery = true)
    List<ErrorReport> findRightType(long s, long e, String repId);

    @Query(value = "select * from error_report WHERE  id>=?1 AND id<=?2 AND report_id = ?3 AND error_classify LIKE '%_2' AND title=" + Data.TypeTitle, nativeQuery = true)
    List<ErrorReport> findWrongType(long s, long e, String repId);

    @Query(value = "select * from error_report WHERE  id>=?1 AND id<=?2 AND report_id = ?3 AND title LIKE '4%'", nativeQuery = true)
    List<ErrorReport> findTitle4(long s3, long id3, String repId);

    @Query(value = "select * from error_report WHERE  id>=?1 AND id<=?2 AND report_id = ?3 AND title=" + Data.RelaTitle, nativeQuery = true)
    List<ErrorReport> findRela(long start, long end, String reportId);

    @Query(value = "select * from error_report WHERE  id>=?1 AND id<=?2 AND report_id = ?3 AND error_classify LIKE '%_1' AND title=" + Data.RelaTitle, nativeQuery = true)
    List<ErrorReport> findRightRela(long start, long end, String reportId);

    @Query(value = "select * from error_report WHERE  id>=?1 AND id<=?2 AND report_id = ?3 AND error_classify LIKE '%_2' AND title=" + Data.RelaTitle, nativeQuery = true)
    List<ErrorReport> findWrongRela(long start, long end, String reportId);

    @Query(value = "select * from error_report WHERE  id>=?1 AND id<=?2 AND report_id = ?3 AND title=" + Data.TypeExTitle, nativeQuery = true)
    List<ErrorReport> findTypeEx(long start, long end, String reportId);

    @Query(value = "select * from error_report WHERE  id>=?1 AND id<=?2 AND report_id = ?3 AND error_classify LIKE '%_1' AND title=" + Data.TypeExTitle, nativeQuery = true)
    List<ErrorReport> findRightTypeEx(long start, long end, String reportId);

    @Query(value = "select * from error_report WHERE  id>=?1 AND id<=?2 AND report_id = ?3 AND error_classify LIKE '%_2' AND title=" + Data.TypeExTitle, nativeQuery = true)
    List<ErrorReport> findWrongTypeEx(long start, long end, String reportId);


    @Query(value = "select * from error_report WHERE  id>=?1 AND id<=?2 AND report_id = ?3", nativeQuery = true)
    List<ErrorReport> findProcess(long start, long end, String reportId);

    @Query(value = "select * from error_report WHERE report_id = ?1 AND title=" + Data.TypeTitle, nativeQuery = true)
    List<ErrorReport> findType(String reportId);

    @Query(value = "select * from error_report WHERE type_id = ?1 AND type_name = ?2 AND report_id = ?3 AND title=" + Data.TypeTitle, nativeQuery = true)
    List<ErrorReport> findType(int typeId, String name, String reportId);

    @Query(value = "select * from error_report WHERE report_id = ?4 AND type_name=?1 AND type_table=?2 AND error_classify = ?3", nativeQuery = true)
    List<ErrorReport> exist(String typeName, String typeTable, String errorClassify, String reportId);

    @Query(value = "SELECT MIN(id) FROM error_report WHERE report_id =?1", nativeQuery = true)
    Long findMinId(String repId);

    /**
     * 找到上一个DemandInvoke的id，如果没有，默认返回什么呢？是0还是null?【应该是null】
     */
    @Query(value = "select max(id) from error_report WHERE id<?1 AND report_id = ?2 AND title=" + Data.DITitle, nativeQuery = true)
    Long findLastDId(long diId, String repId);

    @Query(value = "SELECT * FROM error_report WHERE id =?1", nativeQuery = true)
    ErrorReport findById(long id);
}
