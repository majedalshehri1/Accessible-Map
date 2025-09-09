package com.main.app.model.Place;

import com.main.app.Enum.Category;
import com.main.app.model.Review.Review;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "place")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "place_id", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "place_name", nullable = false)
    private String placeName;

    @NotNull
    @Column(name = "longitude", nullable = false, length = Integer.MAX_VALUE)
    private String longitude;

    @NotNull
    @Column(name = "latitude", nullable = false, length = Integer.MAX_VALUE)
    private String latitude;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "place_category", nullable = false, length = Integer.MAX_VALUE)
    private Category placeCategory;

    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlaceImage> images;


    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Review> reviews;

    @OneToMany(mappedBy = "place", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PlaceFeature> placeFeatures;


}