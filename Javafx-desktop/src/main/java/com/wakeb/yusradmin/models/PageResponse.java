package com.wakeb.yusradmin.models;

import java.util.List;

public class PageResponse<T> {
    public List<T> content;

    public int currentPage;
    public int pageSize;
    public long totalElements;
    public int totalPages;
    public boolean last;
}
