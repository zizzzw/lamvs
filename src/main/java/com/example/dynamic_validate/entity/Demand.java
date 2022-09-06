package com.example.dynamic_validate.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Demand {
    private int id;
    private String demandPathList;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "demand_path_list")
    public String getDemandPathList() {
        return demandPathList;
    }

    public void setDemandPathList(String demandPathList) {
        this.demandPathList = demandPathList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Demand demand = (Demand) o;
        return id == demand.id &&
                Objects.equals(demandPathList, demand.demandPathList);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, demandPathList);
    }
}
