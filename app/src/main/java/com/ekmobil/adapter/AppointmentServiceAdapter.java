package com.ekmobil.adapter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ekmobil.R;
import com.ekmobil.entity.ServiceEntity;
import com.ekmobil.fragment.AppointmentFragment;
import com.ekmobil.utility.Constants;

import java.util.List;

public class AppointmentServiceAdapter extends RecyclerView.Adapter<AppointmentServiceAdapter.ServiceHolder> {
    private Context context;
    private List<ServiceEntity> list;

    public AppointmentServiceAdapter(Context context, List<ServiceEntity> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ServiceHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_service, parent, false);

        return new ServiceHolder(view);
    }

    @Override
    public void onBindViewHolder(ServiceHolder holder, int position) {
        final ServiceEntity entity = list.get(position);
        holder.txt_serviceDuration.setText(entity.getDuration() + " dk");
        holder.txt_serviceName.setText(entity.getServiceName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppointmentFragment fragment = new AppointmentFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BUNDLE_SERVICE_ID, entity.getServiceId());
                bundle.putString(Constants.BUNDLE_SERVICE_NAME, entity.getServiceName());
                bundle.putString(Constants.BUNDLE_SERVICE_DURATION,entity.getDuration());
                fragment.setArguments(bundle);

                ((Activity) context).getFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment_layout, fragment)
                        .addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commitAllowingStateLoss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class ServiceHolder extends RecyclerView.ViewHolder {
        TextView txt_serviceName, txt_serviceDuration;

        public ServiceHolder(View itemView) {
            super(itemView);
            txt_serviceName = itemView.findViewById(R.id.cell_service_name);
            txt_serviceDuration = itemView.findViewById(R.id.cell_service_duration);
        }
    }
}
