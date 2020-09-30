package com.xuqiqiang.view.touch;

import android.animation.ObjectAnimator;
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

import static android.animation.PropertyValuesHolder.ofFloat;

/**
 * Created by xuqiqiang on 2020/09/07.
 */
public class RecyclerTouchHelper {

    private static final float TOUCH_SCALE = 1.1f;
    private static final float TOUCH_ALPHA = 0.7f;

    private Context mContext;
    private RecyclerView mRecyclerView;
    private ItemTouchHelper mItemTouchHelper;
    private RecyclerView.ViewHolder mSelectViewHolder;
    private Adapter mAdapter;
    private boolean mResort;
    private int mFromPosition;
    private int mCurPosition;

    public RecyclerTouchHelper(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mContext = recyclerView.getContext();
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(int position, RecyclerView.ViewHolder vh) {
            }

            @Override
            public void onItemLongClick(int position, RecyclerView.ViewHolder vh) {
                if (mAdapter != null && mAdapter.isEnabled(vh)) {
                    mFromPosition = vh.getAdapterPosition();
                    mCurPosition = -1;
                    mItemTouchHelper.startDrag(vh);
                    Vibrator vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
                    vib.vibrate(50);
                }
            }
        });
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            private ObjectAnimator mObjectAnimator;

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

                //noinspection StatementWithEmptyBody
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
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                if (!mResort) return true;
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if (mAdapter != null) {
                    toPosition = mAdapter.onItemMove(fromPosition, toPosition);
                    if (toPosition >= 0)
                        mCurPosition = toPosition;
                }
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
                    mSelectViewHolder = viewHolder;
                    if (mAdapter != null) {
                        if (mObjectAnimator != null && mObjectAnimator.isRunning()) {
                            mObjectAnimator.cancel();
                        }

                        mObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(viewHolder.itemView,
                                ofFloat("scaleX", 1f, mAdapter.getTouchScale()),
                                ofFloat("scaleY", 1f, mAdapter.getTouchScale()),
                                ofFloat("alpha", 1f, mAdapter.getTouchAlpha()));
                        mObjectAnimator.setDuration(300);
                        mObjectAnimator.start();

                        View deleteView = mAdapter.getDeleteView();
                        if (deleteView != null)
                            deleteView.setVisibility(View.VISIBLE);
                    }
                } else {
                    if (mSelectViewHolder != null) {
                        if (mObjectAnimator != null) {
                            if (mObjectAnimator.isRunning()) {
                                mObjectAnimator.cancel();
                            }
                            mObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(mSelectViewHolder.itemView,
                                    ofFloat("scaleX", 1f),
                                    ofFloat("scaleY", 1f),
                                    ofFloat("alpha", 1f));
                            mObjectAnimator.setDuration(300);
                            mObjectAnimator.start();
                        }
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
            @SuppressWarnings("rawtypes")
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
                if (adapter != null) adapter.notifyDataSetChanged();
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
        if (mAdapter == null || mSelectViewHolder == null)
            return false;
        View deleteView = mAdapter.getDeleteView();
        if (deleteView == null) return false;
        if (e.getAction() == MotionEvent.ACTION_UP &&
                Utils.isInViewZone(deleteView, (int) e.getX(), (int) e.getY())) {
            mAdapter.onRequestDelete(mSelectViewHolder, new OnDeleteCallback() {
                @Override
                public void onDelete(boolean isDelete) {
                    Runnable cancelEvent = new Runnable() {
                        @Override
                        public void run() {
                            e.setAction(MotionEvent.ACTION_CANCEL);
                            listener.dispatchTouchEvent(e);
                        }
                    };
                    if (isDelete && mAdapter.onDelete(mSelectViewHolder, cancelEvent))
                        return;
                    cancelEvent.run();
                    restoreSequence();
                }
            });
            return true;
        }
        return false;
    }

    private void restoreSequence() {
        if (mCurPosition >= 0 && mFromPosition >= 0 && mCurPosition != mFromPosition) {
            mAdapter.onItemMove(mCurPosition, mFromPosition);
        }
    }

    public interface OnDeleteCallback {
        void onDelete(boolean isDelete);
    }

    @SuppressWarnings("rawtypes")
    public abstract static class Adapter {

        private RecyclerView recyclerView;
        private Handler mHandler = new Handler(Looper.getMainLooper());

        public abstract List getDataList();

        public abstract View getDeleteView();

        public abstract void onRequestDelete(RecyclerView.ViewHolder viewHolder, OnDeleteCallback callback);

        @SuppressWarnings("unused")
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public void onResort() {
        }

        public float getTouchScale() {
            return TOUCH_SCALE;
        }

        public float getTouchAlpha() {
            return TOUCH_ALPHA;
        }

        public boolean onDelete(final RecyclerView.ViewHolder viewHolder, final Runnable event) {
            final List mDataList = getDataList();
            if (Utils.isEmpty(mDataList)) return false;
            Runnable deleteEvent = new Runnable() {
                @Override
                public void run() {
                    event.run();
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mDataList.remove(mDataList.size() - 1);
                            RecyclerView.Adapter adapter = recyclerView.getAdapter();
                            if (adapter != null)
                                adapter.notifyItemRemoved(mDataList.size());
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder.itemView.setVisibility(View.VISIBLE);
                                }
                            }, 200);
                        }
                    }, 100);
                }
            };
            if (viewHolder.getAdapterPosition() == mDataList.size() - 1) {
                deleteEvent.run();
            } else {
                onItemMove(viewHolder.getAdapterPosition(), mDataList.size() - 1);
                viewHolder.itemView.setVisibility(View.INVISIBLE);
                mHandler.postDelayed(deleteEvent, 300);
            }
            return true;
        }

        public int onItemMove(int fromPosition, int toPosition) {
            List mDataList = getDataList();
            if (Utils.isEmpty(mDataList)) return -1;

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
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter != null) adapter.notifyItemMoved(fromPosition, toPosition);
            return toPosition;
        }
    }
}