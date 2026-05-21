package model;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {
    private int productId;
    private String name;
    private BigDecimal price;
    private int count;

    public Product() {}

    public Product(String name, BigDecimal price, int count) {
        this.name = name;
        setPrice(price);
        setCount(count);
    }

    public Product(int productId, String name, BigDecimal price, int count) {
        this.productId = productId;
        this.name = name;
        setPrice(price);
        setCount(count);
    }

    // Getters and Setters
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) {
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }

    public int getCount() { return count; }
    public void setCount(int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Stock count cannot be negative");
        }
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productId == product.productId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return "Product{productId=" + productId + ", name='" + name + "', price=" + price + ", count=" + count + "}";
    }
}
