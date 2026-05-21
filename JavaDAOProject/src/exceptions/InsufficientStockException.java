package exceptions;

public class InsufficientStockException extends ShopException {
    public InsufficientStockException(String message) {
        super(message);
    }
}
