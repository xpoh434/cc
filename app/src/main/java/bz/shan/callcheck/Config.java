package bz.shan.callcheck;

import java.util.Date;
import java.util.UUID;

/**
 * Created by shan on 6/1/16.
 */
public class Config {
    private int mNotId;

    public Config(int notId) {
        mNotId = notId;
    }

    public int getNotId() {
        return mNotId;
    }

    public synchronized int consumeNotId() {
        int cur_notId = mNotId;
        mNotId ++;
        return cur_notId;
    }
}
