package com.main.app.repository.Survey;

import com.main.app.model.Survey.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SurveyRepository extends JpaRepository<Survey,Long> {
    boolean existsByUser_UserId(Long userId);

}
