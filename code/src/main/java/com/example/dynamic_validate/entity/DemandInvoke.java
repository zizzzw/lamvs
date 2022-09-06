package com.example.dynamic_validate.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "demand_invoke", schema = "check_sys", catalog = "")
public class DemandInvoke {
    private int id;
    private Integer relationClassify;
    private String name;
    private String exp;
    private Integer func1;
    private String f1Exp;
    private Integer func2;
    private String f2Exp;
    private Integer state;
    private Integer project;
    private int valid;
    private int validIr;

    public DemandInvoke() {
    }

    public DemandInvoke(Integer relationClassify, String name, String exp, Integer project) {
        this.relationClassify = relationClassify;
        this.name = name;
        this.exp = exp;
        this.project = project;
        this.func1 = -1;
        this.func2 = -1;
        this.valid = 2;
        this.validIr = 2;
    }

    public DemandInvoke(Integer relationClassify, Integer func1, Integer func2) {
        this.relationClassify = relationClassify;
        this.func1 = func1;
        this.func2 = func2;
    }

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    @Column(name = "func1")
    public Integer getFunc1() {
        return func1;
    }

    public void setFunc1(Integer func1) {
        this.func1 = func1;
    }

    @Basic
    @Column(name = "f1_exp")
    public String getF1Exp() {
        return f1Exp;
    }

    public void setF1Exp(String f1Exp) {
        this.f1Exp = f1Exp;
    }

    @Basic
    @Column(name = "func2")
    public Integer getFunc2() {
        return func2;
    }

    public void setFunc2(Integer func2) {
        this.func2 = func2;
    }

    @Basic
    @Column(name = "f2_exp")
    public String getF2Exp() {
        return f2Exp;
    }

    public void setF2Exp(String f2Exp) {
        this.f2Exp = f2Exp;
    }

    @Basic
    @Column(name = "state")
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Basic
    @Column(name = "project")
    public Integer getProject() {
        return project;
    }

    public void setProject(Integer project) {
        this.project = project;
    }

    @Basic
    @Column(name = "valid")
    public int getValid() {
        return valid;
    }

    public void setValid(int valid) {
        this.valid = valid;
    }

    @Basic
    @Column(name = "valid_ir")
    public int getValidIr() {
        return validIr;
    }

    public void setValidIr(int validIr) {
        this.validIr = validIr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DemandInvoke that = (DemandInvoke) o;
        return id == that.id &&
                valid == that.valid &&
                validIr == that.validIr &&
                Objects.equals(relationClassify, that.relationClassify) &&
                Objects.equals(name, that.name) &&
                Objects.equals(exp, that.exp) &&
                Objects.equals(func1, that.func1) &&
                Objects.equals(f1Exp, that.f1Exp) &&
                Objects.equals(func2, that.func2) &&
                Objects.equals(f2Exp, that.f2Exp) &&
                Objects.equals(state, that.state) &&
                Objects.equals(project, that.project);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, relationClassify, name, exp, func1, f1Exp, func2, f2Exp, state, project, valid, validIr);
    }
}
