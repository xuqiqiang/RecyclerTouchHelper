package com.xuqiqiang.view.touch;

import android.animation.ObjectAnimator;
import android.app.Service;
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

    public static final int TOUCH_MODE_DOWN = 1, TOUCH_MODE_LONG_PRESS = 2;
    private static final float TOUCH_SCALE = 1.1f;
    private static final float TOUCH_ALPHA = 0.7f;
    private RecyclerView mRecyclerView;
    private ItemTouchHelper mItemTouchHelper;
    private RecyclerView.ViewHolder mSelectViewHolder;
    private Adapter mAdapter;
    private boolean mResort;
    private int mFromPosition;
    private int mCurPosition;
    private int mFromAdapterPosition;
    private int mCurAdapterPosition;
    private int mTouchMode = TOUCH_MODE_LONG_PRESS;
    private int mDragDirects = -1;
    private int mSwipeDirects;

    public RecyclerTouchHelper(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(mRecyclerView) {
            @Override
            public void onItemClick(int position, RecyclerView.ViewHolder vh) {
            }

            @Override
            public void onItemLongClick(int position, RecyclerView.ViewHolder vh) {
                if (mTouchMode == TOUCH_MODE_LONG_PRESS)
                    onTouchStart(vh);
            }

            @Override
            public void onItemTouchDown(int position, RecyclerView.ViewHolder vh) {
                if (mTouchMode == TOUCH_MODE_DOWN)
                    onTouchStart(vh);
            }
        });
        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

            private ObjectAnimator mObjectAnimator;

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = mDragDirects;
                if (dragFlags <= 0) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;

                    //noinspection StatementWithEmptyBody
                    if (layoutManager instanceof GridLayoutManager) {
                        // ignore
                    } else if (layoutManager instanceof LinearLayoutManager) {
                        dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                        if (((LinearLayoutManager) layoutManager).getOrientation() == LinearLayoutManager.HORIZONTAL)
                            dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    }
                }
                return makeMovementFlags(dragFlags, mSwipeDirects);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                if (!mResort) return true;
                if (mAdapter != null) {
                    int fromPosition = mAdapter.getItemPosition(viewHolder);
                    int toPosition = mAdapter.getItemPosition(target);
                    toPosition = mAdapter.onItemMove(fromPosition, toPosition);
                    int toAdapterPosition = mAdapter.notifyItemMoved(viewHolder, target);
                    if (toPosition >= 0) {
                        mCurPosition = toPosition;
                        mCurAdapterPosition = toAdapterPosition;
                    }
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
//                        mAdapter.positionOffset = viewHolder.getAdapterPosition() - mAdapter.getItemPosition(viewHolder);
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
                if (adapter != null) {
                    if (mRecyclerView.isComputingLayout()) {
                        mRecyclerView.post(new Runnable() {
                            @Override
                            public void run() {
                                RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
                                if (adapter != null) adapter.notifyDataSetChanged();
                            }
                        });
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void onTouchStart(RecyclerView.ViewHolder vh) {
        if (mAdapter != null && mAdapter.isEnabled(vh)) {
            mFromPosition = mAdapter.getItemPosition(vh);
            mFromAdapterPosition = vh.getAdapterPosition();
            mAdapter.positionOffset = mFromAdapterPosition - mFromPosition;
            mCurPosition = -1;
            mCurAdapterPosition = -1;
            mItemTouchHelper.startDrag(vh);
            mAdapter.onTouchStart(vh);
        }
    }

    /**
     * 是否拖拽时对数据重排序
     */
    public void setResort(boolean resort) {
        this.mResort = resort;
    }

    /**
     * 响应拖拽的方式
     *
     * @param touchMode TOUCH_MODE_DOWN/TOUCH_MODE_LONG_PRESS
     */
    public void setTouchMode(int touchMode) {
        this.mTouchMode = touchMode;
    }

    /**
     * 可拖拽的方向
     *
     * @param dragDirects ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
     */
    public void setDragDirects(int dragDirects) {
        this.mDragDirects = dragDirects;
    }

    @Deprecated
    public void setSwipeDirects(int swipeDirects) {
        this.mSwipeDirects = swipeDirects;
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
            mAdapter.notifyItemMoved(mCurAdapterPosition, mFromAdapterPosition);
        }
    }

    public interface OnDeleteCallback {
        /**
         * 对需要删除的ViewHolder的数据处理完成后，调用onDelete刷新UI
         *
         * @param isDeleted 是否已删除
         */
        void onDelete(boolean isDeleted);
    }

    @SuppressWarnings("rawtypes")
    public abstract static class Adapter {

        private RecyclerView recyclerView;
        private int positionOffset;
        private Handler mHandler = new Handler(Looper.getMainLooper());

        public abstract List getDataList();

        /**
         * 拖拽到删除的区域
         */
        public abstract View getDeleteView();

        /**
         * 删除ViewHolder前的对数据处理的回调
         *
         * @param callback 对需要删除的ViewHolder的数据处理完成后，调用callback.onDelete刷新UI
         */
        public abstract void onRequestDelete(RecyclerView.ViewHolder viewHolder, OnDeleteCallback callback);

        /**
         * 拖拽开始的回调
         */
        public void onTouchStart(RecyclerView.ViewHolder viewHolder) {
            Vibrator vib = (Vibrator) recyclerView.getContext().getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(50);
        }

        /**
         * 删除ViewHolder后的回调
         */
        public void onDeleted(RecyclerView.ViewHolder viewHolder) {
        }

        /**
         * 是否响应拖拽
         */
        @SuppressWarnings("unused")
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        /**
         * 数据重排序后的回调事件
         */
        public void onResort() {
        }

        /**
         * 如果你的ViewHolder中含有header或者ViewHolder列表和数据不是线性关系，请务必要实现这个方法
         *
         * @return ViewHolder对应数据的索引位置
         */
        public int getItemPosition(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getAdapterPosition();
        }

        /**
         * 如果你的ViewHolder列表和数据不是线性关系，请务必要实现这个方法
         *
         * @return 第一个数据所在的ViewHolder的位置
         */
        public int getFirstPosition() {
            return positionOffset;
        }

        /**
         * 如果你的ViewHolder列表和数据不是线性关系，请务必要实现这个方法
         *
         * @return 最后一个数据所在的ViewHolder的位置
         */
        public int getLastPosition() {
            return getDataList().size() - 1 + positionOffset;
        }

        /**
         * @return ViewHolder被选中后的放大倍数
         */
        public float getTouchScale() {
            return TOUCH_SCALE;
        }

        /**
         * @return ViewHolder被选中后的透明度
         */
        public float getTouchAlpha() {
            return TOUCH_ALPHA;
        }

        private boolean onDelete(final RecyclerView.ViewHolder viewHolder, final Runnable event) {
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
                                adapter.notifyItemRemoved(getLastPosition() + 1);
                            onDeleted(viewHolder);
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
            if (getItemPosition(viewHolder) == mDataList.size() - 1) {
                deleteEvent.run();
            } else {
                onItemMove(getItemPosition(viewHolder), mDataList.size() - 1);
                notifyItemMoved(viewHolder.getAdapterPosition(), getLastPosition());
                viewHolder.itemView.setVisibility(View.INVISIBLE);
                mHandler.postDelayed(deleteEvent, 300);
            }
            return true;
        }

        /**
         * 数据的重排序
         */
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
//            RecyclerView.Adapter adapter = recyclerView.getAdapter();
//            if (adapter != null) adapter.notifyItemMoved(fromPosition, toPosition);
            return toPosition;
        }

        public int notifyItemMoved(RecyclerView.ViewHolder fromViewHolder,
                                   RecyclerView.ViewHolder toViewHolder) {
            int fromAdapterPosition = fromViewHolder.getAdapterPosition();
            int toAdapterPosition = toViewHolder.getAdapterPosition();

            int firstPosition = getFirstPosition();
            int lastPosition = getLastPosition();

            if (fromAdapterPosition < firstPosition) fromAdapterPosition = firstPosition;
            else if (fromAdapterPosition > lastPosition) fromAdapterPosition = lastPosition;

            if (toAdapterPosition < firstPosition) toAdapterPosition = firstPosition;
            else if (toAdapterPosition > lastPosition) toAdapterPosition = lastPosition;

            return notifyItemMoved(fromAdapterPosition, toAdapterPosition);
        }

        public int notifyItemMoved(int fromPosition, int toPosition) {
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter != null) adapter.notifyItemMoved(fromPosition, toPosition);
            return toPosition;
        }
    }
}