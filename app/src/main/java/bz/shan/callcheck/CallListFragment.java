package bz.shan.callcheck;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;
import java.util.UUID;


public class CallListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private static final int REQUEST_CALL = 1;
    private static final String UPDATED_CALL = "updated_call_id";
    private RecyclerView mCallRecyclerView;

    private CallAdapter mAdapter;

    private List<UUID> mUpdated;

    private CallLab callLab;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }

        View view = inflater.inflate(R.layout.fragment_call_list, container, false);

        mCallRecyclerView = (RecyclerView) view
                .findViewById(R.id.call_recycler_view);
        mCallRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        callLab = CallLab.get(getContext());
        updateUI();

        return view;
    }


    private void updateUI() {
        CallLab callLab = CallLab.get(getActivity());
        List<Call> calls = callLab.getCalls();

        if(mAdapter == null) {
            mAdapter = new CallAdapter(calls);
            mCallRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCalls(calls);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }

    private class CallHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Call mCall;
        private TextView mTitleTextView;
        private TextView mNumberTextView;
        private TextView mDateTextView;
        private CheckBox mSolvedCheckBox;

        public CallHolder(View itemView) {
            super(itemView);

            mTitleTextView = (TextView)
                    itemView.findViewById(R.id.list_item_call_title_text_view);
            mDateTextView = (TextView)
                    itemView.findViewById(R.id.list_item_call_date_text_view);
            mNumberTextView = (TextView)
                    itemView.findViewById(R.id.list_item_call_number_text_view);
            mSolvedCheckBox = (CheckBox)
                    itemView.findViewById(R.id.list_item_call_is_junk_check_box);

            itemView.setOnClickListener(this);
        }


        public void bindCall(Call call) {
            mCall = call;
            mTitleTextView.setText(mCall.getName());
            mNumberTextView.setText(mCall.getPhoneNumber());
            mDateTextView.setText(DateFormat.getLongDateFormat(getContext()).format(mCall.getDate()));
            mSolvedCheckBox.setChecked(mCall.isJunk());

            mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mCall.setJunk(isChecked);
                    callLab.updateCall(mCall);
                }
            });
        }

        @Override
        public void onClick(View v) {
            Intent intent = CallPagerActivity.newIntent(getActivity(), mCall.getId());
            startActivity(intent);
        }
    }

    private class CallAdapter extends RecyclerView.Adapter<CallHolder> {

        private List<Call> mCalls;

        public CallAdapter(List<Call> calls) {
            mCalls = calls;
        }

        @Override
        public CallHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater
                    .inflate(R.layout.list_item_call, parent, false);
            return new CallHolder(view);
        }

        @Override
        public void onBindViewHolder(CallHolder holder, int position) {
            Call call = mCalls.get(position);
            holder.bindCall(call);
        }

        @Override
        public int getItemCount() {
            return mCalls.size();
        }

        public void setCalls(List<Call> calls) {
            mCalls = calls;
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_call_list, menu);


        MenuItem subtitleItem = menu.findItem(R.id.menu_item_show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_call:
//                Call call = new Call();
//                CallLab.get(getActivity()).addCall(call);
                Intent intent = CallPagerActivity
                        .newIntent(getActivity(), null);
                startActivity(intent);
                return true;
            case R.id.menu_item_show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean mSubtitleVisible;

    private void updateSubtitle() {
        CallLab callLab = CallLab.get(getActivity());
        int callCount = callLab.getCalls().size();
        String subtitle = getString(R.string.subtitle_format, callCount);

        if (!mSubtitleVisible) {
            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }
}
