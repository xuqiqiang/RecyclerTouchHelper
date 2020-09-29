package com.xuqiqiang.view.touch.demo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xuqiqiang.view.touch.DividerGridItemDecoration;
import com.xuqiqiang.view.touch.RecyclerTouchHelper;
import com.xuqiqiang.view.touch.TouchListener;

import java.util.ArrayList;
import java.util.List;

import static com.xuqiqiang.view.touch.demo.Constants.titles;

/**
 * Created by xuqiqiang on 2020/09/17.
 */
public class DemoActivity extends AppCompatActivity implements TouchListener {
    private RecyclerView mRecyclerView;
    private View mDeleteView;
    private List<Subject> mList = new ArrayList<>();
    private RecyclerTouchHelper mRecyclerTouchHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
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
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4,
                GridLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(new DemoAdapter(this, mList));
    }

    private void initTouch() {
        mDeleteView = findViewById(R.id.ll_delete);
        mRecyclerTouchHelper = new RecyclerTouchHelper(mRecyclerView);
        // 重排序
//        mRecyclerTouchHelper.setResort(true);
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

        @Override
        public View getDeleteView() {
            return mDeleteView;
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
