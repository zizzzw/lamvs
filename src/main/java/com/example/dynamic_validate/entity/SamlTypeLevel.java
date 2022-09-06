package com.example.dynamic_validate.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "saml_type_level", schema = "check_sys", catalog = "")
public class SamlTypeLevel {
    private int id;
    private String name;
    private String chineseName;
    private String exp;
    private Integer project;

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
    @Column(name = "chinese_name")
    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName(String chineseName) {
        this.chineseName = chineseName;
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
    @Column(name = "project")
    public Integer getProject() {
        return project;
    }

    public void setProject(Integer project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SamlTypeLevel that = (SamlTypeLevel) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(chineseName, that.chineseName) &&
                Objects.equals(exp, that.exp) &&
                Objects.equals(project, that.project);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, chineseName, exp, project);
    }
}
