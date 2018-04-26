package com.ekmobil.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.ekmobil.CommentActivity;
import com.ekmobil.R;
import com.ekmobil.utility.Constants;
import com.ekmobil.utility.GlideApp;
import com.ekmobil.utility.HttpUtility;
import com.ekmobil.utility.ProjectUtility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainPageDetailFragment extends Fragment implements View.OnClickListener {
    private Activity nActivity;
    private View progress;
    private ImageButton imb_favorite, imb_comment;
    private ImageView img_main_page_detail;
    private String imageId;

    public MainPageDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_page_detail, container, false);
        defineComponents(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        modifyUIElements();
    }

    private void modifyUIElements() {
        imb_comment.setOnClickListener(this);
        imb_favorite.setOnClickListener(this);

        if (getArguments() != null && getArguments().getString(Constants.BUNDLE_IMAGE_URL) != null) {
            imageId = getArguments().getString(Constants.BUNDLE_IMAGE_ID);
            GlideApp.with(nActivity).load(getArguments().getString(Constants.BUNDLE_IMAGE_URL))
                    .placeholder(R.drawable.ic_logo)
                    .error(R.drawable.ic_logo)
                    .centerCrop().into(img_main_page_detail);
        }
    }

    private void defineComponents(View view) {
        progress = view.findViewById(R.id.progress);
        imb_favorite = view.findViewById(R.id.main_page_detail_imb_add_favorite);
        imb_comment = view.findViewById(R.id.main_page_detail_imb_add_comment);

        img_main_page_detail = view.findViewById(R.id.main_page_detail_img);
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
            case R.id.main_page_detail_imb_add_favorite:
                imb_favorite.setImageResource(R.drawable.ic_favorite_red);
                addFavorites();
                break;
            case R.id.main_page_detail_imb_add_comment:
                if (getArguments() != null && getArguments().getString(Constants.BUNDLE_IMAGE_ID) != null) {
                    Intent intent = new Intent(nActivity, CommentActivity.class);

                    intent.putExtra(Constants.INTENT_IMAGE_ID, getArguments().getString(Constants.BUNDLE_IMAGE_ID));
                    startActivity(intent);
                }
                break;
        }
    }

    private void addFavorites() {
        if (HttpUtility.isOnline(nActivity)) {
            SharedPreferences preferences = nActivity.getSharedPreferences(Constants.PREFERENCE_USER_INFO, Context.MODE_PRIVATE);
            String costumerId = preferences.getString(Constants.PREF_ID, "");
            HttpUrl url = HttpUrl.parse(Constants.URL_ADD_FAVORITE).newBuilder()
                    .addEncodedQueryParameter("musteriid", costumerId)
                    .addEncodedQueryParameter("kesimresimid", imageId).build();

            HttpUtility.get(url.toString(), new Callback() {
                @Override
                public void onFailure(Call call, final IOException e) {
                    nActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (isAdded()) {
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
                                if (res.contains("TRUE")) {
                                    Toast.makeText(nActivity, getString(R.string.add_favorite_success), Toast.LENGTH_SHORT).show();
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
