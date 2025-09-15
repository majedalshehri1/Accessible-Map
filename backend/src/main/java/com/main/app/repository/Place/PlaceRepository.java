package com.main.app.repository.Place;


import com.main.app.Enum.Category;
import com.main.app.dto.Place.TopPlaceDto;
import com.main.app.model.Place.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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
}
