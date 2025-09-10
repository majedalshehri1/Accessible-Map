package com.main.app.service.Admin;

import com.main.app.Enum.Action;
import com.main.app.Enum.EntityType;
import com.main.app.dto.Admin.AdminLogDto;
import com.main.app.dto.PaginatedResponse;
import com.main.app.model.Admin.AdminLog;
import com.main.app.repository.Admin.AdminLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


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


    public PaginatedResponse<AdminLogDto> list(Integer page, Integer size, EntityType entity, Long entityId) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<AdminLog> logs;
        if (entity != null && entityId != null) {
            logs = adminLogRepository.findByEntityAndEntityIdOrderByCreatedAtDesc(entity, entityId, pageable);
        } else if (entity != null) {
            logs = adminLogRepository.findByEntityOrderByCreatedAtDesc(entity, pageable);
        } else {
            logs = adminLogRepository.findAllByOrderByCreatedAtDesc(pageable);
        }

        List<AdminLogDto> logDtos = logs.getContent().stream()
                .map(this::convertToDTO).collect(Collectors.toList());

        return new PaginatedResponse<>(
                logDtos,
                logs.getNumber(),
                logs.getSize(),
                logs.getTotalElements(),
                logs.getTotalPages(),
                logs.isLast()
        );
    }


}
