package com.procurement.storage.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "countries")
public class Main {

    @Id
    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

}
