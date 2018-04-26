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
import com.ekmobil.adapter.AllPersonelAdapter;
import com.ekmobil.entity.TeamEntity;
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
public class PersonelFragment extends Fragment {
    private Activity nActivity;
    private View progress;
    private RecyclerView rcy_personel;

    public PersonelFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personel, container, false);
        defineComponents(view);
        return view;
    }

    private void defineComponents(View view) {
        progress = view.findViewById(R.id.progress);
        rcy_personel = view.findViewById(R.id.rcy_personel);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();
    }

    private void getData() {
        if (HttpUtility.isOnline(nActivity)) {
            HttpUrl url = HttpUrl.parse(Constants.URL_ALL_PERSONEL).newBuilder()
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
                                parseData(res);
                            }
                        }
                    });
                }
            });
        } else {
            ProjectUtility.showDialog(nActivity, getString(R.string.messages_error), getString(R.string.connection_error_message), getString(R.string.messages_ok), null, null, null);
        }
    }

    private void parseData(String res) {
        List<TeamEntity> list = new ArrayList<>();
        if (res != null && res.length() > 0) {
            try {
                JSONArray array = new JSONArray(res);
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        TeamEntity entity = new TeamEntity();
                        entity.setId(ProjectUtility.getJsonValue(object, "ID").trim());
                        entity.setName(ProjectUtility.getJsonValue(object,"adSoyad").trim());
                        entity.setImageUrl(ProjectUtility.getJsonValue(object,"resim").trim());
                        entity.setPhone(ProjectUtility.getJsonValue(object,"telefon").trim());
                        entity.setEmail(ProjectUtility.getJsonValue(object,"email").trim());
                        entity.setAddress(ProjectUtility.getJsonValue(object,"adres").trim());
                        entity.setBio(ProjectUtility.getJsonValue(object,"biyografi").trim());
                        entity.setProfession(ProjectUtility.getJsonValue(object,"uzmanlik").trim());

                        list.add(entity);

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        loadData(list);
    }

    private void loadData(List<TeamEntity> list) {
        if (list != null && list.size() > 0) {
            rcy_personel.setLayoutManager(new LinearLayoutManager(nActivity));
            rcy_personel.setHasFixedSize(true);
            AllPersonelAdapter adapter = new AllPersonelAdapter(nActivity, list);
            rcy_personel.setAdapter(adapter);
        }
        progress.setVisibility(View.GONE);
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
