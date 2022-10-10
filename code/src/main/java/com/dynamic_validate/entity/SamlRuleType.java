package com.dynamic_validate.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "saml_rule_type", schema = "check_sys", catalog = "")
public class SamlRuleType {
    private int id;
    private String name;
    private String chinese;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SamlRuleType that = (SamlRuleType) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(chinese, that.chinese);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, chinese);
    }
}
