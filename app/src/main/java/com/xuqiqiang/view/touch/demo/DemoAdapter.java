package com.xuqiqiang.view.touch.demo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by xuqiqiang on 2020/09/17.
 */
public class DemoAdapter extends BaseRecyclerAdapter<Subject> {

    private boolean isGrid;

    public DemoAdapter(List<Subject> list, boolean isGrid) {
        this.mData = list;
        this.isGrid = isGrid;
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(isGrid ? R.layout.item_grid : R.layout.item_linear, parent, false));
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int realPosition, Subject data) {
        if (viewHolder instanceof ViewHolder) {
            ((ViewHolder) viewHolder).tvTitle.setText(data.getTitle());
            ((ViewHolder) viewHolder).ivImg.setImageResource(data.getImg());
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        ImageView ivImg;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            ivImg = itemView.findViewById(R.id.iv_img);
        }
    }
}