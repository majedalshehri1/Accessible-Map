package com.wakeb.yusradmin.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReviewResponseDTO {
    @JsonProperty("id")
    public Long id;
    @JsonProperty("userName")
    public String userName;
    @JsonProperty("placeName")
    public String placeName;

    @JsonProperty("rating")
    public Integer rating;
    @JsonProperty("reviewDate")
    public String reviewDate;

    @JsonProperty("description")
    @SerializedName("description")
    public String description;

    public String status;

    public Long getId() { return id; }
    public String getUserName() { return userName; }
    public String getPlaceName() { return placeName; }
    public Integer getRating() { return rating; }
    public String getReviewDate() { return reviewDate; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
}
