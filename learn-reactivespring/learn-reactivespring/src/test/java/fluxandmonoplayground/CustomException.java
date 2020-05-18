package fluxandmonoplayground;

public class CustomException extends Throwable {
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    private String message;

    public CustomException(Throwable e) {
        this.message=e.getMessage();
    }


}
