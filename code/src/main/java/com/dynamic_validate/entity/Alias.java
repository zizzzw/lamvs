package com.dynamic_validate.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Alias {
    private int id;
    private String originalType;
    private String alias;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "original_type")
    public String getOriginalType() {
        return originalType;
    }

    public void setOriginalType(String originalType) {
        this.originalType = originalType;
    }

    @Basic
    @Column(name = "alias")
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alias alias1 = (Alias) o;
        return id == alias1.id &&
                Objects.equals(originalType, alias1.originalType) &&
                Objects.equals(alias, alias1.alias);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, originalType, alias);
    }
}
