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
public class NotLinearAdapter1 extends RecyclerView.Adapter<NotLinearAdapter1.ViewHolder> {

    private List<Subject> mList;
    private Context mContext;
    private boolean isGrid;

    public NotLinearAdapter1(Context context, List<Subject> list, boolean isGrid) {
        this.mList = list;
        this.mContext = context;
        this.isGrid = isGrid;
    }

    @NonNull
    @Override
    public NotLinearAdapter1.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(isGrid ? R.layout.item_grid : R.layout.item_linear, parent, false));
    }

    @Override
    public void onBindViewHolder(NotLinearAdapter1.ViewHolder holder, int position) {
        if (position % 5 == 0) {
            holder.tvTitle.setText("不可用");
            holder.ivImg.setImageResource(R.mipmap.ic_launcher);
            return;
        }
        position -= position / 5;
        holder.tvTitle.setText(mList.get(position).getTitle());
        holder.ivImg.setImageResource(mList.get(position).getImg());
    }

    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();
        if (position % 5 == 0) return -1;
        return position - position / 5;
    }

    @Override
    public int getItemCount() {
        return mList.size() + mList.size() / 5;
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
