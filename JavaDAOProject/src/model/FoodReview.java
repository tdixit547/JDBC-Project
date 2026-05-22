package model;

import java.time.LocalDateTime;

public class FoodReview {
    private int reviewId;
    private int foodId;
    private int userId;
    private double rating;
    private String comment;
    private LocalDateTime reviewDate;

    public FoodReview() {}

    public int getReviewId() { return reviewId; }
    public void setReviewId(int reviewId) { this.reviewId = reviewId; }
    public int getFoodId() { return foodId; }
    public void setFoodId(int foodId) { this.foodId = foodId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDateTime reviewDate) { this.reviewDate = reviewDate; }
}
