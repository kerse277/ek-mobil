package com.ekmobil.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ekmobil.R;
import com.ekmobil.entity.ProductEntity;
import com.ekmobil.utility.GlideApp;

import java.util.List;

/**
 * Created by melih on 25.03.2018.
 */

public class PriceListAdapter extends RecyclerView.Adapter<PriceListAdapter.PriceListHolder> {
    private Context context;
    private List<ProductEntity> list;

    public PriceListAdapter(Context context, List<ProductEntity> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public PriceListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_price_list, parent, false);
        return new PriceListHolder(view);
    }

    @Override
    public void onBindViewHolder(PriceListHolder holder, int position) {
        final ProductEntity entity = list.get(position);

        GlideApp.with(context).load(entity.getProductImage())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .fitCenter().into(holder.img_product);

        holder.txt_description.setText(entity.getProductName());
        holder.txt_price.setText(entity.getProductPrice() + " TL");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entity.getProductUrl() != null && entity.getProductUrl().length() > 0) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(entity.getProductUrl()));
                    context.startActivity(browserIntent);
                } else {
                    Toast.makeText(context, "Sayfa Bulunamadı..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class PriceListHolder extends RecyclerView.ViewHolder {
        ImageView img_product;
        TextView txt_description, txt_price;

        public PriceListHolder(View itemView) {
            super(itemView);
            img_product = itemView.findViewById(R.id.cell_online_shop_img);
            txt_description = itemView.findViewById(R.id.cell_online_shop_txt_description);
            txt_price = itemView.findViewById(R.id.cell_online_shop_txt_price);
        }
    }
}
