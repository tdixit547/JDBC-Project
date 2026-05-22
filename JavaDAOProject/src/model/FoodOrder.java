package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class FoodOrder {
    private int orderId;
    private int userId;
    private BigDecimal amount;
    private String addressFirstname;
    private String addressLastname;
    private String addressStreet;
    private String addressCity;
    private String addressState;
    private String addressZipcode;
    private String addressCountry;
    private String addressPhone;
    private String status;
    private LocalDateTime orderDate;
    private boolean payment;

    public FoodOrder() {}

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getAddressFirstname() { return addressFirstname; }
    public void setAddressFirstname(String s) { this.addressFirstname = s; }
    public String getAddressLastname() { return addressLastname; }
    public void setAddressLastname(String s) { this.addressLastname = s; }
    public String getAddressStreet() { return addressStreet; }
    public void setAddressStreet(String s) { this.addressStreet = s; }
    public String getAddressCity() { return addressCity; }
    public void setAddressCity(String s) { this.addressCity = s; }
    public String getAddressState() { return addressState; }
    public void setAddressState(String s) { this.addressState = s; }
    public String getAddressZipcode() { return addressZipcode; }
    public void setAddressZipcode(String s) { this.addressZipcode = s; }
    public String getAddressCountry() { return addressCountry; }
    public void setAddressCountry(String s) { this.addressCountry = s; }
    public String getAddressPhone() { return addressPhone; }
    public void setAddressPhone(String s) { this.addressPhone = s; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public boolean isPayment() { return payment; }
    public void setPayment(boolean payment) { this.payment = payment; }
}
