package com.ekmobil.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ekmobil.R;
import com.ekmobil.entity.PersonelEntity;
import com.ekmobil.fragment.AppointmentDetailFragment;
import com.ekmobil.fragment.CameraFragment;
import com.ekmobil.utility.Constants;
import com.ekmobil.utility.GlideApp;

import java.util.List;

public class PersonelChoicesAdapter extends RecyclerView.Adapter<PersonelChoicesAdapter.PersonelChoicesHolder> {
    private Context context;
    private List<PersonelEntity> list;
    private Dialog dialog;
    private CameraFragment cameraFragment;

    public PersonelChoicesAdapter(Context context, List<PersonelEntity> list, Dialog dialog, CameraFragment cameraFragment) {
        this.context = context;
        this.list = list;
        this.dialog = dialog;
        this.cameraFragment = cameraFragment;
    }

    @Override
    public PersonelChoicesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_personel, parent, false);
        return new PersonelChoicesHolder(view);
    }

    @Override
    public void onBindViewHolder(final PersonelChoicesAdapter.PersonelChoicesHolder holder, int position) {
        final PersonelEntity entity = list.get(position);

        GlideApp.with(context)
                .load(entity.getPersonelImage())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(holder.img_personel);

        holder.txt_personel.setText(entity.getPersonelNameSurname());
        holder.imb_personel.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                cameraFragment.postImage(entity.getId());
            }
        });
    }

    private void goToDetail(String id, String name) {
        AppointmentDetailFragment fragment = new AppointmentDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_APPOINTMENT_PERSONAL_ID, id);
        bundle.putString(Constants.BUNDLE_APPOINTMENT_PERSONAL_NAME, name);
        fragment.setArguments(bundle);

        ((Activity) context).getFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.main_fragment_layout, fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commitAllowingStateLoss();
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class PersonelChoicesHolder extends RecyclerView.ViewHolder {
        ImageView img_personel;
        TextView txt_personel;
        ImageButton imb_personel;

        public PersonelChoicesHolder(View itemView) {
            super(itemView);
            txt_personel = itemView.findViewById(R.id.cell_personel_txt);
            img_personel = itemView.findViewById(R.id.cell_personel_img);
            imb_personel = itemView.findViewById(R.id.cell_personel_imb_popup);
        }
    }
}