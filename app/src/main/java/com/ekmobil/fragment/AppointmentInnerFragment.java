package com.ekmobil.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ekmobil.R;
import com.ekmobil.utility.Constants;
import com.ekmobil.utility.HttpUtility;
import com.ekmobil.utility.ProjectUtility;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentInnerFragment extends Fragment implements View.OnClickListener {
    private Activity nActivity;
    private TextView txt_personalName, txt_appointmentDate, txt_appointment_clock, txt_service;
    //    private RecyclerView rcy_appointment_inner;
    private Button btn_complete_appointment;
    private ImageView img_back;
    private View progress;
    private String hizmetId = "", randevuZamani = "", tahminiBitisZamani = "", personelID = "", serviceDuration = "", clock = "";
//    private EditText edt_search;
//    private ListPopupWindow listPopupWindow;
//    private List<ServiceEntity> list = new ArrayList<>();
//    private AppointmentInnerAdapter adapter = new AppointmentInnerAdapter();

    public AppointmentInnerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointment_inner, container, false);
        defineComponents(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        modifyUIElements();
    }


    private void modifyUIElements() {
        if (getArguments() != null) {
            personelID = getArguments().getString(Constants.BUNDLE_APPOINTMENT_PERSONAL_ID);
            hizmetId = getArguments().getString(Constants.BUNDLE_SERVICE_ID);
            randevuZamani = getArguments().getString(Constants.BUNDLE_APPOINTMENT_DATE) + " " + getArguments().getString(Constants.BUNDLE_APPOINTMENT_CLOCK);
            serviceDuration = getArguments().getString(Constants.BUNDLE_SERVICE_DURATION);

            String clock = getArguments().getString(Constants.BUNDLE_APPOINTMENT_CLOCK);
            try {
                SimpleDateFormat df = new SimpleDateFormat("hh:mm");

                Date date = df.parse(clock);
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.MINUTE, Integer.parseInt(serviceDuration));
                int h = cal.get(Calendar.HOUR_OF_DAY);
                int m = cal.get(Calendar.MINUTE);
                tahminiBitisZamani = getArguments().getString(Constants.BUNDLE_APPOINTMENT_DATE) + " " + h + ":" + m;
            } catch (ParseException e) {
                e.printStackTrace();
            }


            txt_personalName.setText(": " + getArguments().getString(Constants.BUNDLE_APPOINTMENT_PERSONAL_NAME));
            txt_appointmentDate.setText(": " + getArguments().getString(Constants.BUNDLE_APPOINTMENT_DATE));
            txt_appointment_clock.setText(": " + getArguments().getString(Constants.BUNDLE_APPOINTMENT_CLOCK));
            txt_service.setText(": " + getArguments().getString(Constants.BUNDLE_SERVICE_NAME));
        }

        btn_complete_appointment.setOnClickListener(this);
        img_back.setOnClickListener(this);

    }

    private void defineComponents(View view) {
        txt_personalName = view.findViewById(R.id.appointment_inner_txt_hair_dresser);
        txt_appointmentDate = view.findViewById(R.id.appointment_inner_txt_appointment_date);
        txt_appointment_clock = view.findViewById(R.id.appointment_inner_txt_appointment_clock);
        txt_service = view.findViewById(R.id.appointment_inner_txt_appointment_service);

        btn_complete_appointment = view.findViewById(R.id.appointment_inner_appointment_complete);

        img_back = view.findViewById(R.id.appointment_inner_img_back);
        progress = view.findViewById(R.id.progress);
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
            case R.id.appointment_inner_img_back:
                FragmentManager fm = getFragmentManager();
                if (fm.getBackStackEntryCount() > 0) {
                    fm.popBackStack();
                }
                break;
            case R.id.appointment_inner_appointment_complete:
                completeAppointment();
                break;
        }
    }

    private void completeAppointment() {
        if (HttpUtility.isOnline(nActivity)) {
            progress.setVisibility(View.GONE);

            SharedPreferences preferences = nActivity.getSharedPreferences(Constants.PREFERENCE_USER_INFO, Context.MODE_PRIVATE);
            String id = preferences.getString(Constants.PREF_ID, "");
            HttpUrl url = HttpUrl.parse(Constants.URL_TAKE_APPOINTMENT).newBuilder()
                    .addEncodedQueryParameter("hizmetID", hizmetId)
                    .addEncodedQueryParameter("kullaniciID", id)
                    .addEncodedQueryParameter("randevuZamani", randevuZamani)
                    .addEncodedQueryParameter("tahminiBitisZamani", tahminiBitisZamani)
                    .addEncodedQueryParameter("personelID", personelID)
                    .addEncodedQueryParameter("firmaID", Constants.PARAM_FIRMA_ID)
                    .build();

            HttpUtility.get(url.toString(), new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    nActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAdded()) {
                                progress.setVisibility(View.GONE);
                                ProjectUtility.showDialog(nActivity, getString(R.string.connection_error_title), e.getLocalizedMessage(), getString(R.string.messages_ok), null, null, null);
                            }
                        }
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String res = response.body().string();
                    nActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAdded()) {
                                if (res.contains("true")) {
//                                    ProjectUtility.showDialog(nActivity, getString(R.string.messages_info), getString(R.string.appointment_success), getString(R.string.messages_ok), null, null, null);
                                    AppointmentResultFragment fragment = new AppointmentResultFragment();
                                    getFragmentManager().beginTransaction()
                                            .replace(R.id.main_fragment_layout, fragment)
                                            .addToBackStack(null)
                                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                            .commitAllowingStateLoss();

                                }
                            }
                        }
                    });
                }
            });

        } else {
            ProjectUtility.showDialog(nActivity, getString(R.string.connection_error_title), getString(R.string.connection_error_message), getString(R.string.messages_ok), null, null, null);
        }
    }
}
