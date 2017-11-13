package com.procurement.storage.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "bpe_type")
public class BpeTypeEntity {

    @Id
    @Column(name = "bpe_type_pk")
    private String id;

    @Column(name = "bpe_type_name", nullable = false)
    private String name;

}
