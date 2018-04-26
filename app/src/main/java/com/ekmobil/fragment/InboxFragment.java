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
import com.ekmobil.adapter.InboxAdapter;
import com.ekmobil.entity.InboxEntity;
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
public class InboxFragment extends Fragment {
    private Activity nActivity;
    private View progress;
    private RecyclerView rcy_inbox;

    public InboxFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);
        defineComponents(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getInboxData();
    }

    private void getInboxData() {
        progress.setVisibility(View.VISIBLE);
        if (HttpUtility.isOnline(nActivity)) {
            String id = nActivity.getSharedPreferences(Constants.PREFERENCE_USER_INFO, Context.MODE_PRIVATE).getString(Constants.PREF_ID, "");

            HttpUrl url = HttpUrl.parse(Constants.URL_INBOX).newBuilder()
                    .addQueryParameter("id", id).build();

            HttpUtility.get(url.toString(), new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    nActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAdded()) {
                                progress.setVisibility(View.GONE);
                                ProjectUtility.showDialog(nActivity, getString(R.string.messages_error), e.getLocalizedMessage(), getString(R.string.messages_ok), null, null, null);
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
                                parseInboxData(res);
                            }
                        }
                    });
                }
            });
        } else {
            progress.setVisibility(View.GONE);
            ProjectUtility.showDialog(nActivity, getString(R.string.connection_error_title), getString(R.string.connection_error_message), getString(R.string.messages_ok), null, null, null);
        }
    }

    private void parseInboxData(String res) {
        List<InboxEntity> list = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(res);
            if (array != null && array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object = array.getJSONObject(i);
                    InboxEntity entity = new InboxEntity();

                    entity.setMessageId(ProjectUtility.getJsonValue(object, "id").trim());
                    entity.setSenderId(ProjectUtility.getJsonValue(object, "gonderenid").trim());
                    entity.setSenderName(ProjectUtility.getJsonValue(object, "gonderenad").trim());
                    entity.setSubject(ProjectUtility.getJsonValue(object, "konu").trim());
                    entity.setContent(ProjectUtility.getJsonValue(object, "icerik").trim());
                    entity.setDate(ProjectUtility.getJsonValue(object, "gonderitarihi").trim());

                    list.add(entity);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        loadInboxData(list);
    }

    private void loadInboxData(List<InboxEntity> list) {
        if (list != null && list.size() > 0) {
            rcy_inbox.setLayoutManager(new LinearLayoutManager(nActivity));
            InboxAdapter adapter = new InboxAdapter(nActivity, list);
            rcy_inbox.setAdapter(adapter);
        } else {
            //liste boş
        }
        progress.setVisibility(View.GONE);
    }

    private void defineComponents(View view) {
        progress = view.findViewById(R.id.progress);
        rcy_inbox = view.findViewById(R.id.rcy_inbox);
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
