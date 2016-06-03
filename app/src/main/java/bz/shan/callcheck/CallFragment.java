package bz.shan.callcheck;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

public class CallFragment extends Fragment {

    private static final String DIALOG_DATE = "DialogDate";
    private static final int REQUEST_DATE = 0;
    private EditText mNameField;
    private EditText mNumberField;
    private Call mCall;
    private Button mDateButton;
    private CheckBox mJunkCheckBox;

    private boolean mChanged;
    private Button mTimeButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CALL_ID);
        mCall = CallLab.get(getActivity()).getCall(crimeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.fragment_call, container, false);

        mChanged =false;

        mNameField = (EditText)v.findViewById(R.id.call_title);
        mNameField.setText(mCall.getName());
        mNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mCall.setName(s.toString());
                mChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });

        mNumberField = (EditText)v.findViewById(R.id.call_number);
        mNumberField.setText(mCall.getPhoneNumber());
        mNumberField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
                // This space intentionally left blank
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                mCall.setPhoneNumber(s.toString());
                mChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                // This one too
            }
        });

        mDateButton = (Button)v.findViewById(R.id.call_date);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCall.getDate());
                dialog.setTargetFragment(CallFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mTimeButton = (Button)v.findViewById(R.id.call_time);
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment timeDialog = TimePickerFragment
                        .newInstance(mCall.getDate());
                timeDialog.setTargetFragment(CallFragment.this, REQUEST_DATE);
                timeDialog.show(manager, DIALOG_DATE);
            }
        });

        updateDate();

        mJunkCheckBox = (CheckBox)v.findViewById(R.id.call_is_junk);
        mJunkCheckBox.setChecked(mCall.isJunk());
        mJunkCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCall.setJunk(isChecked);
                mChanged = true;
            }
        });

        return v;
    }

    private String formatDate(Date date) {
        return DateFormat.getLongDateFormat(getContext()).format(date);
    }

    private String formatTime(Date date) {
        return DateFormat.getTimeFormat(getContext()).format(date);
    }

    private void updateDate() {
        mDateButton.setText(formatDate(mCall.getDate()));
        mTimeButton.setText(formatTime(mCall.getDate()));
    }

    private static final String ARG_CALL_ID = "call_id";

    public static CallFragment newInstance(UUID callId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CALL_ID, callId);

        CallFragment fragment = new CallFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCall.setDate(date);
            updateDate();
        }
    }
}
