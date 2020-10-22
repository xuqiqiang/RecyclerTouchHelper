package com.xuqiqiang.view.touch.demo;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Created by xuqiqiang on 2020/09/17.
 */
public class NotLinearAdapter extends DemoAdapter {

    public NotLinearAdapter(List<Subject> list, boolean isGrid) {
        super(list, isGrid);
    }

    @Override
    public void onBind(RecyclerView.ViewHolder viewHolder, int realPosition, Subject data) {
        if (viewHolder instanceof DemoAdapter.ViewHolder) {
            if (realPosition < 0) {
                ((DemoAdapter.ViewHolder) viewHolder).tvTitle.setText("不可用");
                ((DemoAdapter.ViewHolder) viewHolder).ivImg.setImageResource(R.mipmap.ic_launcher);
                return;
            }
            ((DemoAdapter.ViewHolder) viewHolder).tvTitle.setText(data.getTitle());
            ((DemoAdapter.ViewHolder) viewHolder).ivImg.setImageResource(data.getImg());
        }
    }

    @Override
    public int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getAdapterPosition();
        if (position % 5 == 0) return -1;
        return position - position / 5;
    }

    @Override
    public int getItemCount() {
        return mData.size() + mData.size() / 5;
    }
}
