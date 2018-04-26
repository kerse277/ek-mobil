package com.ekmobil.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ekmobil.R;
import com.ekmobil.adapter.ImageAdapter;
import com.ekmobil.entity.ImageEntity;
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
public class FavoriteFragment extends Fragment {
    private Activity nActivity;
    private View progress;
    private RecyclerView rcy_favorites;

    public FavoriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        defineComponents(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getData();
    }

    private void getData() {
        if (HttpUtility.isOnline(nActivity)) {
            progress.setVisibility(View.VISIBLE);
            SharedPreferences preferences = nActivity.getSharedPreferences(Constants.PREFERENCE_USER_INFO, Context.MODE_PRIVATE);
            String costumerId = preferences.getString(Constants.PREF_ID, "");
            HttpUrl url = HttpUrl.parse(Constants.URL_GET_FAVORITE).newBuilder()
                    .addEncodedQueryParameter("musteriid", costumerId).build();

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
            ProjectUtility.showDialog(nActivity, getString(R.string.connection_error_title), getString(R.string.connection_error_message), getString(R.string.messages_ok), null, null, null);
        }
    }

    private void parseData(String res) {
        List<ImageEntity> list = new ArrayList<>();
        if (res != null) {
            try {
                JSONArray array = new JSONArray(res);
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        ImageEntity entity = new ImageEntity();
                        JSONObject object = array.getJSONObject(i);

                        entity.setId(ProjectUtility.getJsonValue(object, "ID").trim());
                        entity.setFirmaId(ProjectUtility.getJsonValue(object, "firmaid").trim());
                        entity.setCostumerId(ProjectUtility.getJsonValue(object, "musteriid").trim());
                        entity.setImageUrl(ProjectUtility.getJsonValue(object, "resimurl").trim());
                        entity.setPersonelId(ProjectUtility.getJsonValue(object, "personelid").trim());
                        list.add(entity);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        loadData(list);
    }

    private void loadData(List<ImageEntity> list) {
        if (list != null && list.size() > 0) {
            rcy_favorites.setHasFixedSize(true);
            rcy_favorites.setLayoutManager(new GridLayoutManager(nActivity, 3));
            ImageAdapter adapter = new ImageAdapter(nActivity, list);
            rcy_favorites.setAdapter(adapter);
        } else {

        }
        progress.setVisibility(View.GONE);
    }

    private void defineComponents(View view) {
        rcy_favorites = view.findViewById(R.id.favorite_rcy);
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
