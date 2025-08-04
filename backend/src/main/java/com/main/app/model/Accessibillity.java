package com.main.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "accessibillity")
public class Accessibillity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "type")
    private List<String> type;

    @OneToMany(mappedBy = "accessibillity")
    private Set<PlaceFeature> placeFeatures = new LinkedHashSet<>();

}