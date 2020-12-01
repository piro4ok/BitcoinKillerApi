public class AddBlockResult {
    private boolean successful;
    private String error_message;

    public AddBlockResult(boolean operation_result, String error_message) {
        this.successful = operation_result;
        this.error_message = error_message;
    }

    public String getError_message() {
        return error_message;
    }

    public boolean isSuccessful() {
        return successful;
    }
}
