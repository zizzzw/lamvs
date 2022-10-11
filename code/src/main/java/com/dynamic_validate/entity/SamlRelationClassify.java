package com.dynamic_validate.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "saml_relation_classify", schema = "check_sys", catalog = "")
public class SamlRelationClassify {
    private int id;
    private String name;
    private String chinese;
    private String exp;
    private String field1;
    private String field2;
    private String illustration;
    private String remark;
    private Integer type;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
    @Column(name = "chinese")
    public String getChinese() {
        return chinese;
    }

    public void setChinese(String chinese) {
        this.chinese = chinese;
    }

    @Basic
    @Column(name = "exp")
    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    @Basic
    @Column(name = "field1")
    public String getField1() {
        return field1;
    }

    public void setField1(String field1) {
        this.field1 = field1;
    }

    @Basic
    @Column(name = "field2")
    public String getField2() {
        return field2;
    }

    public void setField2(String field2) {
        this.field2 = field2;
    }

    @Basic
    @Column(name = "illustration")
    public String getIllustration() {
        return illustration;
    }

    public void setIllustration(String illustration) {
        this.illustration = illustration;
    }

    @Basic
    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Basic
    @Column(name = "type")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SamlRelationClassify that = (SamlRelationClassify) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(chinese, that.chinese) &&
                Objects.equals(exp, that.exp) &&
                Objects.equals(field1, that.field1) &&
                Objects.equals(field2, that.field2) &&
                Objects.equals(illustration, that.illustration) &&
                Objects.equals(remark, that.remark) &&
                Objects.equals(type, that.type);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, chinese, exp, field1, field2, illustration, remark, type);
    }
}
