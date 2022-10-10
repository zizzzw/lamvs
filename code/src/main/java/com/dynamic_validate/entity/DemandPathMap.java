package com.dynamic_validate.entity;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "demand_path_map", schema = "check_sys", catalog = "")
public class DemandPathMap {
    private int id;
    private Integer demandPathId;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "demand_path_id")
    public Integer getDemandPathId() {
        return demandPathId;
    }

    public void setDemandPathId(Integer demandPathId) {
        this.demandPathId = demandPathId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DemandPathMap that = (DemandPathMap) o;
        return id == that.id &&
                Objects.equals(demandPathId, that.demandPathId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id, demandPathId);
    }
}
