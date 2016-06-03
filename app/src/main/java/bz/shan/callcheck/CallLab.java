package bz.shan.callcheck;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by shan on 6/2/16.
 */
public class CallLab {
    private static CallLab sCallLab;

    private List<Call> mCalls;

    public static CallLab get(Context context) {
        if (sCallLab == null) {
            sCallLab = new CallLab(context);
        }
        return sCallLab;
    }

    private CallLab(Context context) {
        mCalls = new ArrayList<>();

        //test
//        for (int i = 0; i < 100; i++) {
//            Call call = new Call();
//            call.setName("Call #" + i);
//            call.setJunk(i % 2 == 0); // Every other one
//            call.setPhoneNumber(String.format("12345%03d",i));
//            mCalls.add(call);
//        }

    }

    public List<Call> getCalls() {
        return mCalls;
    }

    public Call getCall(UUID id) {
        for (Call c : mCalls) {
            if (id.equals(c.getId())) {
                return c;
            }
        }
        return null;
    }

    public void addCall(Call c) {
        mCalls.add(c);
    }

}