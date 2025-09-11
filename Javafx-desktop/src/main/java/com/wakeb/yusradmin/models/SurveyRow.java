package com.wakeb.yusradmin.models;

public class SurveyRow {
    private Long id;
    private Long userId;
    private String description;
    private Integer rating;

    // UI-only state
    private boolean read;

    public SurveyRow() {}

    public SurveyRow(Long id, Long userId, String description, Integer rating) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.rating = rating;
        this.read = false; // default
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getDescription() { return description; }
    public Integer getRating() { return rating; }
    public boolean isRead() { return read; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setDescription(String description) { this.description = description; }
    public void setRating(Integer rating) { this.rating = rating; }
    public void setRead(boolean read) { this.read = read; }
}
