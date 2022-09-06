package com.example.dynamic_validate.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "saml_rule", schema = "check_sys", catalog = "")
public class SamlRule {
    private int id;
    private String conditionList;
    private Integer conclusion;
    private String code;
    private Integer ruleType;
    private Integer state;
    private String remark;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "condition_list")
    public String getConditionList() {
        return conditionList;
    }

    public void setConditionList(String conditionList) {
        this.conditionList = conditionList;
    }

    @Basic
    @Column(name = "conclusion")
    public Integer getConclusion() {
        return conclusion;
    }

    public void setConclusion(Integer conclusion) {
        this.conclusion = conclusion;
    }

    @Basic
    @Column(name = "code")
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Basic
    @Column(name = "rule_type")
    public Integer getRuleType() {
        return ruleType;
    }

    public void setRuleType(Integer ruleType) {
        this.ruleType = ruleType;
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
    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SamlRule samlRule = (SamlRule) o;
        return id == samlRule.id &&
                Objects.equals(conditionList, samlRule.conditionList) &&
                Objects.equals(conclusion, samlRule.conclusion) &&
                Objects.equals(code, samlRule.code) &&
                Objects.equals(ruleType, samlRule.ruleType) &&
                Objects.equals(state, samlRule.state) &&
                Objects.equals(remark, samlRule.remark);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, conditionList, conclusion, code, ruleType, state, remark);
    }
}
