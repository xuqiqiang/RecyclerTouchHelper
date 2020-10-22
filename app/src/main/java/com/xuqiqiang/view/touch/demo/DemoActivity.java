package com.xuqiqiang.view.touch.demo;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuqiqiang.view.touch.DividerGridItemDecoration;
import com.xuqiqiang.view.touch.RecyclerTouchHelper;
import com.xuqiqiang.view.touch.TouchListener;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import static com.xuqiqiang.view.touch.demo.Constants.titles;

/**
 * Created by xuqiqiang on 2020/09/17.
 */
public class DemoActivity extends AppCompatActivity implements TouchListener {
    private static SoftReference<Toast> mToast;
    private RecyclerView mRecyclerView;
    private View mDeleteView;
    private DemoAdapter mAdapter;
    private List<Subject> mList = new ArrayList<>();
    private RecyclerTouchHelper mRecyclerTouchHelper;
    private boolean isGrid;
    private boolean hasResort;
    private boolean hasDelete;
    private boolean hasHeader;

    private static void showMessage(Context context, String message) {
        if (mToast != null) {
            Toast t = mToast.get();
            if (t != null) {
                t.cancel();
            }
        }
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        mToast = new SoftReference<>(toast);
        toast.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        Intent intent = getIntent();
        isGrid = intent.getBooleanExtra("isGrid", false);
        hasResort = intent.getBooleanExtra("hasResort", false);
        hasDelete = intent.getBooleanExtra("hasDelete", false);
        hasHeader = intent.getBooleanExtra("hasHeader", false);
        initData();
        initView();
        initTouch();
    }

    private void initData() {
        for (int i = 0; i < titles.length; i++) {
            int imageId = getResources().getIdentifier("ic_category_" + i, "mipmap", getPackageName());
            mList.add(new Subject(titles[i], imageId));
        }
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.addItemDecoration(new DividerGridItemDecoration(this));
        if (isGrid) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4,
                    GridLayoutManager.VERTICAL, false));
        } else {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                    RecyclerView.VERTICAL, false));
        }

        mAdapter = new NotLinearAdapter(mList, isGrid);
        if (hasHeader) {
            View header = LayoutInflater.from(this).inflate(R.layout.item_header, mRecyclerView, false);
            mAdapter.setHeaderView(header);
            View footer = LayoutInflater.from(this).inflate(R.layout.item_footer, mRecyclerView, false);
            mAdapter.setFootView(footer);
        }
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener<Subject>() {
            @Override
            public void onItemClick(int position, Subject data) {
                if (data != null)
                    showMessage(DemoActivity.this, data.getTitle());
            }
        });
    }

    private void initTouch() {
        mDeleteView = findViewById(R.id.ll_delete);
        mRecyclerTouchHelper = new RecyclerTouchHelper(mRecyclerView);
        // 重排序
        mRecyclerTouchHelper.setResort(hasResort);
        mRecyclerTouchHelper.setAdapter(new TouchAdapter());
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        if (mRecyclerTouchHelper.dispatchTouchEvent(this, e))
            return false;
        return super.dispatchTouchEvent(e);
    }

    public class TouchAdapter extends RecyclerTouchHelper.Adapter {

        @SuppressWarnings("rawtypes")
        @Override
        public List getDataList() {
            return mList;
        }

        // region 带header的列表，请务必实现这两个方法
        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int position = getItemPosition(viewHolder);
            return position >= 0 && position < mList.size();
        }

        @Override
        public int getItemPosition(RecyclerView.ViewHolder viewHolder) {
            return mAdapter.getRealPosition(viewHolder);
        }
        // endregion

        @Override
        public View getDeleteView() {
            return hasDelete ? mDeleteView : null;
        }

        @Override
        public void onRequestDelete(RecyclerView.ViewHolder viewHolder, final RecyclerTouchHelper.OnDeleteCallback callback) {
            new AlertDialog.Builder(DemoActivity.this)
                    .setTitle("删除")
                    .setMessage("是否删除选中的图片")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(DemoActivity.this, "已删除", Toast.LENGTH_SHORT).show();
                            callback.onDelete(true);
                            dialog.cancel();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            callback.onDelete(false);
                            dialog.cancel();
                        }
                    })
                    .create().show();
        }
    }
}
