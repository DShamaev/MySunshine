package com.example.hikimori911.sunshine.app.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;

/**
 * Created by hikimori911 on 14.03.2015.
 */
public class MyView extends View {

    public static final int CIRCLE_STROKE_WIDTH = 10;

    public MyView(Context context){
        super(context);
        degrees = -1;
    }

    public MyView(Context context,AttributeSet attrs){
        super(context,attrs);
    }

    public MyView(Context context,AttributeSet attrs,int defaultStyle){
        super(context,attrs,defaultStyle);
    }

    protected int degrees;

    public void setDegrees(int degrees){
        this.degrees = degrees+90; //another start coordinate system
        invalidate();

        AccessibilityManager am = (AccessibilityManager)(getContext()
                        .getSystemService(Context.ACCESSIBILITY_SERVICE));
        if (am.isEnabled()) {
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int myHeight = hSpecSize;

        if(hSpecMode == MeasureSpec.EXACTLY){
            myHeight = hSpecSize;
        }else if (hSpecMode == MeasureSpec.AT_MOST){

        }

        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int myWidth = hSpecSize;

        if(wSpecMode == MeasureSpec.EXACTLY){
            myWidth = wSpecSize;
        }else if (wSpecMode == MeasureSpec.AT_MOST){

        }

        setMeasuredDimension(myWidth,myHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        int extRadius = Math.min(width,height)/2-CIRCLE_STROKE_WIDTH;
        int intRadius = extRadius-CIRCLE_STROKE_WIDTH;
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(2);
        canvas.drawCircle(width / 2, height / 2, extRadius, paint);
        paint.setColor(Color.parseColor("#ff64c2f4"));
        canvas.drawCircle(width / 2, height / 2, intRadius, paint);
        paint.setColor(Color.BLACK);
        canvas.drawCircle(width / 2, height / 2, 2, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(20);
        canvas.drawText("N",width/2.f-CIRCLE_STROKE_WIDTH,height/2.f-extRadius+3*CIRCLE_STROKE_WIDTH,paint);
        canvas.drawText("S",width/2.f-CIRCLE_STROKE_WIDTH,height/2.f+extRadius-2*CIRCLE_STROKE_WIDTH,paint);
        canvas.drawText("W",width/2.f-extRadius+2*CIRCLE_STROKE_WIDTH,height/2.f,paint);
        canvas.drawText("E",width/2.f+extRadius-3*CIRCLE_STROKE_WIDTH,height/2.f,paint);

        //degree to radians
        if(degrees != -1) {
            float angle = (float) (degrees * Math.PI / 180);
            //draw direction line
            paint.setColor(Color.RED);
            paint.setStrokeWidth(10);
            canvas.drawLine(width / 2.f, height / 2.f, (float) (width / 2.f + extRadius * Math.cos(angle)), (float) (height / 2.f - extRadius * Math.sin(angle)), paint);
        }
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        event.getText().add(String.valueOf(degrees));
        return true;
    }
}
