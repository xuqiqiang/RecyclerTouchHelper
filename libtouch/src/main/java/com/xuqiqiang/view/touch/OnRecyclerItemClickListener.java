package com.xuqiqiang.view.touch;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by xuqiqiang on 2020/09/07.
 */
public abstract class OnRecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private GestureDetectorCompat mGestureDetector;
    private RecyclerView recyclerView;

    public OnRecyclerItemClickListener(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        mGestureDetector = new GestureDetectorCompat(recyclerView.getContext(), new ItemTouchHelperGestureListener());
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    public abstract void onItemClick(int position, RecyclerView.ViewHolder vh);

    public abstract void onItemLongClick(int position, RecyclerView.ViewHolder vh);

    public abstract void onItemTouchDown(int position, RecyclerView.ViewHolder vh);

    private class ItemTouchHelperGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null) {
                onItemClick(recyclerView.getChildLayoutPosition(child),
                        recyclerView.getChildViewHolder(child));
            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null) {
                onItemLongClick(recyclerView.getChildLayoutPosition(child),
                        recyclerView.getChildViewHolder(child));
            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
            if (child != null) {
                onItemTouchDown(recyclerView.getChildLayoutPosition(child),
                        recyclerView.getChildViewHolder(child));
            }
            return true;
        }
    }
}