package com.xuqiqiang.view.touch.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by xuqiqiang on 2020/09/17.
 */
public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.ViewHolder> {

    private List<Subject> mList;
    private Context mContext;

    public DemoAdapter(Context context, List<Subject> list) {
        this.mList = list;
        this.mContext = context;
    }

    @NonNull
    @Override
    public DemoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_grid, parent, false));
    }

    @Override
    public void onBindViewHolder(DemoAdapter.ViewHolder holder, int position) {
        holder.tvTitle.setText(mList.get(position).getTitle());
        holder.ivImg.setImageResource(mList.get(position).getImg());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView ivImg;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);
            ivImg = (ImageView) itemView.findViewById(R.id.iv_img);
        }
    }
}
