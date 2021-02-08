package fr.eurecom.restaurantv3;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * Created by admin on 10/09/17.
 */

public class CircleView extends View {

    Paint paint;
    private int cX=20;
    private int cY=20;
    Bitmap dragon = null;
    public CircleView(Context context, int c) {
        super(context);
        init(context,c);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, R.color.green));
        paint.setStyle(Paint.Style.FILL);
        dragon =  BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_check_circle_24 );
    }

    private void init(Context context, int c) {
        paint = new Paint();
        paint.setColor(ContextCompat.getColor(context, c));
        paint.setStyle(Paint.Style.FILL);
        dragon =  BitmapFactory.decodeResource(getResources(), R.drawable.ic_baseline_check_circle_24 );
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
// Icon made by https://www.flaticon.com/authors/freepik from www.flaticon.com
          canvas.drawCircle(cX, cY, 20, paint);
        //canvas.drawBitmap(dragon,cX,cY,null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }


}
