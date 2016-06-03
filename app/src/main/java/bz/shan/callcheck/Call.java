package bz.shan.callcheck;

import java.util.Date;
import java.util.UUID;

/**
 * Created by shan on 6/1/16.
 */
public class Call {
    private String mName;
    private String mPhoneNumber;
    private Date mDate;
    private boolean isJunk;
    private UUID mId;

    public Call() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public boolean isJunk() {
        return isJunk;
    }

    public void setJunk(boolean junk) {
        isJunk = junk;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public UUID getId() { return mId; }
}
