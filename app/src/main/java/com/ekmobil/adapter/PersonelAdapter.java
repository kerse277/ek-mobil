package com.ekmobil.adapter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.ekmobil.R;
import com.ekmobil.entity.PersonelEntity;
import com.ekmobil.fragment.AppointmentDetailFragment;
import com.ekmobil.utility.Constants;
import com.ekmobil.utility.GlideApp;

import java.util.List;

/**
 * Created by melih on 26.03.2018.
 */

public class PersonelAdapter extends RecyclerView.Adapter<PersonelAdapter.PersonelHolder> {
    private Context context;
    private List<PersonelEntity> list;
    private String serviceId;
    private String serviceName;
    private String serviceDuration;

    public PersonelAdapter(Context context, List<PersonelEntity> list, String serviceId, String serviceName, String serviceDuration) {
        this.context = context;
        this.list = list;
        this.serviceId = serviceId;
        this.serviceName = serviceName;
        this.serviceDuration = serviceDuration;
    }

    @Override
    public PersonelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_personel, parent, false);
        return new PersonelHolder(view);
    }

    @Override
    public void onBindViewHolder(final PersonelHolder holder, int position) {
        final PersonelEntity entity = list.get(position);

        GlideApp.with(context)
                .load(entity.getPersonelImage())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .into(holder.img_personel);

        holder.txt_personel.setText(entity.getPersonelNameSurname());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDetail(entity.getId(), entity.getPersonelNameSurname());
            }
        });

        holder.imb_personel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //creating a popup menu
                PopupMenu popup = new PopupMenu(context, holder.imb_personel);
                //inflating menu from xml resource
                popup.inflate(R.menu.personel_popup);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.personel_popup_make_an_appointment:
                                //handle menu1 click
                                goToDetail(entity.getId(), entity.getPersonelNameSurname());
                                break;
//                            case R.id.personel_popup_default_hairdresser:
//                                //handle menu2 click
//                                break;
                            case R.id.personel_popup_phone:
                                Uri call = Uri.parse("tel:" + entity.getPersonelPhone());
                                Intent surf = new Intent(Intent.ACTION_DIAL, call);
                                context.startActivity(surf);
                                //handle menu3 click
                                break;
                            case R.id.personel_popup_sms:
                                Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + entity.getPersonelPhone()));
                                context.startActivity(smsIntent);
                                //handle menu4 click
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }

    private void goToDetail(String id, String name) {
        AppointmentDetailFragment fragment = new AppointmentDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_APPOINTMENT_PERSONAL_ID, id);
        bundle.putString(Constants.BUNDLE_APPOINTMENT_PERSONAL_NAME, name);
        bundle.putString(Constants.BUNDLE_SERVICE_ID, serviceId);
        bundle.putString(Constants.BUNDLE_SERVICE_NAME, serviceName);
        bundle.putString(Constants.BUNDLE_SERVICE_DURATION,serviceDuration);
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

    public class PersonelHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ImageView img_personel;
        TextView txt_personel;
        ImageButton imb_personel;

        public PersonelHolder(View itemView) {
            super(itemView);
            txt_personel = itemView.findViewById(R.id.cell_personel_txt);
            img_personel = itemView.findViewById(R.id.cell_personel_img);
            imb_personel = itemView.findViewById(R.id.cell_personel_imb_popup);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select The Action");
            menu.add(0, v.getId(), 0, "Call");//groupId, itemId, order, title
            menu.add(0, v.getId(), 0, "SMS");
        }
    }
}
