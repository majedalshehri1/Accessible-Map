package com.wakeb.yusradmin.models;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class SurveyRow {
    private Long id;
    private Long userId;
    private String userName;
    private String description;
    private Integer rating;

    // UI-only: bound to the "Read?" checkbox in the table
    private final BooleanProperty read = new SimpleBooleanProperty(false);

    public SurveyRow() {}

    public SurveyRow(Long id, Long userId, String description, Integer rating) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.rating = rating;
    }

    // --- id ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    // --- userId ---
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    // --- description ---
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // --- rating ---
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    // --- read (JavaFX property) ---
    public boolean isRead() { return read.get(); }
    public void setRead(boolean value) { read.set(value); }
    public BooleanProperty readProperty() { return read; }

    @Override
    public String toString() {
        return "SurveyRow{" +
                "id=" + id +
                ", userId=" + userId +
                ", rating=" + rating +
                ", read=" + isRead() +
                ", description='" + description + '\'' +
                '}';
    }
}
