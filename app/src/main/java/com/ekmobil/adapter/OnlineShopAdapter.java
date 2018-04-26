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
import com.ekmobil.fragment.OnlineShopDetailFragment;
import com.ekmobil.utility.Constants;

import java.util.List;

public class OnlineShopAdapter extends RecyclerView.Adapter<OnlineShopAdapter.OnlineShopHolder> {
    private Context context;
    private List<String> list;

    public OnlineShopAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public OnlineShopHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_online_shop, parent, false);
        return new OnlineShopHolder(view);
    }

    @Override
    public void onBindViewHolder(OnlineShopHolder holder, final int position) {
        holder.txt_product_group.setText(list.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnlineShopDetailFragment fragment = new OnlineShopDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BUNDLE_ONLINE_SHOP_PRODUCT_GROUP, list.get(position));
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

    public class OnlineShopHolder extends RecyclerView.ViewHolder {
        TextView txt_product_group;

        public OnlineShopHolder(View itemView) {
            super(itemView);
            txt_product_group = itemView.findViewById(R.id.cell_online_shop_produt_group);
        }
    }
}
