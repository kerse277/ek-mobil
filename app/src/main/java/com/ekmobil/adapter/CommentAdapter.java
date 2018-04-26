package com.ekmobil.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ekmobil.R;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentHolder> {
    private List<String> list;

    public CommentAdapter(List<String> list) {
        this.list = list;
    }

    @Override
    public CommentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_comment, parent, false);
        return new CommentHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentHolder holder, int position) {
        holder.txt_comment.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return (list != null ? list.size() : 0);
    }

    public class CommentHolder extends RecyclerView.ViewHolder {
        TextView txt_comment;

        public CommentHolder(View itemView) {
            super(itemView);
            txt_comment = itemView.findViewById(R.id.cell_comment_txt);
        }
    }
}
