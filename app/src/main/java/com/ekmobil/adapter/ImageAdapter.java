package com.ekmobil.adapter;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.request.target.SizeReadyCallback;
import com.ekmobil.R;
import com.ekmobil.entity.ImageEntity;
import com.ekmobil.fragment.MainPageDetailFragment;
import com.ekmobil.utility.Constants;
import com.ekmobil.utility.GlideApp;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageHolder> {
    private Context context;
    private List<ImageEntity> list;

    public ImageAdapter(Context context, List<ImageEntity> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_image, parent, false);
        return new ImageHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageHolder holder, int position) {
        final ImageEntity entity = list.get(position);

        GlideApp.with(context).load(entity.getImageUrl())
                .placeholder(R.drawable.ic_logo)
                .error(R.drawable.ic_logo)
                .centerCrop().into(holder.img_image).getSize(new SizeReadyCallback() {
            @Override
            public void onSizeReady(int width, int height) {

                holder.img_image.getLayoutParams().height=width;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainPageDetailFragment fragment = new MainPageDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString(Constants.BUNDLE_IMAGE_URL, entity.getImageUrl());
                bundle.putString(Constants.BUNDLE_IMAGE_ID, entity.getId());

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

    public class ImageHolder extends RecyclerView.ViewHolder {
        ImageView img_image;

        public ImageHolder(View itemView) {
            super(itemView);
            img_image = itemView.findViewById(R.id.cell_image);
        }
    }
}
