package com.ekmobil.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ekmobil.R;
import com.ekmobil.utility.Constants;
import com.ekmobil.utility.HttpUtility;
import com.ekmobil.utility.ProjectUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddressFragment extends Fragment {
    private Activity nActivity;
    private WebView wv_address;
    private View progress;

    public AddressFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_address, container, false);
        defineComponents(view);

        return view;
    }


    private void defineComponents(View view) {
        wv_address = view.findViewById(R.id.address_webview);
        progress = view.findViewById(R.id.progress);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getUrl();

    }

    private void getUrl() {
        if (HttpUtility.isOnline(nActivity)) {
            HttpUrl url = HttpUrl.parse(Constants.URL_FIRMA).newBuilder().build();

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
                                parseURL(res);
                            }
                        }
                    });
                }
            });
        } else {

        }
    }

    private void parseURL(String res) {
        if (res != null) {
            try {
                JSONArray array = new JSONArray(res);
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        String firmaId = ProjectUtility.getJsonString(object, "ID").trim();
                        if (firmaId.equals(Constants.PARAM_FIRMA_ID)) {
                            String url = ProjectUtility.getJsonValue(object, "mobilMap").trim();
                            loadUrl(url);
                            break;
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadUrl(String url) {
        if (HttpUtility.isOnline(nActivity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                wv_address.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
            } else {
                wv_address.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            }
            wv_address.clearCache(true);
            wv_address.getSettings().setBuiltInZoomControls(true);
            wv_address.getSettings().setLoadWithOverviewMode(true);
            wv_address.getSettings().setUseWideViewPort(true);
            wv_address.loadUrl(url);
            wv_address.getSettings().setJavaScriptEnabled(true);

            wv_address.setWebViewClient(new WebViewClient() {

                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(nActivity, description, Toast.LENGTH_SHORT).show();
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.endsWith(".pdf") || url.endsWith(".doc") || url.endsWith(".docx") || url.endsWith(".xls") || url.endsWith(".xlsx")) {
                        wv_address.loadUrl("https://docs.google.com/viewer?url=" + url);
                    } else {
                        wv_address.loadUrl(url);
                    }
                    return true;

                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    progress.setVisibility(View.VISIBLE);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    progress.setVisibility(View.GONE);
                }

            });
        } else {
            ProjectUtility.showDialog(nActivity, getString(R.string.messages_error), getString(R.string.connection_error_message), getString(R.string.messages_ok), null, null, null);
        }
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
