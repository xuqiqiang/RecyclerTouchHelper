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
public class DemoAdapter extends BaseRecyclerAdapter<Subject> {

    private boolean isGrid;

    public DemoAdapter(Context context, List<Subject> list, boolean isGrid) {
        this.mDatas = list;
//        this.mContext = context;
        this.isGrid = isGrid;
    }

    @Override
    public RecyclerView.ViewHolder onCreate(ViewGroup parent, int viewType) {

        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(isGrid ? R.layout.item_grid : R.layout.item_linear, parent, false));

//        View layout = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
//        return new MyHolder(layout);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int realPosition, Subject data) {
        if(viewHolder instanceof ViewHolder) {
            ((ViewHolder) viewHolder).tvTitle.setText(data.getTitle());
            ((ViewHolder) viewHolder).ivImg.setImageResource(data.getImg());
//            ((ViewHolder) viewHolder).text.setText(data);
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


//public class DemoAdapter extends RecyclerView.Adapter<DemoAdapter.ViewHolder> {
//
//    private List<Subject> mList;
//    private Context mContext;
//    private boolean isGrid;
//
//    public DemoAdapter(Context context, List<Subject> list, boolean isGrid) {
//        this.mList = list;
//        this.mContext = context;
//        this.isGrid = isGrid;
//    }
//
//    @NonNull
//    @Override
//    public DemoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        return new ViewHolder(LayoutInflater.from(mContext)
//                .inflate(isGrid ? R.layout.item_grid : R.layout.item_linear, parent, false));
//    }
//
//    @Override
//    public void onBindViewHolder(DemoAdapter.ViewHolder holder, int position) {
//        holder.tvTitle.setText(mList.get(position).getTitle());
//        holder.ivImg.setImageResource(mList.get(position).getImg());
//    }
//
//    @Override
//    public int getItemCount() {
//        return mList == null ? 0 : mList.size();
//    }
//
//    static class ViewHolder extends RecyclerView.ViewHolder {
//        TextView tvTitle;
//        ImageView ivImg;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//            tvTitle = itemView.findViewById(R.id.tv_title);
//            ivImg = itemView.findViewById(R.id.iv_img);
//        }
//    }
//}
