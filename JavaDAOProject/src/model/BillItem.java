package model;

import java.math.BigDecimal;
import java.util.Objects;

public class BillItem {
    private int billItemId;
    private int billId;
    private int productId;
    private int quantity;
    private BigDecimal priceAtPurchase;

    public BillItem() {}

    public BillItem(int billId, int productId, int quantity, BigDecimal priceAtPurchase) {
        this.billId = billId;
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }

    public BillItem(int billItemId, int billId, int productId, int quantity, BigDecimal priceAtPurchase) {
        this.billItemId = billItemId;
        this.billId = billId;
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }

    // Getters and Setters
    public int getBillItemId() { return billItemId; }
    public void setBillItemId(int billItemId) { this.billItemId = billItemId; }

    public int getBillId() { return billId; }
    public void setBillId(int billId) { this.billId = billId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPriceAtPurchase() { return priceAtPurchase; }
    public void setPriceAtPurchase(BigDecimal priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BillItem billItem = (BillItem) o;
        return billItemId == billItem.billItemId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(billItemId);
    }

    @Override
    public String toString() {
        return "BillItem{billItemId=" + billItemId + ", billId=" + billId + ", productId=" + productId +
               ", quantity=" + quantity + ", priceAtPurchase=" + priceAtPurchase + "}";
    }
}
