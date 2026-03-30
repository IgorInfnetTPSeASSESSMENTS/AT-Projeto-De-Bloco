package adopet.exception;

public class RemoteServiceException extends ApplicationException {
    private final int statusCode;
    private final String responseBody;

    public RemoteServiceException(String message, int statusCode, String responseBody) {
        super(message);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() { return statusCode; }
    public String getResponseBody() { return responseBody; }
}
