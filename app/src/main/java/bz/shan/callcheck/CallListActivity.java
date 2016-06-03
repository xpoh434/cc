package bz.shan.callcheck;

import android.support.v4.app.Fragment;

/**
 * Created by shan on 6/2/16.
 */
public class CallListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CallListFragment();
    }
}
