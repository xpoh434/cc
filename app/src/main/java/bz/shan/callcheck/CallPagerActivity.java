package bz.shan.callcheck;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;
import java.util.UUID;

public class CallPagerActivity extends AppCompatActivity {

    private static final String EXTRA_CALL_ID = "bz.shan.callcheck.call_id";
    private ViewPager mViewPager;
    private List<Call> mCalls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_pager);

        UUID callId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CALL_ID);

        mViewPager = (ViewPager) findViewById(R.id.activity_call_pager_view_pager);

        mCalls= CallLab.get(this).getCalls();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            @Override
            public Fragment getItem(int position) {
                if(position >= mCalls.size()) {
                    return CallFragment.newInstance(null);
                } else {
                    Call Call = mCalls.get(position);
                    return CallFragment.newInstance(Call.getId());
                }
            }

            @Override
            public int getCount() {
                return mCalls.size() + 1;
            }
        });

        if(callId !=null) {
            for (int i = 0; i < mCalls.size(); i++) {
                if (mCalls.get(i).getId().equals(callId)) {
                    mViewPager.setCurrentItem(i);
                    break;
                }
            }
        } else {
            mViewPager.setCurrentItem(mCalls.size());
        }
    }

    public static Intent newIntent(Context packageContext, UUID callId) {
        Intent intent = new Intent(packageContext, CallPagerActivity.class);
        intent.putExtra(EXTRA_CALL_ID, callId);
        return intent;
    }

}
