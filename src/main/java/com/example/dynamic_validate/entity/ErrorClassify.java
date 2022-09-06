package com.example.dynamic_validate.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "error_classify", schema = "check_sys", catalog = "")
public class ErrorClassify {
    private String id;
    private String errorName;
    private String errorInfo;

    public ErrorClassify() {
    }

    public ErrorClassify(String id, String errorInfo) {
        this.id = id;
        this.errorInfo = errorInfo;
    }

    @Id
    @Column(name = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Basic
    @Column(name = "errorName")
    public String getErrorName() {
        return errorName;
    }

    public void setErrorName(String errorName) {
        this.errorName = errorName;
    }

    @Basic
    @Column(name = "errorInfo")
    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ErrorClassify that = (ErrorClassify) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(errorName, that.errorName) &&
                Objects.equals(errorInfo, that.errorInfo);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, errorName, errorInfo);
    }
}
