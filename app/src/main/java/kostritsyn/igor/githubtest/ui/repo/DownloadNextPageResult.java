package kostritsyn.igor.githubtest.ui.repo;

public class DownloadNextPageResult {

    private boolean successful;

    private boolean hasMore;

    private String errorMessage;

    private DownloadNextPageResult(boolean successful, boolean hasMore, String errorMessage) {
        this.successful = successful;
        this.hasMore = hasMore;
        this.errorMessage = errorMessage;
    }

    public static DownloadNextPageResult getSuccess(boolean hasMore) {
        return new DownloadNextPageResult(true, hasMore, null);
    }

    public static DownloadNextPageResult getFailed(String errorMessage) {
        return new DownloadNextPageResult(false, true, errorMessage);
    }

    public boolean isSuccessful() {
        return successful;
    }

    public boolean hasMore() {
        return hasMore;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
