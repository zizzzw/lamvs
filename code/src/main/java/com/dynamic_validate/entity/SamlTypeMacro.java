package com.dynamic_validate.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "saml_type_macro", schema = "check_sys", catalog = "")
public class SamlTypeMacro {
    private int id;
    private Integer level;
    private String path;
    private String scope;
    private String name;
    private String exp;
    private int father;
    private int list1;
    private int list2;
    private String code;
    private Integer source;
    private Integer project;
    private int valid;
    private Integer state;
    private Integer staticMark;

    public SamlTypeMacro() {
    }

    public SamlTypeMacro(Integer level, String name, String path, String exp, Integer father, Integer project, int staticMark) {
        this.level = level;
        this.name = name;
        this.path = path;
        this.exp = exp;
        this.project = project;
        this.father = father;
        this.list1 = 0;
        this.list2 = 0;
        this.valid = 0;
        this.state = 1;
        this.staticMark = staticMark;
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
    @Column(name = "scope")
    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
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
    @Column(name = "father")
    public int getFather() {
        return father;
    }

    public void setFather(int father) {
        this.father = father;
    }

    @Basic
    @Column(name = "list1")
    public int getList1() {
        return list1;
    }

    public void setList1(int list1) {
        this.list1 = list1;
    }

    @Basic
    @Column(name = "list2")
    public int getList2() {
        return list2;
    }

    public void setList2(int list2) {
        this.list2 = list2;
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
    @Column(name = "source")
    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
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
    @Column(name = "state")
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Basic
    @Column(name = "static_mark")
    public Integer getStaticMark() {
        return staticMark;
    }

    public void setStaticMark(Integer staticMark) {
        this.staticMark = staticMark;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SamlTypeMacro that = (SamlTypeMacro) o;
        return id == that.id &&
                father == that.father &&
                list1 == that.list1 &&
                list2 == that.list2 &&
                valid == that.valid &&
                Objects.equals(level, that.level) &&
                Objects.equals(path, that.path) &&
                Objects.equals(scope, that.scope) &&
                Objects.equals(name, that.name) &&
                Objects.equals(exp, that.exp) &&
                Objects.equals(code, that.code) &&
                Objects.equals(source, that.source) &&
                Objects.equals(project, that.project) &&
                Objects.equals(state, that.state) &&
                Objects.equals(staticMark, that.staticMark);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, level, path, scope, name, exp, father, list1, list2, code, source, project, valid, state, staticMark);
    }
}
