package com.main.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.main.app.Enum.AccessibillityType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "place_feature")
public class PlaceFeature {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_feature_id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id", nullable = false)
    @JsonIgnore
    private Place place;

    @Enumerated(EnumType.STRING)
    @Column(name = "accessibillity_type")
    private AccessibillityType accessibillityType;

    @Column(name = "is_avaliable")
    private Boolean isAvaliable;
}