package com.ekmobil.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ekmobil.R;
import com.ekmobil.adapter.PriceListAdapter;
import com.ekmobil.entity.ProductEntity;
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
public class OnlineShopDetailFragment extends Fragment {
    private Activity nActivity;
    private View progress;
    private RecyclerView rcy_online_shop_detail;
    private TextView txt_title;

    public OnlineShopDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_online_shop_detail, container, false);
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
            if (getArguments() != null && getArguments().getString(Constants.BUNDLE_ONLINE_SHOP_PRODUCT_GROUP) != null) {
                String productGroup = getArguments().getString(Constants.BUNDLE_ONLINE_SHOP_PRODUCT_GROUP);
                txt_title.setText(productGroup);
                HttpUrl url = HttpUrl.parse(Constants.URL_PRODUCT_DETAIL).newBuilder()
                        .addEncodedQueryParameter("kategoriadi", productGroup).build();
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

            }
        } else {
            ProjectUtility.showDialog(nActivity, getString(R.string.connection_error_title), getString(R.string.connection_error_message), getString(R.string.messages_ok), null, null, null);

        }
    }

    private void parseData(String res) {
        List<ProductEntity> list = new ArrayList<>();
        if (res != null && res.length() > 0) {
            try {
                JSONArray array = new JSONArray(res);
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        ProductEntity entity = new ProductEntity();
                        entity.setId(ProjectUtility.getJsonValue(object, "id"));
                        entity.setProductName(ProjectUtility.getJsonValue(object, "urunAdi"));
                        entity.setProductPrice(ProjectUtility.getJsonValue(object, "urunFiyati"));

                        entity.setProductImage(ProjectUtility.getJsonValue(object, "resimURL"));
                        entity.setProductUrl(ProjectUtility.getJsonValue(object, "urunURL"));
                        entity.setProductLike(ProjectUtility.getJsonValue(object, "urunbegeni"));

                        list.add(entity);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        loadData(list);
    }

    private void loadData(List<ProductEntity> list) {
        if (list != null && list.size() > 0) {
            rcy_online_shop_detail.setLayoutManager(new GridLayoutManager(nActivity, 2));
            rcy_online_shop_detail.setHasFixedSize(true);
            PriceListAdapter adapter = new PriceListAdapter(nActivity, list);
            rcy_online_shop_detail.setAdapter(adapter);
        } else {

        }
        progress.setVisibility(View.GONE);
    }

    private void defineComponents(View view) {
        progress = view.findViewById(R.id.progress);
        rcy_online_shop_detail = view.findViewById(R.id.online_shop_detail_rcy);
        txt_title = view.findViewById(R.id.online_shop_detail_title);
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
