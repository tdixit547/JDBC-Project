package exceptions;

public class InsufficientBalanceException extends ShopException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
