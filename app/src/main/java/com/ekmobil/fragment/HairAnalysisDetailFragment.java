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
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HairAnalysisDetailFragment extends Fragment {
    private Activity nActivity;
    private View progress;
    private RecyclerView rcy_hair_analysis_detail;

    public HairAnalysisDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hair_analysis_detail, container, false);
        defineComponents(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        modifyUIElements();
        getData();
    }

    private void modifyUIElements() {
    }

    private void getData() {
        if (HttpUtility.isOnline(nActivity)) {
            progress.setVisibility(View.VISIBLE);
            if (getArguments() != null && getArguments().getString(Constants.BUNDLE_HAIR_ANALYSIS_URL) != null) {
                String url = getArguments().getString(Constants.BUNDLE_HAIR_ANALYSIS_URL);

                HttpUtility.get(url, new Callback() {
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
//                        final String res = "[{\"id\":1015,\"urunAdi\":\"Kerastase Resistance Therapiste [3-4] Yenileyici Serum 30ml\",\"urunFiyati\":\"79.99\",\"resimURL\":\"https://kozmetikmarkalari.com/1185-large_default/kerastase-resistance-therapiste-3-4-yenileyici-serum-30ml.jpg\",\"urunURL\":null,\"urunbegeni\":\"0\"},{\"id\":1013,\"urunAdi\":\"Milk Shake Sweet Camomile Sprey Krem 150ml\",\"urunFiyati\":\"79.99\",\"resimURL\":\"https://kozmetikmarkalari.com/1491-large_default/milk-shake-sweet-camomile-sprey-krem-150ml.jpg\",\"urunURL\":null,\"urunbegeni\":\"0\"},{\"id\":1013,\"urunAdi\":\"Milk Shake Sweet Camomile Sprey Krem 150ml\",\"urunFiyati\":\"79.99\",\"resimURL\":\"https://kozmetikmarkalari.com/1491-large_default/milk-shake-sweet-camomile-sprey-krem-150ml.jpg\",\"urunURL\":null,\"urunbegeni\":\"0\"}]";
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
            rcy_hair_analysis_detail.setLayoutManager(new GridLayoutManager(nActivity, 2));
            rcy_hair_analysis_detail.setHasFixedSize(true);
            PriceListAdapter adapter = new PriceListAdapter(nActivity, list);
            rcy_hair_analysis_detail.setAdapter(adapter);
        } else {
            ProjectUtility.showDialog(nActivity, getString(R.string.messages_info), getString(R.string.product_cannot_be_found), getString(R.string.messages_ok), null, null, null);
        }
        progress.setVisibility(View.GONE);
    }

    private void defineComponents(View view) {
        progress = view.findViewById(R.id.progress);
        rcy_hair_analysis_detail = view.findViewById(R.id.hair_analysis_detail_rcy);
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
