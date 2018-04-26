package com.ekmobil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.ekmobil.adapter.ClockChoicesAdapter;
import com.ekmobil.utility.Constants;
import com.ekmobil.utility.HttpUtility;
import com.ekmobil.utility.ProjectUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

public class ClockChoicesActivity extends BaseActivity {
    private Toolbar toolbar;
    private String date = "", personalId = "";
    private View progress;
    private RecyclerView rcy_clock_choice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock_choices);

        defineComponents();

        getData();
    }

    private void getData() {
        if (HttpUtility.isOnline(this)) {
            if (date != null && date.length() > 0) {
                progress.setVisibility(View.VISIBLE);

                HttpUrl url = HttpUrl.parse(Constants.URL_EMPTY_APPOINTMENT).newBuilder()
                        .addEncodedQueryParameter("personelID", personalId)
                        .addEncodedQueryParameter("ilkTarih", date)
                        .addEncodedQueryParameter("hizmetSuresi", Constants.DURATION_OF_SERVICE)
                        .build();

                HttpUtility.get(url.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setVisibility(View.GONE);
                                ProjectUtility.showDialog(ClockChoicesActivity.this, getString(R.string.connection_error_title), e.getLocalizedMessage(), getString(R.string.messages_ok), null, null, null);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String res = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                parseData(res);
                            }
                        });
                    }
                });
            }
        } else {
            ProjectUtility.showDialog(this, getString(R.string.connection_error_title), getString(R.string.connection_error_message), getString(R.string.messages_ok), null, null, null);
        }
    }

    private void parseData(String res) {
        HashMap<String, List<String>> hashMap = new HashMap<>();
        for (int i = 7; i < 24; i++) {
            String clock = "";
            if (i < 10)
                clock = "0" + i;
            else
                clock = String.valueOf(i);
            hashMap.put(clock, new ArrayList<String>());
        }
        if (res != null) {
            try {
                JSONArray array = new JSONArray(res);
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String value = ProjectUtility.getJsonValue(object, "saat").trim();
                        hashMap.get(value.substring(0, 2)).add(value);

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        loadData(hashMap);
    }



    private void loadData(HashMap<String, List<String>> hashMap) {
        if (hashMap != null && hashMap.size() > 0) {
            rcy_clock_choice.setHasFixedSize(true);
            rcy_clock_choice.setLayoutManager(new GridLayoutManager(this, 5));
            ClockChoicesAdapter adapter = new ClockChoicesAdapter(this, hashMap,this);
            rcy_clock_choice.setAdapter(adapter);
        } else {

        }
        progress.setVisibility(View.GONE);
    }

    private void defineComponents() {
        toolbar = findViewById(R.id.clock_choices_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rcy_clock_choice = findViewById(R.id.clock_choices_rcy);
        progress = findViewById(R.id.progress);
        TextView txt_date = findViewById(R.id.clock_choices_date);
        TextView txt_personal_name = findViewById(R.id.clock_choices_personel);
        if (getIntent() != null) {
            date = getIntent().getStringExtra(Constants.INTENT_CHOICES_DATE);
            personalId = getIntent().getStringExtra(Constants.INTENT_CHOICES_ID);
            txt_date.setText(date);
            txt_personal_name.setText(getIntent().getStringExtra(Constants.INTENT_CHOICES_NAME));
        }
    }
}
