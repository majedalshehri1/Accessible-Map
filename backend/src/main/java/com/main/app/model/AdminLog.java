package com.main.app.model;

import com.main.app.Enum.Action;
import com.main.app.Enum.EntityType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Data
@Table(name = "admin_log" , indexes = {
        @Index(name = "idx_admin_log_entity_target", columnList = "entity,entityId"),
        @Index(name = "idx_admin_log_created_at", columnList = "created_at")
})
@Entity
public class AdminLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private EntityType entity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private Action action;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "actor_user_id")
    private Long actorUserId;

    @Column(name = "actor_name")
    private String actorName;

    @Column(name = "description", length = 500)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


}
