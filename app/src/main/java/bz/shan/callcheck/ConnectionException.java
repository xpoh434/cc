package bz.shan.callcheck;

/**
 * Created by shan on 7/5/16.
 */
public class ConnectionException extends RuntimeException {
    public ConnectionException(Exception e) {
        super(e);
    }
    public ConnectionException(String msg) {
        super(msg);
    }
}
