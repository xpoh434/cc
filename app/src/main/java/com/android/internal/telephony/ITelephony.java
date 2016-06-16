package com.android.internal.telephony;

/**
 * Created by shan on 6/16/16.
 */
public interface ITelephony {
    boolean endCall();

    void answerRingingCall();

    void silenceRinger();
}
