package com.procurement.storage.model.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "bp_types")
public class BpTypeEntity {

    @Id
    @Column(name = "bp_pk")
    private String id;

    @Column(name = "bp_name", nullable = false)
    private String name;
}
