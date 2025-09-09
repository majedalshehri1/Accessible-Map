package com.main.app.service;

import com.main.app.Enum.Action;
import com.main.app.Enum.EntityType;
import com.main.app.dto.AdminLogDto;
import com.main.app.dto.AuthResponse;
import com.main.app.model.AdminLog;
import com.main.app.model.User;
import com.main.app.repository.AdminLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdminLogService {

    private final AdminLogRepository adminLogRepository;

    public AdminLogDto convertToDTO(AdminLog log) {
        AdminLogDto dto = new AdminLogDto();
        dto.setId(log.getId());
        dto.setEntityType(log.getEntity());
        dto.setAction(log.getAction());
        dto.setEntityId(log.getEntityId());
        dto.setActorUserId(log.getActorUserId());
        dto.setActorName(log.getActorName());
        dto.setDescription(log.getDescription());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }

    public AdminLog writeLog(EntityType entityType,Action action,Long entityId,
                             Long actorUserId,String actorName,String description){
        AdminLog log = new AdminLog();
        log.setEntity(entityType);
        log.setAction(action);
        log.setEntityId(entityId);
        log.setActorUserId(actorUserId);
        log.setActorName(actorName);
        log.setDescription(description);
        return adminLogRepository.save(log);

    }


    public Page<AdminLogDto> list(Integer page, Integer size, EntityType entity, Long entityId) {
        int p = (page == null || page < 0) ? 0 : page;
        int s = (size == null || size < 1) ? 20 : Math.min(size, 200);
        Pageable pageable = PageRequest.of(p, s, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<AdminLog> logs;
        if (entity != null && entityId != null) {
            logs = adminLogRepository.findByEntityAndEntityIdOrderByCreatedAtDesc(entity, entityId, pageable);
        } else if (entity != null) {
            logs = adminLogRepository.findByEntityOrderByCreatedAtDesc(entity, pageable);
        } else {
            logs = adminLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        }
        return logs.map(this::convertToDTO);
    }


}
