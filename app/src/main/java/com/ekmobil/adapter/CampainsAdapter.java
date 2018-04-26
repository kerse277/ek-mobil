package com.ekmobil.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ekmobil.R;
import com.ekmobil.entity.CampainEntity;
import com.ekmobil.utility.GlideApp;

import java.util.List;

public class CampainsAdapter extends RecyclerView.Adapter<CampainsAdapter.CampainsHolder> {
    private Context context;
    private List<CampainEntity> list;

    public CampainsAdapter(Context context, List<CampainEntity> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public CampainsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_campains, parent, false);
        return new CampainsHolder(view);
    }

    @Override
    public void onBindViewHolder(CampainsHolder holder, int position) {
        final CampainEntity entity = list.get(position);
        GlideApp.with(context).load(entity.getImageUrl())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .fitCenter().into(holder.img_campains);

        holder.txt_campains.setText(entity.getDescription());
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (entity.get() != null && entity.getProductUrl().length() > 0) {
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(entity.getProductUrl()));
//                    context.startActivity(browserIntent);
//                } else {
//                    Toast.makeText(context, "Sayfa Bulunamadı..", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class CampainsHolder extends RecyclerView.ViewHolder {
        ImageView img_campains;
        TextView txt_campains;

        public CampainsHolder(View itemView) {
            super(itemView);

            img_campains = itemView.findViewById(R.id.cell_campains_img);
            txt_campains = itemView.findViewById(R.id.cell_campains_txt);
        }
    }
}
