package com.ait.tuckerwilliams.dontforgettosear.view;

import android.graphics.Color;
import android.graphics.Paint;
import android.widget.TextView;

public class DrawOver {

    //Empty constructor.
    public DrawOver() {
    }

    public static void drawOverTextView(TextView tv) {

        tv.setPaintFlags(tv.getPaintFlags() | new Paint(Color.RED).STRIKE_THRU_TEXT_FLAG);
    }

    public static void eraseDrawFromTextView(TextView tv) {
        tv.setPaintFlags(tv.getPaintFlags() & (~new Paint(Color.RED).STRIKE_THRU_TEXT_FLAG));
    }
}
