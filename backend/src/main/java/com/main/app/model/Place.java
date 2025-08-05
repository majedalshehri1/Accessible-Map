package com.main.app.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "place")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "place_name", nullable = false, length = Integer.MAX_VALUE)
    private String placeName;

    @NotNull
    @Column(name = "longitude", nullable = false, length = Integer.MAX_VALUE)
    private String longitude;

    @NotNull
    @Column(name = "latitude", nullable = false, length = Integer.MAX_VALUE)
    private String latitude;

    @NotNull
    @Column(name = "place_category", nullable = false, length = Integer.MAX_VALUE)
    private String placeCategory;

    @OneToMany(mappedBy = "place")
    private Set<PlaceFeature> placeFeatures = new LinkedHashSet<>();

    @OneToMany(mappedBy = "place")
    private Set<Review> reviews = new LinkedHashSet<>();

}