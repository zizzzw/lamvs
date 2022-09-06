package com.example.dynamic_validate.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "error_report", schema = "check_sys", catalog = "")
public class ErrorReport {
    private long id;
    private Integer typeId;
    private String typeName;
    private String typeTable;
    private String errorClassify;
    private String info;
    private Integer title;
    private String reportId;
    private Integer startId;

    public ErrorReport() {
    }

    public ErrorReport(Integer typeId, String typeName, String typeTable, String errorClassify, String info, Integer title, String reportId) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.typeTable = typeTable;
        this.errorClassify = errorClassify;
        this.info = info;
        this.title = title;
        this.reportId = reportId;
    }

    public ErrorReport(Integer typeId, String typeName, String typeTable, String errorClassify, String info, String reportId) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.typeTable = typeTable;
        this.errorClassify = errorClassify;
        this.info = info;
        this.reportId = reportId;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Basic
    @Column(name = "type_id")
    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    @Basic
    @Column(name = "type_name")
    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    @Basic
    @Column(name = "type_table")
    public String getTypeTable() {
        return typeTable;
    }

    public void setTypeTable(String typeTable) {
        this.typeTable = typeTable;
    }

    @Basic
    @Column(name = "error_classify")
    public String getErrorClassify() {
        return errorClassify;
    }

    public void setErrorClassify(String errorClassify) {
        this.errorClassify = errorClassify;
    }

    @Basic
    @Column(name = "info")
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Basic
    @Column(name = "title")
    public Integer getTitle() {
        return title;
    }

    public void setTitle(Integer title) {
        this.title = title;
    }

    @Basic
    @Column(name = "report_id")
    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    @Basic
    @Column(name = "start_id")
    public Integer getStartId() {
        return startId;
    }

    public void setStartId(Integer startId) {
        this.startId = startId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorReport that = (ErrorReport) o;
        return id == that.id &&
                Objects.equals(typeId, that.typeId) &&
                Objects.equals(typeName, that.typeName) &&
                Objects.equals(typeTable, that.typeTable) &&
                Objects.equals(errorClassify, that.errorClassify) &&
                Objects.equals(info, that.info) &&
                Objects.equals(title, that.title) &&
                Objects.equals(reportId, that.reportId) &&
                Objects.equals(startId, that.startId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, typeId, typeName, typeTable, errorClassify, info, title, reportId, startId);
    }
}
