package com.xuqiqiang.view.touch;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

/**
 * Created by xuqiqiang on 2020/09/07.
 */
public class RecyclerTouchHelper {

    private Context mContext;
    private RecyclerView mRecyclerView;
    private ItemTouchHelper mItemTouchHelper;
    private RecyclerView.ViewHolder mSelectViewHolder;
    private Adapter mAdapter;
    private boolean mResort;

    public RecyclerTouchHelper(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        this.mContext = recyclerView.getContext();

        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(int position, RecyclerView.ViewHolder vh) {
            }

            @Override
            public void onItemLongClick(int position, RecyclerView.ViewHolder vh) {
                if (mAdapter != null && mAdapter.isEnabled(vh)) {
                    mItemTouchHelper.startDrag(vh);
                    Vibrator vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(50);
                }
            }
        });
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            private ObjectAnimator mObjectAnimator;

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

                if (layoutManager instanceof GridLayoutManager) {
                    // ignore
                } else if (layoutManager instanceof LinearLayoutManager) {
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    if (((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.HORIZONTAL)
                        dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                }
                int swipeFlags = 0;
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                if (!mResort) return true;
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if (mAdapter != null)
                    mAdapter.onItemMove(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    mSelectViewHolder = viewHolder;
                    if (mObjectAnimator != null && mObjectAnimator.isRunning()) {
                        mObjectAnimator.cancel();
                    }

                    PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f, 1.1f);
                    PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f, 1.1f);
                    PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1f, 0.7f);
                    mObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(viewHolder.itemView,
                            scaleX, scaleY, alpha);
                    mObjectAnimator.setDuration(300);
                    mObjectAnimator.start();
                    if (mAdapter != null) {
                        View deleteView = mAdapter.getDeleteView();
                        if (deleteView != null)
                            deleteView.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mSelectViewHolder != null) {
                        if (mObjectAnimator != null && mObjectAnimator.isRunning()) {
                            mObjectAnimator.cancel();
                        }

                        PropertyValuesHolder scaleX = PropertyValuesHolder.ofFloat("scaleX", 1f);
                        PropertyValuesHolder scaleY = PropertyValuesHolder.ofFloat("scaleY", 1f);
                        PropertyValuesHolder alpha = PropertyValuesHolder.ofFloat("alpha", 1f);
                        mObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(mSelectViewHolder.itemView,
                                scaleX, scaleY, alpha);
                        mObjectAnimator.setDuration(300);
                        mObjectAnimator.start();
                        mSelectViewHolder = null;
                        if (mAdapter != null) {
                            View deleteView = mAdapter.getDeleteView();
                            if (deleteView != null)
                                deleteView.setVisibility(View.GONE);
                            if (mResort)
                                mAdapter.onResort();
                        }
                    }
                }
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    public void setResort(boolean resort) {
        this.mResort = resort;
    }

    public void setAdapter(Adapter adapter) {
        if (this.mAdapter != null)
            this.mAdapter.recyclerView = null;
        this.mAdapter = adapter;
        this.mAdapter.recyclerView = mRecyclerView;
    }

    public boolean dispatchTouchEvent(@NonNull final TouchListener listener, final MotionEvent e) {
//        Logger.d("dispatchTouchEvent curX:" + e.getX() + "curY:" + e.getY());
        if (mAdapter == null || mSelectViewHolder == null)
            return false;//return listener.dispatchTouchEvent(e);
        View deleteView = mAdapter.getDeleteView();
        if (deleteView == null) return false;//return listener.dispatchTouchEvent(e);
        if (e.getAction() == MotionEvent.ACTION_UP &&
                Utils.isInViewZone(deleteView, (int) e.getX(), (int) e.getY())) {
            mAdapter.onRequestDelete(mSelectViewHolder, new OnDeleteCallback() {
                @Override
                public void onDelete(boolean isDelete) {
                    if (isDelete && mAdapter.onDelete(mSelectViewHolder))
                        return;
                    e.setLocation(e.getX(), 0);
                    e.setAction(MotionEvent.ACTION_DOWN);
                    listener.dispatchTouchEvent(e);
                }
            });
            return true;
        }
        return false;
    }

    public interface OnDeleteCallback {
        void onDelete(boolean isDelete);
    }

    @SuppressWarnings("rawtypes")
    public abstract static class Adapter {

        private RecyclerView recyclerView;

        public abstract List getDataList();

        public abstract View getDeleteView();

        public abstract void onRequestDelete(RecyclerView.ViewHolder viewHolder, OnDeleteCallback callback);

        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public void onResort() {
        }

        public boolean onDelete(RecyclerView.ViewHolder viewHolder) {
            final List mDataList = getDataList();
            if (Utils.isEmpty(mDataList)) return false;
            if (viewHolder.getAdapterPosition() == mDataList.size() - 1) {
                mDataList.remove(mDataList.size() - 1);
                recyclerView.getAdapter().notifyDataSetChanged();
            } else {
                onItemMove(viewHolder.getAdapterPosition(), mDataList.size() - 1);
                viewHolder.itemView.setVisibility(View.INVISIBLE);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDataList.remove(mDataList.size() - 1);
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }
                }, 300);
            }
            return true;
        }

        public void onItemMove(int fromPosition, int toPosition) {
            List mDataList = getDataList();
            if (Utils.isEmpty(mDataList)) return;

            if (fromPosition < 0) fromPosition = 0;
            else if (fromPosition >= mDataList.size())
                fromPosition = mDataList.size() - 1;

            if (toPosition < 0) toPosition = 0;
            else if (toPosition >= mDataList.size())
                toPosition = mDataList.size() - 1;

            if (fromPosition < toPosition) {
                for (int i = fromPosition; i < toPosition; i++) {
                    Collections.swap(mDataList, i, i + 1);
                }
            } else {
                for (int i = fromPosition; i > toPosition; i--) {
                    Collections.swap(mDataList, i, i - 1);
                }
            }
            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
        }
    }
}