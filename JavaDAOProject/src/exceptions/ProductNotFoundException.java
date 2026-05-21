package exceptions;

public class ProductNotFoundException extends ShopException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
