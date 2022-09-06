package com.example.dynamic_validate.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Typedef {
    private int id;
    private String path;
    private String exp;
    private String oldType;
    private String newType;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "path")
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Basic
    @Column(name = "exp")
    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Typedef typedef = (Typedef) o;
        return id == typedef.id &&
                Objects.equals(path, typedef.path) &&
                Objects.equals(exp, typedef.exp);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, path, exp);
    }

    @Basic
    @Column(name = "old_type")
    public String getOldType() {
        return oldType;
    }

    public void setOldType(String oldType) {
        this.oldType = oldType;
    }

    @Basic
    @Column(name = "new_type")
    public String getNewType() {
        return newType;
    }

    public void setNewType(String newType) {
        this.newType = newType;
    }
}
