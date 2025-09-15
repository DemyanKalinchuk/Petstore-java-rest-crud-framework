package utils.helpers;

public class WaitHelper {

    public static void justWait(long timeoutInMilliSeconds) {
        try {
            Thread.sleep(timeoutInMilliSeconds);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(exception);
        }
    }
}
