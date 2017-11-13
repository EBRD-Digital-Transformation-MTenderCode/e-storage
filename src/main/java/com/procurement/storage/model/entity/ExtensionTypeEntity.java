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
@Table(name = "ext_type")
public class ExtensionTypeEntity {

    @Id
    @Column(name = "ext_type_pk")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ext_type_name", nullable = false)
    private String name;

    @Column(name = "is_image")
    private Boolean isImage;
}
