package com.gamingtournament.msa.model;

public class ReviewModel {

    private String timestamp, review, uid;
    private float rating;

    public ReviewModel() {
    }

    public ReviewModel(String timestamp, String review, String uid, float rating) {
        this.timestamp = timestamp;
        this.review = review;
        this.uid = uid;
        this.rating = rating;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
