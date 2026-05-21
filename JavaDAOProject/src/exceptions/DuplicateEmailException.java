package exceptions;

public class DuplicateEmailException extends ShopException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
