package com.wakeb.yusradmin.dto;
// Dummy data purpose for overview
public class CategoryCount {
    private String category;
    private int count;

    public CategoryCount() {
    }

    public CategoryCount(String category, int count) {
        this.category = category;
        this.count = count;
    }

    // Getters and Setters
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}