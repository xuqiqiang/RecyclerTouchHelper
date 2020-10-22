package com.xuqiqiang.view.touch.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void test1(View view) {
        Intent intent = new Intent(this, DemoActivity.class);
        intent.putExtra("hasResort", true);
        startActivity(intent);
    }

    public void test2(View view) {
        Intent intent = new Intent(this, DemoActivity.class);
        intent.putExtra("isGrid", true);
        intent.putExtra("hasResort", true);
        startActivity(intent);
    }

    public void test3(View view) {
        Intent intent = new Intent(this, DemoActivity.class);
        intent.putExtra("hasDelete", true);
        startActivity(intent);
    }

    public void test4(View view) {
        Intent intent = new Intent(this, DemoActivity.class);
        intent.putExtra("isGrid", true);
        intent.putExtra("hasDelete", true);
        startActivity(intent);
    }

    public void test5(View view) {
        Intent intent = new Intent(this, DemoActivity.class);
        intent.putExtra("hasResort", true);
        intent.putExtra("hasDelete", true);
        startActivity(intent);
    }

    public void test6(View view) {
        Intent intent = new Intent(this, DemoActivity.class);
        intent.putExtra("isGrid", true);
        intent.putExtra("hasResort", true);
        intent.putExtra("hasDelete", true);
        startActivity(intent);
    }

    public void test7(View view) {
        Intent intent = new Intent(this, DemoActivity.class);
        intent.putExtra("hasResort", true);
        intent.putExtra("hasDelete", true);
        intent.putExtra("hasHeader", true);
        startActivity(intent);
    }

    public void test8(View view) {
        Intent intent = new Intent(this, DemoActivity.class);
        intent.putExtra("isGrid", true);
        intent.putExtra("hasResort", true);
        intent.putExtra("hasDelete", true);
        intent.putExtra("hasHeader", true);
        startActivity(intent);
    }
}
