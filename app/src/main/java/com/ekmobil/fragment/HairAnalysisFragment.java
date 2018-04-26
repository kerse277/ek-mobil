package com.ekmobil.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.ekmobil.R;
import com.ekmobil.UI.NonSwipeableViewPager;
import com.ekmobil.adapter.HairAnalysisAdapter;
import com.ekmobil.utility.Constants;
import com.ekmobil.utility.ProjectUtility;

import okhttp3.HttpUrl;

/**
 * A simple {@link Fragment} subclass.
 */
public class HairAnalysisFragment extends Fragment implements View.OnClickListener {
    private Activity nActivity;
    private View progress;

    private RadioGroup rg1a, rg2a, rg2b, rg3a, rg3b, rg3c, rg4a, rg5a, rg5b, rg6a, rg7a, rg7b, rg7c, rg7d, rg7e, rg7f;
    private Button frw1, frw2, frw3, frw4, frw5, frw6, frw7;
    private Button bck2, bck3, bck4, bck5, bck6, bck7;
    private NonSwipeableViewPager viewPager;

    public HairAnalysisFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hair_analysis, container, false);
        defineComponents(view);
        return view;
    }

    private void defineComponents(View view) {
        viewPager = view.findViewById(R.id.hair_analysis_pager);
        progress = view.findViewById(R.id.progress);
        rg1a = view.findViewById(R.id.analysis_group_1);
        rg2a = view.findViewById(R.id.analysis_group_2A);
        rg2b = view.findViewById(R.id.analysis_group_2B);
        rg3a = view.findViewById(R.id.analysis_group_3A);
        rg3b = view.findViewById(R.id.analysis_group_3B);
        rg3c = view.findViewById(R.id.analysis_group_3C);
        rg4a = view.findViewById(R.id.analysis_group_4A);
        rg5a = view.findViewById(R.id.analysis_group_5A);
        rg5b = view.findViewById(R.id.analysis_group_5B);
        rg6a = view.findViewById(R.id.analysis_group_6A);
        rg7a = view.findViewById(R.id.analysis_group_7A);
        rg7b = view.findViewById(R.id.analysis_group_7B);
        rg7c = view.findViewById(R.id.analysis_group_7C);
        rg7d = view.findViewById(R.id.analysis_group_7D);
        rg7e = view.findViewById(R.id.analysis_group_7E);
        rg7f = view.findViewById(R.id.analysis_group_7F);

        frw1 = view.findViewById(R.id.analysis_forward_btn_1);
        frw2 = view.findViewById(R.id.analysis_forward_btn_2);
        frw3 = view.findViewById(R.id.analysis_forward_btn_3);
        frw4 = view.findViewById(R.id.analysis_forward_btn_4);
        frw5 = view.findViewById(R.id.analysis_forward_btn_5);
        frw6 = view.findViewById(R.id.analysis_forward_btn_6);
        frw7 = view.findViewById(R.id.analysis_forward_btn_7);

        bck2 = view.findViewById(R.id.analysis_back_btn_2);
        bck3 = view.findViewById(R.id.analysis_back_btn_3);
        bck4 = view.findViewById(R.id.analysis_back_btn_4);
        bck5 = view.findViewById(R.id.analysis_back_btn_5);
        bck6 = view.findViewById(R.id.analysis_back_btn_6);
        bck7 = view.findViewById(R.id.analysis_back_btn_7);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        modifyUIElements();
    }

    private void modifyUIElements() {
        viewPager.setOffscreenPageLimit(7);
        HairAnalysisAdapter adapter = new HairAnalysisAdapter(nActivity);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);

        frw1.setOnClickListener(this);
        frw2.setOnClickListener(this);
        frw3.setOnClickListener(this);
        frw4.setOnClickListener(this);
        frw5.setOnClickListener(this);
        frw6.setOnClickListener(this);
        frw7.setOnClickListener(this);

        bck2.setOnClickListener(this);
        bck3.setOnClickListener(this);
        bck4.setOnClickListener(this);
        bck5.setOnClickListener(this);
        bck6.setOnClickListener(this);
        bck7.setOnClickListener(this);
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

    public void showEmptyMessage() {
        ProjectUtility.showDialog(nActivity, getString(R.string.messages_info), getString(R.string.empty_field), getString(R.string.messages_ok), null, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.analysis_forward_btn_1:
                if (rg1a.getCheckedRadioButtonId() != -1) {
                    viewPager.setCurrentItem(1, true);
                } else {
                    showEmptyMessage();
                }
                break;
            case R.id.analysis_forward_btn_2:
                if (rg2a.getCheckedRadioButtonId() != -1 && rg2b.getCheckedRadioButtonId() != -1) {
                    viewPager.setCurrentItem(2, true);
                } else {
                    showEmptyMessage();
                }
                break;
            case R.id.analysis_forward_btn_3:
                if (rg3a.getCheckedRadioButtonId() != -1 && rg3b.getCheckedRadioButtonId() != -1 && rg3c.getCheckedRadioButtonId() != -1) {
                    viewPager.setCurrentItem(3, true);
                } else {
                    showEmptyMessage();
                }
                break;
            case R.id.analysis_forward_btn_4:
                if (rg4a.getCheckedRadioButtonId() != -1) {
                    viewPager.setCurrentItem(4, true);
                } else {
                    showEmptyMessage();
                }
                break;
            case R.id.analysis_forward_btn_5:
                if (rg5a.getCheckedRadioButtonId() != -1 && rg5b.getCheckedRadioButtonId() != -1) {
                    viewPager.setCurrentItem(5, true);
                } else {
                    showEmptyMessage();
                }
                break;
            case R.id.analysis_forward_btn_6:
                if (rg6a.getCheckedRadioButtonId() != -1) {
                    viewPager.setCurrentItem(6, true);
                } else {
                    showEmptyMessage();
                }
                break;
            case R.id.analysis_forward_btn_7:
                if (rg7a.getCheckedRadioButtonId() != -1 && rg7b.getCheckedRadioButtonId() != -1 && rg7c.getCheckedRadioButtonId() != -1 && rg7d.getCheckedRadioButtonId() != -1 && rg7e.getCheckedRadioButtonId() != -1 && rg7f.getCheckedRadioButtonId() != -1) {
                    sendChoices();
                } else {
                    showEmptyMessage();
                }
                break;
            case R.id.analysis_back_btn_2:
                viewPager.setCurrentItem(0, true);
                break;
            case R.id.analysis_back_btn_3:
                viewPager.setCurrentItem(1, true);
                break;
            case R.id.analysis_back_btn_4:
                viewPager.setCurrentItem(2, true);
                break;
            case R.id.analysis_back_btn_5:
                viewPager.setCurrentItem(3, true);
                break;
            case R.id.analysis_back_btn_6:
                viewPager.setCurrentItem(4, true);
                break;
            case R.id.analysis_back_btn_7:
                viewPager.setCurrentItem(5, true);
                break;
        }
    }

    private void sendChoices() {
        //Data Gönderilecek
        String param1A = rg1a.findViewById(rg1a.getCheckedRadioButtonId()).getTag().toString();
        String param2A = rg2a.findViewById(rg2a.getCheckedRadioButtonId()).getTag().toString();
        String param2B = rg2b.findViewById(rg2b.getCheckedRadioButtonId()).getTag().toString();
        String param3A = rg3a.findViewById(rg3a.getCheckedRadioButtonId()).getTag().toString();
        String param3B = rg3b.findViewById(rg3b.getCheckedRadioButtonId()).getTag().toString();
        String param3C = rg3c.findViewById(rg3c.getCheckedRadioButtonId()).getTag().toString();
        String param4A = rg4a.findViewById(rg4a.getCheckedRadioButtonId()).getTag().toString();
        String param5A = rg5a.findViewById(rg5a.getCheckedRadioButtonId()).getTag().toString();
        String param5B = rg5b.findViewById(rg5b.getCheckedRadioButtonId()).getTag().toString();
        String param6A = rg6a.findViewById(rg6a.getCheckedRadioButtonId()).getTag().toString();

        String param7A = rg7a.findViewById(rg7a.getCheckedRadioButtonId()).getTag().toString();
        String param7B = rg7b.findViewById(rg7b.getCheckedRadioButtonId()).getTag().toString();
        String param7C = rg7c.findViewById(rg7c.getCheckedRadioButtonId()).getTag().toString();
        String param7D = rg7d.findViewById(rg7d.getCheckedRadioButtonId()).getTag().toString();
        String param7E = rg7e.findViewById(rg7e.getCheckedRadioButtonId()).getTag().toString();
        String param7F = rg7f.findViewById(rg7f.getCheckedRadioButtonId()).getTag().toString();

        HttpUrl url = HttpUrl.parse(Constants.URL_HAIR_ANALYSIS).newBuilder()
                .addQueryParameter("cevap1", param1A)
                .addQueryParameter("cevap2A", param2A)
                .addQueryParameter("cevap2B", param2B)
                .addQueryParameter("cevap3A", param3A)
                .addQueryParameter("cevap3B", param3B)
                .addQueryParameter("cevap3C", param3C)
                .addQueryParameter("cevap4A", param4A)
                .addQueryParameter("cevap5A", param5A)
                .addQueryParameter("cevap5B", param5B)
                .addQueryParameter("cevap6A", param6A)
                .addQueryParameter("cevap7A", param7A)
                .addQueryParameter("cevap7B", param7B)
                .addQueryParameter("cevap7C", param7C)
                .addQueryParameter("cevap7D", param7D)
                .addQueryParameter("cevap7E", param7E)
                .addQueryParameter("cevap7F", param7F)
                .build();

        HairAnalysisDetailFragment fragment = new HairAnalysisDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_HAIR_ANALYSIS_URL, url.toString());
        fragment.setArguments(bundle);

        getFragmentManager().beginTransaction()
                .replace(R.id.main_fragment_layout, fragment)
                .addToBackStack(null)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commitAllowingStateLoss();

    }
}
