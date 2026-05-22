package model;

import java.math.BigDecimal;

public class Food {
    private int foodId;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;
    private String category;
    private double rating;
    private int totalReviews;

    public Food() {}

    public Food(int foodId, String name, String description, BigDecimal price,
                String image, String category, double rating, int totalReviews) {
        this.foodId = foodId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.image = image;
        this.category = category;
        this.rating = rating;
        this.totalReviews = totalReviews;
    }

    public int getFoodId() { return foodId; }
    public void setFoodId(int foodId) { this.foodId = foodId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
}
