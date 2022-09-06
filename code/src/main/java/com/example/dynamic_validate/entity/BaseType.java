package com.example.dynamic_validate.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "base_type", schema = "check_sys", catalog = "")
public class BaseType {
    private int id;
    private String name;
    private Integer simplification;

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
    @Column(name = "simplification")
    public Integer getSimplification() {
        return simplification;
    }

    public void setSimplification(Integer simplification) {
        this.simplification = simplification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseType baseType = (BaseType) o;
        return id == baseType.id &&
                Objects.equals(name, baseType.name) &&
                Objects.equals(simplification, baseType.simplification);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, name, simplification);
    }
}
