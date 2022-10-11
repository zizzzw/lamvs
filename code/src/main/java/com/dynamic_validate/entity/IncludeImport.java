package com.dynamic_validate.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "include_import", schema = "check_sys", catalog = "")
public class IncludeImport {
    private int id;
    private Integer level;
    private String path;
    private String name;
    private String exp;
    private Integer originType;
    private Integer staticMark;
    private Integer pro;

    public IncludeImport() {
    }

    public IncludeImport(String exp) {
        this.exp = exp;
    }

    public IncludeImport(Integer level, String path, String name, String exp, Integer staticMark, Integer pro) {
        this.level = level;
        this.path = path;
        this.name = name;
        this.exp = exp;
        this.staticMark = staticMark;
        this.pro = pro;
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
    @Column(name = "level")
    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
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
    @Column(name = "origin_type")
    public Integer getOriginType() {
        return originType;
    }

    public void setOriginType(Integer originType) {
        this.originType = originType;
    }

    @Basic
    @Column(name = "static_mark")
    public Integer getStaticMark() {
        return staticMark;
    }

    public void setStaticMark(Integer staticMark) {
        this.staticMark = staticMark;
    }

    @Basic
    @Column(name = "pro")
    public Integer getPro() {
        return pro;
    }

    public void setPro(Integer pro) {
        this.pro = pro;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IncludeImport that = (IncludeImport) o;
        return id == that.id &&
                Objects.equals(level, that.level) &&
                Objects.equals(path, that.path) &&
                Objects.equals(name, that.name) &&
                Objects.equals(exp, that.exp) &&
                Objects.equals(originType, that.originType) &&
                Objects.equals(staticMark, that.staticMark) &&
                Objects.equals(pro, that.pro);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, level, path, name, exp, originType, staticMark, pro);
    }
}
