package com.ekmobil.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ekmobil.R;
import com.ekmobil.adapter.PersonelAdapter;
import com.ekmobil.entity.PersonelEntity;
import com.ekmobil.utility.Constants;
import com.ekmobil.utility.HttpUtility;
import com.ekmobil.utility.ProjectUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppointmentFragment extends Fragment {
    private Activity nActivity;
    private RecyclerView rcy_appointment;
    private View progress;
    private String serviceId = "",serviceName="",serviceDuration="";

    public AppointmentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);
        defineComponents(view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getPersonelData();
    }

    private void getPersonelData() {
        progress.setVisibility(View.VISIBLE);
        if (HttpUtility.isOnline(nActivity)) {
            if (getArguments() != null && getArguments().getString(Constants.BUNDLE_SERVICE_ID) != null) {
                serviceId = getArguments().getString(Constants.BUNDLE_SERVICE_ID);
                serviceName=getArguments().getString(Constants.BUNDLE_SERVICE_NAME);
                serviceDuration = getArguments().getString(Constants.BUNDLE_SERVICE_DURATION);

                HttpUrl url = HttpUrl.parse(Constants.URL_PERSONEL).newBuilder()
                        .addQueryParameter("hizmetID", serviceId)
                        .addQueryParameter("firmaID", Constants.PARAM_FIRMA_ID).build();
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
                                    parsePersonelData(res);
                                }
                            }
                        });
                    }
                });
            }


        } else {
            progress.setVisibility(View.GONE);
            ProjectUtility.showDialog(nActivity, getString(R.string.connection_error_title), getString(R.string.connection_error_message), getString(R.string.messages_ok), null, null, null);
        }
    }

    private void parsePersonelData(String res) {
        List<PersonelEntity> list = new ArrayList<>();
        if (res != null && res.length() > 0) {
            try {
                JSONArray array = new JSONArray(res);
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        PersonelEntity entity = new PersonelEntity();
                        entity.setId(ProjectUtility.getJsonValue(object, "personelID").trim());
                        entity.setPersonelImage(ProjectUtility.getJsonValue(object, "personelImg").trim());
//                        entity.setPersonelPhone(ProjectUtility.getJsonValue(object, "telefon").trim());
//                        entity.setPersonelMail(ProjectUtility.getJsonValue(object, "email").trim());
                        entity.setPersonelNameSurname(ProjectUtility.getJsonValue(object, "personelAdi").trim());

                        list.add(entity);

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        loadPersonelData(list);
    }

    private void loadPersonelData(List<PersonelEntity> list) {
        if (list != null && list.size() > 0) {
            rcy_appointment.setLayoutManager(new LinearLayoutManager(nActivity));
            rcy_appointment.setHasFixedSize(true);
            PersonelAdapter adapter = new PersonelAdapter(nActivity, list,serviceId,serviceName,serviceDuration);
            rcy_appointment.setAdapter(adapter);
        } else {
            //liste boş
        }
        progress.setVisibility(View.GONE);
    }


    private void defineComponents(View view) {
        rcy_appointment = view.findViewById(R.id.appointment_rcy);
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
}
