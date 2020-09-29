package com.xuqiqiang.view.touch.demo;

/**
 * Created by xuqiqiang on 2020/09/17.
 */
public class Subject {
    private String title;
    private int img;

    public Subject(String title, int img) {
        this.title = title;
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
