package com.main.app.repository;


import com.main.app.Enum.Category;
import com.main.app.model.Place;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlaceRepository extends JpaRepository<Place,Integer>{

    List<Place> getCategory(Category category);
}
