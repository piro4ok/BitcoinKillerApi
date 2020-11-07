public class GetTransResult {
    private boolean successful;
    private Transaction transaction;
    private String error_message;

    public GetTransResult(boolean operation_result, Transaction transaction, String error_message) {
        this.successful =operation_result;
        this.transaction=transaction;
        this.error_message=error_message;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public String getError_message() {
        return error_message;
    }
}
