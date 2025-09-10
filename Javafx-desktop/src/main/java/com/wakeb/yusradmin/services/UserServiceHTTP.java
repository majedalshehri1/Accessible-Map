// com.wakeb.yusradmin.services.UserServiceHTTP.java
package com.wakeb.yusradmin.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.wakeb.yusradmin.models.PageResponse;
import com.wakeb.yusradmin.models.User;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class UserServiceHTTP implements UserService {
    private final ApiClient api;
    public UserServiceHTTP(ApiClient api) { this.api = api; }

    @Override
    public List<User> list() throws Exception {
        PageResponse<User> page = api.get(
                "/api/admin/all/users?page=0&size=50",
                new TypeReference<PageResponse<User>>() {}
        );
        return (page != null && page.content != null) ? page.content : Collections.emptyList();
    }

    public PageResponse<User> page(int page, int size) throws Exception {
        return api.get(
                "/api/admin/all/users?page=" + page + "&size=" + size,
                new TypeReference<PageResponse<User>>() {}
        );
    }

    @Override
    public List<User> search(String email) throws Exception {
        String q = URLEncoder.encode(email, StandardCharsets.UTF_8);
        return api.get("/api/admin/searchUser?email=" + q, new TypeReference<List<User>>() {});
    }

    @Override
    public User update(User u) throws Exception {
        return api.put("/api/admin/update/user/" + u.getId(), u, new TypeReference<User>() {});
    }

    @Override public void delete(long userId) throws Exception {
        api.delete("/api/admin/delete/user/" + userId);
    }
    @Override public void block(long userId) throws Exception  {
        api.put("/api/admin/users/" + userId + "/block", null, null);
    }
    @Override public void unblock(long userId) throws Exception{
        api.put("/api/admin/users/" + userId + "/unblock", null, null);
    }
}