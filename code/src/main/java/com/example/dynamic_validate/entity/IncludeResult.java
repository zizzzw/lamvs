package com.example.dynamic_validate.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "include_result", schema = "check_sys", catalog = "")
public class IncludeResult {
    private int id;
    private Integer fileId;
    private String funcDeclareList;
    private String structDeclareList;
    private String includeFileList;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "file_id")
    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    @Basic
    @Column(name = "func_declare_list")
    public String getFuncDeclareList() {
        return funcDeclareList;
    }

    public void setFuncDeclareList(String funcDeclareList) {
        this.funcDeclareList = funcDeclareList;
    }

    @Basic
    @Column(name = "struct_declare_list")
    public String getStructDeclareList() {
        return structDeclareList;
    }

    public void setStructDeclareList(String structDeclareList) {
        this.structDeclareList = structDeclareList;
    }

    @Basic
    @Column(name = "include_file_list")
    public String getIncludeFileList() {
        return includeFileList;
    }

    public void setIncludeFileList(String includeFileList) {
        this.includeFileList = includeFileList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IncludeResult that = (IncludeResult) o;
        return id == that.id &&
                Objects.equals(fileId, that.fileId) &&
                Objects.equals(funcDeclareList, that.funcDeclareList) &&
                Objects.equals(structDeclareList, that.structDeclareList) &&
                Objects.equals(includeFileList, that.includeFileList);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, fileId, funcDeclareList, structDeclareList, includeFileList);
    }
}
