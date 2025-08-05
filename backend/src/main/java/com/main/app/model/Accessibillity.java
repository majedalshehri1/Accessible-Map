package com.main.app.model;

import com.main.app.Enum.AccessibillityType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "accessibility")
public class Accessibility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;


    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private AccessibillityType type;

    @OneToMany(mappedBy = "accessibillity")
    private Set<PlaceFeature> placeFeatures = new LinkedHashSet<>();

}