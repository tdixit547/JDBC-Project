package model;

import java.math.BigDecimal;
import java.util.Objects;

public class Wallet {
    private int walletId;
    private int userId;
    private BigDecimal balance;

    public Wallet() {}

    public Wallet(int userId, BigDecimal balance) {
        this.userId = userId;
        setBalance(balance);
    }

    public Wallet(int walletId, int userId, BigDecimal balance) {
        this.walletId = walletId;
        this.userId = userId;
        setBalance(balance);
    }

    // Getters and Setters
    public int getWalletId() { return walletId; }
    public void setWalletId(int walletId) { this.walletId = walletId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) {
        if (balance != null && balance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wallet wallet = (Wallet) o;
        return walletId == wallet.walletId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(walletId);
    }

    @Override
    public String toString() {
        return "Wallet{walletId=" + walletId + ", userId=" + userId + ", balance=" + balance + "}";
    }
}
