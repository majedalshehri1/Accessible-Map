package com.wakeb.yusradmin.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wakeb.yusradmin.dto.AdminLogDto;
import com.wakeb.yusradmin.models.PageResponse;

import javafx.concurrent.Task;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdminLogService {
    private final ApiClient api;

    public AdminLogService() {
        this.api = new ApiClient(AuthService.getInstance().getBaseUrl());
    }

    public Task<PageResponse<AdminLogDto>> pageAsync(int page, int size, String entityTypeOrNull) {
        return new Task<>() {
            @Override
            protected PageResponse<AdminLogDto> call() throws Exception {
                String url = "/admin/logs?page=" + page + "&size=" + size;
                if (entityTypeOrNull != null && !entityTypeOrNull.equals("ALL")) {
                    url += "&entityType=" + entityTypeOrNull;
                }
                return api.get(url, new TypeReference<PageResponse<AdminLogDto>>() {
                });
            }
        };
    }}
