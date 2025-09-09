package com.main.app.dto.Admin;

import com.main.app.Enum.Action;
import com.main.app.Enum.EntityType;
import lombok.Data;

import java.time.Instant;



@Data
public class AdminLogDto {
    Long id;
    EntityType entityType;
    Action action;
    Long entityId;
    Long actorUserId;
    String actorName;
    String description;
    Instant createdAt;
}
