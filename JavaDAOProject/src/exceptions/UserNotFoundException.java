package exceptions;

public class UserNotFoundException extends ShopException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
