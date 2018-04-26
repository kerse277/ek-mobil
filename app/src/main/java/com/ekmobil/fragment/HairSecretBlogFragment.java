package com.ekmobil.fragment;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.ekmobil.R;
import com.ekmobil.utility.HttpUtility;
import com.ekmobil.utility.ProjectUtility;

/**
 * A simple {@link Fragment} subclass.
 */
public class HairSecretBlogFragment extends Fragment {
    private Activity nActivity;
    private WebView wv_blog;
    private View progress;

    public HairSecretBlogFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hair_secret_blog, container, false);
        defineComponents(view);
        return view;
    }

    private void defineComponents(View view) {
        progress = view.findViewById(R.id.progress);
        wv_blog = view.findViewById(R.id.blog_webview);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        loadUrl();
    }

    private void loadUrl() {
        if (HttpUtility.isOnline(nActivity)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                wv_blog.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
            } else {
                wv_blog.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
            }
            wv_blog.clearCache(true);
            wv_blog.getSettings().setBuiltInZoomControls(true);
            wv_blog.getSettings().setLoadWithOverviewMode(true);
            wv_blog.getSettings().setUseWideViewPort(true);
            wv_blog.loadUrl("http://hepies.com/");
            wv_blog.getSettings().setJavaScriptEnabled(true);

            wv_blog.setWebViewClient(new WebViewClient() {

                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    Toast.makeText(nActivity, description, Toast.LENGTH_SHORT).show();
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.endsWith(".pdf") || url.endsWith(".doc") || url.endsWith(".docx") || url.endsWith(".xls") || url.endsWith(".xlsx")) {
                        wv_blog.loadUrl("https://docs.google.com/viewer?url=" + url);
                    } else {
                        wv_blog.loadUrl(url);
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
