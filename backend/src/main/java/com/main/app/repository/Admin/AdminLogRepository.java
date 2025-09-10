package com.main.app.repository.Admin;

import com.main.app.Enum.EntityType;
import com.main.app.dto.PaginatedResponse;
import com.main.app.model.Admin.AdminLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface AdminLogRepository extends JpaRepository<AdminLog,Long> {

    Page<AdminLog> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<AdminLog> findByEntityOrderByCreatedAtDesc(EntityType entity ,Pageable pageable);

    Page<AdminLog> findByEntityAndEntityIdOrderByCreatedAtDesc(EntityType entity ,Long entityId,Pageable pageable);

}
