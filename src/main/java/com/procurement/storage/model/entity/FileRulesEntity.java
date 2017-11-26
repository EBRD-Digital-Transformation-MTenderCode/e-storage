package com.procurement.storage.model.entity;

import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "file_rules")
public class FileRulesEntity {

    @Id
    @Column(name = "rule_pk")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_bp_types"))
    private BpTypeEntity bpType;

    @Column(name = "file_ext", nullable = false)
    private String fileExtension;

    @Column(name = "file_size", nullable = false)
    private Integer fileSize;

    @Column(name = "file_is_image")
    private Boolean fileIsImage;
}
