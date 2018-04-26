package com.ekmobil.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ekmobil.ClockChoicesActivity;
import com.ekmobil.R;

import java.util.List;

public class DialogClockAdapter extends RecyclerView.Adapter<DialogClockAdapter.DialogHolder> {
    private List<String> list;
    private Dialog dialog;
    private ClockChoicesActivity clockChoicesActivity;

    public DialogClockAdapter(List<String> list, Dialog dialog, ClockChoicesActivity clockChoicesActivity) {
        this.list = list;
        this.dialog = dialog;
        this.clockChoicesActivity = clockChoicesActivity;
    }

    @Override
    public DialogHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_rcy_cell, parent, false);
        return new DialogHolder(view);
    }

    @Override
    public void onBindViewHolder(DialogHolder holder, final int position) {
        holder.txt_clock.setText(list.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent returnIntent = new Intent();
                returnIntent.putExtra("saat", list.get(position));
                clockChoicesActivity.setResult(Activity.RESULT_OK, returnIntent);
                clockChoicesActivity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class DialogHolder extends RecyclerView.ViewHolder {
        TextView txt_clock;

        public DialogHolder(View itemView) {
            super(itemView);
            txt_clock = itemView.findViewById(R.id.dialog_rcy_txt);
        }
    }
}
