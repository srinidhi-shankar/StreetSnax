package com.streetsnax.srinidhi.streetsnax.utilities;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ViewFlipper;

/**
 * Created by I15230 on 12/31/2015.
 */
public class ViewFlipperExtender extends ViewFlipper {

    Paint paint = new Paint();

    public ViewFlipperExtender(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        int width = getWidth();

        float margin = 4;
        float radius = 10;
        float radMargin = (radius + margin) * 2 * getChildCount() / 2;
        float cx = (width / 2) - radMargin;
        float cy = getHeight() - 15;

        canvas.save();

        for (int i = 0; i < getChildCount(); i++) {
            if (i == getDisplayedChild()) {
                paint.setColor(Color.RED);
                canvas.drawCircle(cx, cy, radius, paint);

            } else {
                paint.setColor(Color.GRAY);
                canvas.drawCircle(cx, cy, radius, paint);
            }
            cx += 2 * (radius + margin);
        }
        canvas.restore();
    }

}