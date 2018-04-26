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

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.ekmobil.R;
import com.ekmobil.adapter.OnlineShopAdapter;
import com.ekmobil.entity.BannerEntity;
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
public class OnlineShopFragment extends Fragment {
    private Activity nActivity;
    private View progress;
    private RecyclerView rcy_online_shop;
    private SliderLayout sld_online;
    private List<String> productList = new ArrayList<>();
    private List<BannerEntity> bannerList = new ArrayList<>();

    public OnlineShopFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_online_shop, container, false);
        defineComponents(view);
        return view;
    }

    private void defineComponents(View view) {
        progress = view.findViewById(R.id.progress);
        rcy_online_shop = view.findViewById(R.id.online_shop_rcy);
        sld_online = view.findViewById(R.id.online_shop_slider);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        modifyUIElements();
        if (productList != null && productList.size() > 0)
            loadData();
        else
            getData();

        if (bannerList != null && bannerList.size() > 0)
            loadBannerList();
        else
            getBannerData();
    }

    private void loadBannerList() {
        if (bannerList != null && bannerList.size() > 0) {
            for (final BannerEntity banner : bannerList) {
                TextSliderView textSliderView = new TextSliderView(nActivity);
                textSliderView.description(banner.getDescription())
                        .image(banner.getImageUrl())
                        .empty(R.drawable.ic_logo)
                        .error(R.drawable.ic_logo)
                        .setScaleType(BaseSliderView.ScaleType.Fit);

                sld_online.addSlider(textSliderView);
            }
//            sld_announcement.setCurrentPosition(0);
//            sld_online.setCurrentPosition(slidingList.size() - 1, false);
//            sld_online.moveNextPosition();
        }
    }

    private void getBannerData() {
        if (HttpUtility.isOnline(nActivity)) {
//            progress.setVisibility(View.VISIBLE);
            HttpUrl url = HttpUrl.parse(Constants.URL_BANNER).newBuilder()
                    .addEncodedQueryParameter("firmaID", Constants.PARAM_FIRMA_ID).build();

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
                                parseBannerData(res);
                            }
                        }
                    });
                }
            });
        }
    }

    private void parseBannerData(String res) {
        if (res != null) {
            try {
                JSONArray array = new JSONArray(res);
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        BannerEntity entity = new BannerEntity();
                        JSONObject object = array.getJSONObject(i);
                        entity.setImageUrl(ProjectUtility.getJsonValue(object, "imageUrl").trim());
                        entity.setDescription(ProjectUtility.getJsonValue(object, "kapmanya").trim());
                        bannerList.add(entity);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        loadBannerList();
    }

    private void modifyUIElements() {
        int screenHeight = ProjectUtility.getScreenSizes(nActivity).y;
        int screenWidth = ProjectUtility.getScreenSizes(nActivity).x;

        sld_online.getLayoutParams().height = screenHeight / 4;
        sld_online.getLayoutParams().width = screenWidth * 7 / 8;
        sld_online.setPresetTransformer(SliderLayout.Transformer.Tablet);
        sld_online.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sld_online.setCustomAnimation(new DescriptionAnimation());
        sld_online.setDuration(6000);
    }

    private void getData() {
        if (HttpUtility.isOnline(nActivity)) {
            progress.setVisibility(View.VISIBLE);
            HttpUrl url = HttpUrl.parse(Constants.URL_PRODUCT_GRUP_LIST).newBuilder().build();

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
        if (res != null) {
            try {
                JSONArray array = new JSONArray(res);
                if (array != null && array.length() > 0) {
                    for (int i = 0; i < array.length(); i++) {
                        String product = array.get(i).toString();
                        if (product != null && !product.equals("null"))
                            productList.add(product);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        loadData();
    }

    private void loadData() {
        if (productList != null && productList.size() > 0) {
            rcy_online_shop.setHasFixedSize(true);
            rcy_online_shop.setLayoutManager(new GridLayoutManager(nActivity, 2));
            OnlineShopAdapter adapter = new OnlineShopAdapter(nActivity, productList);
            rcy_online_shop.setAdapter(adapter);
        } else {

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
