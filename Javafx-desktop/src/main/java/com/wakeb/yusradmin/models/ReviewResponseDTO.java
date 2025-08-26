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
    @SerializedName("description")   // ✅ ensure JSON -> field maps
    public String description;

    public String status;



}

