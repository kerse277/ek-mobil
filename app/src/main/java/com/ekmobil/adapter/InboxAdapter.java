package com.ekmobil.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ekmobil.R;
import com.ekmobil.entity.InboxEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InboxAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<InboxEntity> list;
    private final int TYPE_HEADER = 1000;
    private final int TYPE_CONTENT = 2000;
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");


    public InboxAdapter(Context context, List<InboxEntity> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return TYPE_HEADER;
        return TYPE_CONTENT;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_inbox_header, parent, false);
            return new InboxHeaderHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_inbox, parent, false);
            return new InboxHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position != 0) {
            InboxEntity entity = list.get(position - 1);
            ((InboxHolder) holder).txt_sender.setText(entity.getSenderName());
            ((InboxHolder) holder).txt_subject.setText(entity.getSubject());
            ((InboxHolder) holder).txt_content.setText(entity.getContent());

            Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(entity.getDate());
            String date = "";
            while (m.find()) {
                date = m.group(1);
            }
            if (date != null && date.length() > 0) {
                Date date1 = new Date(Long.parseLong(date));
                ((InboxHolder) holder).txt_date.setText(sdf.format(date1));
            }
        }
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() + 1 : 1);
    }

    public class InboxHolder extends RecyclerView.ViewHolder {
        TextView txt_sender, txt_subject, txt_date, txt_content;

        public InboxHolder(View itemView) {
            super(itemView);
            txt_sender = itemView.findViewById(R.id.cell_inbox_sender);
            txt_subject = itemView.findViewById(R.id.cell_inbox_subject);
            txt_date = itemView.findViewById(R.id.cell_inbox_date);
            txt_content = itemView.findViewById(R.id.cell_inbox_content);
        }
    }

    public class InboxHeaderHolder extends RecyclerView.ViewHolder {
        public InboxHeaderHolder(View itemView) {
            super(itemView);
        }
    }
}
