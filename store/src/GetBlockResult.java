public class GetBlockResult {
    private boolean successful;
    private Block block;
    private String error_message;

    public GetBlockResult(boolean operation_result, Block block, String error_message) {
        this.successful =operation_result;
        this.block=block;
        this.error_message=error_message;
    }

    public String getError_message() {
        return error_message;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public Block getBlock() {
        return block;
    }
}
