package com.ekmobil.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ekmobil.R;
import com.ekmobil.entity.ServiceEntity;

import java.util.ArrayList;
import java.util.List;

public class AppointmentInnerAdapter extends RecyclerView.Adapter<AppointmentInnerAdapter.InnerHolder> {
    private List<ServiceEntity> list = new ArrayList<>();

    @Override
    public InnerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_announcement_inner, parent, false);
        return new InnerHolder(view);
    }

    public void addItem(ServiceEntity entity) {
        list.add(entity);
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(InnerHolder holder, final int position) {
        final ServiceEntity entity = list.get(position);

        holder.txt_serviceName.setText(entity.getServiceName());
        holder.txt_serviceDuration.setText(entity.getDuration());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.remove(entity);
                notifyItemRemoved(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        TextView txt_serviceName, txt_serviceDuration;

        public InnerHolder(View itemView) {
            super(itemView);
            txt_serviceName = itemView.findViewById(R.id.cell_inner_name);
            txt_serviceDuration = itemView.findViewById(R.id.cell_inner_duration);
        }
    }
}
