package com.wakeb.yusradmin.models;

import javafx.beans.property.*;

public class ReviewRow {
    private final LongProperty id = new SimpleLongProperty();
    private final StringProperty username = new SimpleStringProperty();
    private final StringProperty place = new SimpleStringProperty();
    private final IntegerProperty rating = new SimpleIntegerProperty();
    private final StringProperty date = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();
    private final StringProperty description = new SimpleStringProperty();


    public ReviewRow(long id, String username, String place, int rating,String description, String date, String status) {
        this.id.set(id);
        this.username.set(username);
        this.place.set(place);
        this.rating.set(rating);
        this.date.set(date);
        this.status.set(status);
        this.description.set(description);
    }

    // === Getters + Properties (needed for TableView) ===
    public long getId() { return id.get(); }
    public LongProperty idProperty() { return id; }

    public String getUsername() { return username.get(); }
    public StringProperty usernameProperty() { return username; }

    public String getPlace() { return place.get(); }
    public StringProperty placeProperty() { return place; }

    public int getRating() { return rating.get(); }
    public IntegerProperty ratingProperty() { return rating; }

    public String getDate() { return date.get(); }
    public StringProperty dateProperty() { return date; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }

    public void setStatus(String s) { status.set(s); }

    public void setRating(int r) { rating.set(r); }
    public void setDate(String d) { date.set(d); }
    public String getDescription() { return description.get(); }
    public StringProperty descriptionProperty() { return description; }
    public void setDescription(String d) { description.set(d); }

}
