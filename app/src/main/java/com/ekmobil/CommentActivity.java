package com.ekmobil;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ekmobil.adapter.CommentAdapter;
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

public class CommentActivity extends BaseActivity implements View.OnClickListener {
    private RecyclerView rcy_comment;
    private Button btn_comment;
    private EditText edt_comment;
    private View progress;
    private String imageId = "";
    private ImageButton imb_back;
    private List<String> list = new ArrayList<>();
    private CommentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        defineComponents();

        getComments();
    }


    private void getComments() {
        if (HttpUtility.isOnline(this)) {
            if (getIntent() != null && getIntent().hasExtra(Constants.INTENT_IMAGE_ID)) {
                progress.setVisibility(View.VISIBLE);
                imageId = getIntent().getStringExtra(Constants.INTENT_IMAGE_ID);
                HttpUrl url = HttpUrl.parse(Constants.URL_COMMENTS_LIST).newBuilder()
                        .addEncodedQueryParameter("kesimresimid", getIntent().getStringExtra(Constants.INTENT_IMAGE_ID)).build();

                HttpUtility.get(url.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setVisibility(View.GONE);
                                ProjectUtility.showDialog(CommentActivity.this, getString(R.string.connection_error_title), e.getLocalizedMessage(), getString(R.string.messages_ok), null, null, null);
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
            ProjectUtility.showDialog(CommentActivity.this, getString(R.string.connection_error_title), getString(R.string.connection_error_message), getString(R.string.messages_ok), null, null, null);
        }
    }

    private void parseData(String res) {
        if (res != null) {
            try {
                JSONArray array = new JSONArray(res);
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String comment = ProjectUtility.getJsonValue(object, "yorum").trim();
                        list.add(comment);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        loadData();
    }

    private void loadData() {
        if (list != null) {
            adapter = new CommentAdapter(list);
            rcy_comment.setHasFixedSize(true);
            rcy_comment.setLayoutManager(new LinearLayoutManager(this));
            rcy_comment.setAdapter(adapter);
        } else {

        }
        progress.setVisibility(View.GONE);
    }

    private void defineComponents() {
        rcy_comment = findViewById(R.id.rcy_comment);
        btn_comment = findViewById(R.id.comment_btn);
        edt_comment = findViewById(R.id.comment_edt);
        progress = findViewById(R.id.progress);
        imb_back = findViewById(R.id.comments_back);

        imb_back.setOnClickListener(this);
        btn_comment.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_btn:
                if (edt_comment.getText().toString().trim().length() > 0) {
                    postComment();
                } else {
                    ProjectUtility.showDialog(this, getString(R.string.messages_info), getString(R.string.messages_blank_field), getString(R.string.messages_ok), null, null, null);
                }
                break;
            case R.id.comments_back:
                finish();
                break;
        }
    }

    private void postComment() {
        if (HttpUtility.isOnline(this)) {
            if (getIntent() != null && getIntent().hasExtra(Constants.INTENT_IMAGE_ID)) {
                SharedPreferences preferences = getSharedPreferences(Constants.PREFERENCE_USER_INFO, MODE_PRIVATE);
                String costumerId = preferences.getString(Constants.PREF_ID, "");


                HttpUrl url = HttpUrl.parse(Constants.URL_ADD_COMMENT).newBuilder()
                        .addEncodedQueryParameter("kesimresimid", imageId)
                        .addEncodedQueryParameter("musteriid", costumerId)
                        .addEncodedQueryParameter("yorum", edt_comment.getText().toString().trim()).build();

                HttpUtility.get(url.toString(), new Callback() {
                    @Override
                    public void onFailure(Call call, final IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setVisibility(View.GONE);
                                ProjectUtility.showDialog(CommentActivity.this, getString(R.string.connection_error_title), e.getLocalizedMessage(), getString(R.string.messages_ok), null, null, null);
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String res = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (res.contains("TRUE")) {
                                    list.add(edt_comment.getText().toString().trim());
                                    if (adapter != null) {
                                        adapter.notifyDataSetChanged();
                                    }
                                    edt_comment.setText("");
                                }
                            }
                        });
                    }
                });
            }
        } else {
            ProjectUtility.showDialog(CommentActivity.this, getString(R.string.connection_error_title), getString(R.string.connection_error_message), getString(R.string.messages_ok), null, null, null);
        }
    }
}
