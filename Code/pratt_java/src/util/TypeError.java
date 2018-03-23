package util;

public class TypeError {
    private final String errorMessage;

    public String getErrorMessage() {
        return errorMessage;
    }

    public TypeError(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
