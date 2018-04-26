package com.ekmobil.adapter;

import android.app.Activity;
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
import com.ekmobil.entity.TeamEntity;
import com.ekmobil.fragment.PersonelInfoFragment;
import com.ekmobil.utility.Constants;
import com.ekmobil.utility.GlideApp;

import java.util.List;

public class AllPersonelAdapter extends RecyclerView.Adapter<AllPersonelAdapter.AllPersonelHolder> {

    private Context context;
    private List<TeamEntity> list;

    public AllPersonelAdapter(Context context, List<TeamEntity> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public AllPersonelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_personel, parent, false);
        return new AllPersonelHolder(view);
    }

    @Override
    public void onBindViewHolder(final AllPersonelHolder holder, int position) {
        final TeamEntity entity = list.get(position);

        GlideApp.with(context)
                .load(entity.getImageUrl())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(holder.img_personel);

        holder.txt_personel.setText(entity.getName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDetail(entity);
            }
        });

        holder.imb_personel.setVisibility(View.GONE);
    }

    private void goToDetail(TeamEntity entity) {
        PersonelInfoFragment fragment = new PersonelInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_PERSONEL_INFO, entity);
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

    public class AllPersonelHolder extends RecyclerView.ViewHolder {
        ImageView img_personel;
        TextView txt_personel;
        ImageButton imb_personel;

        public AllPersonelHolder(View itemView) {
            super(itemView);
            txt_personel = itemView.findViewById(R.id.cell_personel_txt);
            img_personel = itemView.findViewById(R.id.cell_personel_img);
            imb_personel = itemView.findViewById(R.id.cell_personel_imb_popup);
        }
    }
}
