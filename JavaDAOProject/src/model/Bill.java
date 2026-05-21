package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

public class Bill {
    private int billId;
    private int userId;
    private BigDecimal totalAmount;
    private LocalDateTime billDate;
    private String status;

    public Bill() {}

    public Bill(int userId, BigDecimal totalAmount) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.status = "COMPLETED";
    }

    public Bill(int billId, int userId, BigDecimal totalAmount, LocalDateTime billDate, String status) {
        this.billId = billId;
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.billDate = billDate;
        this.status = status;
    }

    // Getters and Setters
    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getBillDate() { return billDate; }
    public void setBillDate(LocalDateTime billDate) { this.billDate = billDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bill bill = (Bill) o;
        return billId == bill.billId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(billId);
    }

    @Override
    public String toString() {
        return "Bill{billId=" + billId + ", userId=" + userId + ", totalAmount=" + totalAmount +
               ", billDate=" + billDate + ", status='" + status + "'}";
    }
}
