package com.dynamic_validate.entity;

import com.dynamic_validate.data.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "saml_list", schema = "check_sys", catalog = "")
public class SamlList {
    private int id;
    private String memberType;
    private String memberList;
    private Integer listCompound;
    private String exp;
    private Integer valid;
    private Integer state;


    public SamlList() {

    }

    public SamlList(String memberType, Integer listCompound, String exp) {
        this.memberType = memberType;
        this.listCompound = listCompound;
        this.exp = exp;
        this.valid = Data.Valid2;
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
    @Column(name = "list_compound")
    public Integer getListCompound() {
        return listCompound;
    }

    public void setListCompound(Integer listCompound) {
        this.listCompound = listCompound;
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
    @Column(name = "state")
    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Basic
    @Column(name = "member_type")
    public String getMemberType() {
        return memberType;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    @Basic
    @Column(name = "member_list")
    public String getMemberList() {
        return memberList;
    }

    public void setMemberList(String memberList) {
        this.memberList = memberList;
    }

    @Basic
    @Column(name = "valid")
    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SamlList samlList = (SamlList) o;
        return id == samlList.id &&
                Objects.equals(memberType, samlList.memberType) &&
                Objects.equals(memberList, samlList.memberList) &&
                Objects.equals(listCompound, samlList.listCompound) &&
                Objects.equals(exp, samlList.exp) &&
                Objects.equals(valid, samlList.valid) &&
                Objects.equals(state, samlList.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, memberType, memberList, listCompound, exp, valid, state);
    }
}
