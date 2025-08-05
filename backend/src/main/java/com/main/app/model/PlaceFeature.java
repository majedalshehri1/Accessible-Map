package com.main.app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@Table(name = "place_feature")
public class PlaceFeature {
    @Id
    @ColumnDefault("nextval('place_feature_place_feature_id_seq')")
    @Column(name = "place_feature_id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "place_id" , nullable = false)
    private Place place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accessibillity_id" , nullable = false)
    private Accessibillity accessibillity;

    @Column(name = "is_avaliable")
    private Boolean isAvaliable;

}