package com.ekmobil.fragment;


import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.ekmobil.R;
import com.ekmobil.entity.TeamEntity;
import com.ekmobil.utility.Constants;
import com.ekmobil.utility.GlideApp;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonelInfoFragment extends Fragment {
    private Activity nActivity;
    private EditText edt_name_surname, edt_username, edt_password, edt_password_again, edt_mail, edt_phone;
    private View progress;
    private ImageView img_personel_info;

    public PersonelInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personel_info, container, false);
        defineComponents(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadData();
    }

    private void loadData() {
        TeamEntity entity = (TeamEntity) getArguments().getSerializable(Constants.BUNDLE_PERSONEL_INFO);
        GlideApp.with(nActivity)
                .load(entity.getImageUrl())
                .placeholder(R.drawable.unknown)
                .error(R.drawable.unknown)
                .into(img_personel_info);

        edt_username.setText(entity.getName());
        edt_mail.setText(entity.getEmail());
        edt_phone.setText(entity.getPhone());

        edt_password.setText(entity.getAddress());
        edt_password_again.setText(entity.getBio());
        edt_name_surname.setText(entity.getProfession());
    }

    private void defineComponents(View view) {
        progress = view.findViewById(R.id.progress);
        img_personel_info = view.findViewById(R.id.personel_info_imageView);
        edt_phone = view.findViewById(R.id.personel_info_edt_phone);
        edt_name_surname = view.findViewById(R.id.personel_info_edt_name_surname);
        edt_username = view.findViewById(R.id.personel_info_edt_username);
        edt_password = view.findViewById(R.id.personel_info_edt_password);
        edt_password_again = view.findViewById(R.id.personel_info_edt_password_again);
        edt_mail = view.findViewById(R.id.personel_info_edt_mail);
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
