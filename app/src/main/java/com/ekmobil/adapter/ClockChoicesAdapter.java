package com.ekmobil.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.ekmobil.ClockChoicesActivity;
import com.ekmobil.R;

import java.util.HashMap;
import java.util.List;

public class ClockChoicesAdapter extends RecyclerView.Adapter<ClockChoicesAdapter.ClockHolder> {
    private Context context;
    private HashMap<String, List<String>> hashMap;
    private ClockChoicesActivity clockChoicesActivity;

    public ClockChoicesAdapter(Context context, HashMap<String, List<String>> hashMap, ClockChoicesActivity clockChoicesActivity) {
        this.context = context;
        this.hashMap = hashMap;
        this.clockChoicesActivity = clockChoicesActivity;
    }

    @Override
    public ClockHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_clock_choice, parent, false);
        return new ClockHolder(view);
    }

    @Override
    public void onBindViewHolder(ClockHolder holder, int position) {
        String clock = "";
        if ((position + 7) < 10)
            clock = "0" + (position + 7);
        else
            clock = String.valueOf((position + 7));
//        clock = clock + ":00";
        holder.txt_clock.setText(clock + ":00");

        if (hashMap.get(clock) != null && hashMap.get(clock).size() > 0) {
            holder.txt_clock.setTextColor(Color.BLACK);

            final String finalClock = clock;
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(hashMap.get(finalClock));
                }
            });
        } else {
            holder.txt_clock.setTextColor(Color.LTGRAY);
        }
    }

    private void showDialog(List<String> list) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_choice_clock);

        RecyclerView rcy_dialog = dialog.findViewById(R.id.rcy_choice_clock);
        DialogClockAdapter adapter = new DialogClockAdapter(list,dialog,clockChoicesActivity);
        rcy_dialog.setHasFixedSize(true);
        rcy_dialog.setLayoutManager(new LinearLayoutManager(context));
        rcy_dialog.setAdapter(adapter);

        Button btn_cancel = dialog.findViewById(R.id.btn_choice_clock_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public int getItemCount() {
        return 17;
//        return (hashMap != null ? hashMap.size() : 0);
    }

    public class ClockHolder extends RecyclerView.ViewHolder {
        TextView txt_clock;

        public ClockHolder(View itemView) {
            super(itemView);
            txt_clock = itemView.findViewById(R.id.cell_clock_choices_txt);
        }
    }
}
