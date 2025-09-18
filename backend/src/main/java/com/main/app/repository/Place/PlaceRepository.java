package com.main.app.repository.Place;


import com.main.app.Enum.Category;
import com.main.app.dto.Place.PlaceWithStatsDTO;
import com.main.app.dto.Place.TopPlaceDto;
import com.main.app.model.Place.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface PlaceRepository extends JpaRepository<Place,Long> {

    List<Place> findByPlaceCategory(Category category);

    List<Place> findByPlaceNameContainingIgnoreCase(String placeName);

    boolean existsByPlaceNameIgnoreCase(String placeName);

    @Query("SELECT p.placeCategory, COUNT(p) FROM Place p GROUP BY p.placeCategory")
    List<Object[]> countPlacesByCategory();

    // Pagination methods
    Page<Place> findAll(Pageable pageable);
    Page<Place> findByPlaceCategory(Category category, Pageable pageable);

    @Query("""
    select
        p.id as id,
        p.placeName as placeName,
        p.placeCategory as placeCategory,
        coalesce(avg(r.rating), 0.0) as avgRating,
        count(r.id) as reviewCount
    from Place p
    left join Review r on r.place = p
    group by p.id, p.placeName, p.placeCategory
    order by coalesce(avg(r.rating), 0.0) desc, count(r.id) desc
    """)
    List<TopPlaceDto> findTopPlaces(Pageable pageable);

    @Query(value = "SELECT * FROM Place " +
            "WHERE ST_Intersects(" +
            "  ST_SetSRID(ST_MakePoint(longitude, latitude), 4326)::geography, " +  // Construct point from lng/lat
            "  ST_SetSRID(ST_MakeEnvelope(?4, ?2, ?3, ?1), 4326)::geography" +    // Envelope: minx(west), miny(south), maxx(east), maxy(north)
            ")",
            nativeQuery = true)
    List<Place> findWithinDistance(Double north, Double south, Double east, Double west);

    @Query(value = "SELECT * FROM Place " +
            "WHERE ST_Intersects(" +
            "  ST_SetSRID(ST_MakePoint(longitude, latitude), 4326), " +
            "  ST_SetSRID(ST_MakeEnvelope(?4, ?2, ?3, ?1), 4326)" +
            ") AND LOWER(place_name) LIKE LOWER(CONCAT('%', ?5, '%'))",
            nativeQuery = true)
    List<Place> findWithinBoundsByName(Double north, Double south, Double east, Double west, String name);

    @Query(value = "SELECT * FROM Place " +
            "WHERE ST_Intersects(" +
            "  ST_SetSRID(ST_MakePoint(longitude, latitude), 4326), " +
            "  ST_SetSRID(ST_MakeEnvelope(?4, ?2, ?3, ?1), 4326)" +
            ") AND place_category = ?5",
            nativeQuery = true)
    List<Place> findWithinBoundsByCategory(Double north, Double south, Double east, Double west, String category);

    @Query(value = "SELECT * FROM Place " +
            "WHERE ST_Intersects(" +
            "  ST_SetSRID(ST_MakePoint(longitude, latitude), 4326), " +
            "  ST_SetSRID(ST_MakeEnvelope(?4, ?2, ?3, ?1), 4326)" +
            ") AND LOWER(place_name) LIKE LOWER(CONCAT('%', ?5, '%')) " +
            "AND place_category = ?6",
            nativeQuery = true)
    List<Place> findWithinBoundsByNameAndCategory(Double north, Double south, Double east, Double west, String name, String category);

    @Query("""
        select
            p.id as id,
            p.placeName as placeName,
            p.placeCategory as placeCategory,
            p.latitude as latitude,
            p.longitude as longitude,
            coalesce(avg(r.rating), 0.0) as avgRating,
            count(r.id) as reviewCount
        from Place p
        left join Review r on r.place = p
        where p.id = :placeId
        group by p.id, p.placeName, p.placeCategory, p.latitude, p.longitude
        order by coalesce(avg(r.rating), 0.0) desc, count(r.id) desc
        """)
    PlaceWithStatsDTO findStatsByPlaceId(@Param("placeId") Long placeId);
}
