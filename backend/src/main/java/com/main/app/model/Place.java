package com.main.app.model;

import com.main.app.Enum.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "place")
public class Place {
    @Id
    @ColumnDefault("nextval('place_place_id_seq')")
    @Column(name = "place_id", nullable = false)
    private Integer id;

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
    @Enumerated(EnumType.STRING)
    @Column(name = "place_category", nullable = false, length = Integer.MAX_VALUE)
    private Category placeCategory;

}