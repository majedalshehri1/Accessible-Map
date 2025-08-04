package com.main.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(name = "place_category", nullable = false, length = Integer.MAX_VALUE)
    private String placeCategory;

}