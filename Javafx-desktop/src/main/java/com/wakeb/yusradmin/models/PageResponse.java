package com.wakeb.yusradmin.models;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class PageResponse<T> {
    public List<T> content;
    public int currentPage;
    public int pageSize;
    public long totalElements;
    public int totalPages;
    public boolean last;

    public PageResponse() {}

    public PageResponse(List<T> content, int currentPage, int pageSize,
                        long totalElements, int totalPages, boolean last) {
        this.content = content;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }
}