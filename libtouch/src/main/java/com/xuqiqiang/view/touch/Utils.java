package com.xuqiqiang.view.touch;

import android.graphics.Rect;
import android.view.View;

import java.util.Collection;

/**
 * Created by xuqiqiang on 2020/09/17.
 */
public class Utils {
    private static Rect mViewRect;

    public static boolean isInViewZone(View view, int x, int y) {
        if (null == mViewRect) {
            mViewRect = new Rect();
        }
        view.getDrawingRect(mViewRect);
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        mViewRect.left = location[0];
        mViewRect.top = location[1];
        mViewRect.right = mViewRect.right + location[0];
        mViewRect.bottom = mViewRect.bottom + location[1];
        return mViewRect.contains(x, y);
    }

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.size() <= 0;
    }
}