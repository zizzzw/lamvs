package com.example.dynamic_validate.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "saml_relation", schema = "check_sys", catalog = "")
public class SamlRelation {
    private int id;
    private Integer relationClassify;
    private String exp;
    private Integer field1;
    private Integer field2;
    private Integer field2Classify;
    private Integer project;

    public SamlRelation() {
    }

    public SamlRelation(String exp) {
        this.exp = exp;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)//加上这条注解后，save方法就会返回数据库id了！
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "relation_classify")
    public Integer getRelationClassify() {
        return relationClassify;
    }

    public void setRelationClassify(Integer relationClassify) {
        this.relationClassify = relationClassify;
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
    public Integer getField1() {
        return field1;
    }

    public void setField1(Integer field1) {
        this.field1 = field1;
    }

    @Basic
    @Column(name = "field2")
    public Integer getField2() {
        return field2;
    }

    public void setField2(Integer field2) {
        this.field2 = field2;
    }

    @Basic
    @Column(name = "field2_classify")
    public Integer getField2Classify() {
        return field2Classify;
    }

    public void setField2Classify(Integer field2Classify) {
        this.field2Classify = field2Classify;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SamlRelation that = (SamlRelation) o;
        return id == that.id &&
                Objects.equals(relationClassify, that.relationClassify) &&
                Objects.equals(exp, that.exp) &&
                Objects.equals(field1, that.field1) &&
                Objects.equals(field2, that.field2) &&
                Objects.equals(field2Classify, that.field2Classify);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, relationClassify, exp, field1, field2, field2Classify);
    }

    @Basic
    @Column(name = "project")
    public Integer getProject() {
        return project;
    }

    public void setProject(Integer project) {
        this.project = project;
    }
}
