package com.dynamic_validate.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "type_lack", schema = "check_sys", catalog = "")
public class TypeLack {
    private long id;
    private String typeTable;
    private Integer fatherLevel;
    private String fatherName;
    private Long fatherId;
    private String name;
    private String info;
    private String errClassify;

    @Id
    @Column(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
    @Column(name = "father_level")
    public Integer getFatherLevel() {
        return fatherLevel;
    }

    public void setFatherLevel(Integer fatherLevel) {
        this.fatherLevel = fatherLevel;
    }

    @Basic
    @Column(name = "father_name")
    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    @Basic
    @Column(name = "father_id")
    public Long getFatherId() {
        return fatherId;
    }

    public void setFatherId(Long fatherId) {
        this.fatherId = fatherId;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    @Column(name = "err_classify")
    public String getErrClassify() {
        return errClassify;
    }

    public void setErrClassify(String errClassify) {
        this.errClassify = errClassify;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TypeLack typeLack = (TypeLack) o;
        return id == typeLack.id &&
                Objects.equals(typeTable, typeLack.typeTable) &&
                Objects.equals(fatherLevel, typeLack.fatherLevel) &&
                Objects.equals(fatherName, typeLack.fatherName) &&
                Objects.equals(fatherId, typeLack.fatherId) &&
                Objects.equals(name, typeLack.name) &&
                Objects.equals(info, typeLack.info) &&
                Objects.equals(errClassify, typeLack.errClassify);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, typeTable, fatherLevel, fatherName, fatherId, name, info, errClassify);
    }
}
