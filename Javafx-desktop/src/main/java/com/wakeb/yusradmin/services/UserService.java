// com.wakeb.yusradmin.services.UserService.java
package com.wakeb.yusradmin.services;

import com.wakeb.yusradmin.models.PaginatedResponse;
import com.wakeb.yusradmin.models.User;
import java.util.List;

public interface UserService {
    PaginatedResponse<User> list(int page, int size) throws Exception;
    PaginatedResponse<User> search(String query, int page, int size) throws Exception;
    List<User> list() throws Exception;
    List<User> search(String email) throws Exception;
    User update(User user) throws Exception;
    void delete(long userId) throws Exception;
    void block(long userId) throws Exception;
    void unblock(long userId) throws Exception;
}
