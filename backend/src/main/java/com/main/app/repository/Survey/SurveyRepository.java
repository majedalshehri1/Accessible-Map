package com.main.app.repository.Survey;

import com.main.app.model.Survey.Survey;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SurveyRepository extends JpaRepository<Survey,Long> {



//    @org.springframework.data.jpa.repository.Query(
//            "select s from Survey s join fetch s.user"
//    )
//    List<Survey> findAllWithUser();   // <--- new method
    boolean existsByUser_UserId(Long userId);

}
