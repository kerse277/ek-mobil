package com.ekmobil.fragment;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import com.ekmobil.ClockChoicesActivity;
import com.ekmobil.R;
import com.ekmobil.utility.Constants;
import com.ekmobil.utility.ProjectUtility;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentDetailFragment extends Fragment implements View.OnClickListener {
    private Activity nActivity;
    private View progress;
    private EditText edt_calendar, edt_clock;
    private Button btn_continue;
    private ImageView img_calendar, img_clock;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public AppointmentDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointment_detail, container, false);
        defineComponents(view);
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("saat");
                edt_clock.setText(result);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupCalendarProperties();
    }

    private void setupCalendarProperties() {
        Calendar calendar = Calendar.getInstance();

        datePickerDialog = new DatePickerDialog(nActivity, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);

                Calendar currenDate = Calendar.getInstance();
                if (newDate.getTime().before(currenDate.getTime())) {
                    ProjectUtility.showDialog(nActivity, getString(R.string.messages_info), getString(R.string.appointment_date_error), getString(R.string.messages_ok), null, null, null);
                } else {
                    edt_calendar.setText(dateFormat.format(newDate.getTime()));
                }

//                progress.setVisibility(View.VISIBLE);
//                getData(dateFormat.format(newDate.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }


    private void defineComponents(View view) {
        progress = view.findViewById(R.id.progress);
        edt_calendar = view.findViewById(R.id.appointment_detail_edt_calendar);
        edt_clock = view.findViewById(R.id.appointment_detail_edt_clock);

        img_calendar = view.findViewById(R.id.appointment_detail_img_calendar);
        img_clock = view.findViewById(R.id.appointment_detail_img_clock);
        btn_continue = view.findViewById(R.id.appointment_detail_btn_continue);

        edt_calendar.setOnClickListener(this);
        edt_clock.setOnClickListener(this);
        img_calendar.setOnClickListener(this);
        img_clock.setOnClickListener(this);
        btn_continue.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.nActivity = (Activity) context;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            this.nActivity = activity;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.appointment_detail_edt_calendar:
                datePickerDialog.show();
                break;
            case R.id.appointment_detail_img_calendar:
                datePickerDialog.show();
                break;
            case R.id.appointment_detail_edt_clock:
                if (edt_calendar.getText().toString() != null && edt_calendar.getText().toString().length() > 0) {
                    if (getArguments() != null && getArguments().getString(Constants.BUNDLE_APPOINTMENT_PERSONAL_NAME) != null && getArguments().getString(Constants.BUNDLE_APPOINTMENT_PERSONAL_ID) != null) {
                        Intent intent = new Intent(nActivity, ClockChoicesActivity.class);
                        intent.putExtra(Constants.INTENT_CHOICES_NAME, getArguments().getString(Constants.BUNDLE_APPOINTMENT_PERSONAL_NAME));
                        intent.putExtra(Constants.INTENT_CHOICES_DATE, edt_calendar.getText().toString());
                        intent.putExtra(Constants.INTENT_CHOICES_ID, getArguments().getString(Constants.BUNDLE_APPOINTMENT_PERSONAL_ID));
                        startActivityForResult(intent, 1);
                    }
                } else {
                    ProjectUtility.showDialog(nActivity, getString(R.string.messages_info), getString(R.string.calendar_must_not_be_empty), getString(R.string.messages_ok), null, null, null);
                }
                break;
            case R.id.appointment_detail_img_clock:
                if (edt_calendar.getText().toString() != null && edt_calendar.getText().toString().length() > 0) {
                    if (getArguments() != null && getArguments().getString(Constants.BUNDLE_APPOINTMENT_PERSONAL_NAME) != null && getArguments().getString(Constants.BUNDLE_APPOINTMENT_PERSONAL_ID) != null) {
                        Intent intent = new Intent(nActivity, ClockChoicesActivity.class);
                        intent.putExtra(Constants.INTENT_CHOICES_NAME, getArguments().getString(Constants.BUNDLE_APPOINTMENT_PERSONAL_NAME));
                        intent.putExtra(Constants.INTENT_CHOICES_DATE, edt_calendar.getText().toString());
                        intent.putExtra(Constants.INTENT_CHOICES_ID, getArguments().getString(Constants.BUNDLE_APPOINTMENT_PERSONAL_ID));
                        startActivityForResult(intent, 1);
                    }
                } else {
                    ProjectUtility.showDialog(nActivity, getString(R.string.messages_info), getString(R.string.calendar_must_not_be_empty), getString(R.string.messages_ok), null, null, null);
                }
                break;
            case R.id.appointment_detail_btn_continue:
                if (edt_calendar.getText().toString().length() > 0 && edt_clock.getText().toString().length() > 0) {
                    String personalName = "", personalId = "", serviceId = "", serviceName = "", serviceDuration="";
                    if (getArguments() != null && getArguments().getString(Constants.BUNDLE_APPOINTMENT_PERSONAL_NAME) != null && getArguments().getString(Constants.BUNDLE_APPOINTMENT_PERSONAL_ID) != null) {
                        personalName = getArguments().getString(Constants.BUNDLE_APPOINTMENT_PERSONAL_NAME);
                        personalId = getArguments().getString(Constants.BUNDLE_APPOINTMENT_PERSONAL_ID);
                        serviceId = getArguments().getString(Constants.BUNDLE_SERVICE_ID);
                        serviceName = getArguments().getString(Constants.BUNDLE_SERVICE_NAME);
                        serviceDuration = getArguments().getString(Constants.BUNDLE_SERVICE_DURATION);
                    }

                    AppointmentInnerFragment fragment = new AppointmentInnerFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.BUNDLE_APPOINTMENT_PERSONAL_NAME, personalName);
                    bundle.putString(Constants.BUNDLE_APPOINTMENT_PERSONAL_ID, personalId);
                    bundle.putString(Constants.BUNDLE_APPOINTMENT_DATE, edt_calendar.getText().toString());
                    bundle.putString(Constants.BUNDLE_APPOINTMENT_CLOCK, edt_clock.getText().toString());
                    bundle.putString(Constants.BUNDLE_SERVICE_ID, serviceId);
                    bundle.putString(Constants.BUNDLE_SERVICE_NAME, serviceName);
                    bundle.putString(Constants.BUNDLE_SERVICE_DURATION, serviceDuration);

                    fragment.setArguments(bundle);


                    nActivity.getFragmentManager()
                            .beginTransaction()
                            .addToBackStack(null)
                            .replace(R.id.main_fragment_layout, fragment)
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commitAllowingStateLoss();
                } else {
                    ProjectUtility.showDialog(nActivity, getString(R.string.messages_info), getString(R.string.empty_field), getString(R.string.messages_ok), null, null, null);

                }

                break;
        }
    }
}
