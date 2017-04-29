package com.ait.tuckerwilliams.dontforgettosear.gui;

import android.graphics.Paint;
import android.widget.TextView;

public class DrawOver {

    //Empty constructor.
    public DrawOver() {}

    public void drawOverTextView(TextView tv) {
        tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    public void eraseDrawFromTextView(TextView tv) {
        tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
    }
}
