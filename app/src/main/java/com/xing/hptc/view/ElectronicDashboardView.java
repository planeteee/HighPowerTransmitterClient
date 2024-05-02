package com.xing.hptc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.NonNull;

public class ElectronicDashboardView extends View {
    private  Paint cPaint, mPaint, tPaint,pPaint,iPaint;
    private RectF inRectF,outRectF;
    private int centerX,centerY,startX,startY,stopX,stopY;
    private float currentX,currentY;
    private String string;


    private static final int ROTATION_ANGLE = 3;       // 旋转角度
    private static final int LONG_POINT = 40;          // 长刻度线
    private static final int SHORT_POINT = 20;         // 短刻度线
    private static final int TITLE_TEXT_SIZE = 60;     // 标题文字大小
    private static final int POINT_TEXT_SIZE = 30;     // 刻度文字大小
    private static final int TITLE_STROKE_WIDTH = 1;   // 标题画笔宽度
    private static final int POINT_STROKE_WIDTH = 5;   // 刻度画笔宽度
    private static final int CIRCLE_RADIUS_IN = 200;   // 内圆半径
    private static final int CIRCLE_RADIUS_OUT = 350;  // 外圆半径
    private static final int STAT_ANGLE = 15;          // 开始角度
    private static final int SWEEP_ANGLE = -210;       // 扫过角度

    public ElectronicDashboardView(Context context) {
        super(context);
    }
    private void init() {

//      定义圆画笔
        cPaint = new Paint();
        cPaint.setAntiAlias(true);
        cPaint.setAntiAlias(true);
        cPaint.setStyle(Paint.Style.STROKE);
        cPaint.setColor(0xFF0698DA);
        cPaint.setStrokeCap(Paint.Cap.ROUND);
        cPaint.setStrokeWidth(5);

//      定义刻度画笔
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(0xFF0698DA);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(POINT_STROKE_WIDTH);

//      定义文字画笔
        tPaint = new Paint();
        tPaint.setAntiAlias(true);
        tPaint.setStyle(Paint.Style.FILL);
        tPaint.setColor(Color.BLUE);
        tPaint.setStrokeCap(Paint.Cap.ROUND);
        tPaint.setStrokeWidth(TITLE_STROKE_WIDTH);
        tPaint.setTextAlign(Paint.Align.CENTER);

//      定义指针画笔
        pPaint = new Paint();
        pPaint.setAntiAlias(true);
        pPaint.setStyle(Paint.Style.FILL);
        pPaint.setColor(Color.BLUE);

//      定义内圆画笔
        iPaint = new Paint();
        iPaint.setAntiAlias(true);
        iPaint.setColor(0xFF03DAC5);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        centerX = getMeasuredWidth() / 2;
        centerY = getMeasuredHeight() / 2 + 100;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画圆盘
        onDrawCircle(canvas);
        // 画刻度
        onDrawLine(canvas);
        // 画指针
        onDrawPoint(canvas);
        // 画文字
        onDrawText(canvas);
    }

    private void onDrawCircle(Canvas canvas) {

//      表盘内圆
        inRectF = new RectF(centerX - CIRCLE_RADIUS_IN, centerY - CIRCLE_RADIUS_IN, centerX + CIRCLE_RADIUS_IN, centerY + CIRCLE_RADIUS_IN);
        canvas.drawArc(inRectF, STAT_ANGLE, SWEEP_ANGLE, false, cPaint);

//      表盘外圆
        outRectF = new RectF(centerX - CIRCLE_RADIUS_OUT, centerY - CIRCLE_RADIUS_OUT, centerX + CIRCLE_RADIUS_OUT, centerY + CIRCLE_RADIUS_OUT);
        canvas.drawArc(outRectF, STAT_ANGLE, SWEEP_ANGLE, false, cPaint);

    }
    //绘制刻度线
    private void onDrawLine(Canvas canvas) {
        canvas.save();

        startX = centerX - CIRCLE_RADIUS_OUT;
        startY = centerY;
        stopX = centerX - 330;
        stopY = centerY;

        canvas.rotate(-STAT_ANGLE, centerX, centerY);

        for (int i = 0; i <= 70; i ++) {
            if (i % 10 == 0) {
                canvas.drawLine(startX, startY, stopX + LONG_POINT, stopY, mPaint);
            }
            else if (i % 5 == 0)
                canvas.drawLine(startX, startY, stopX + SHORT_POINT, stopY, mPaint);
            else
                canvas.drawLine(startX, startY, stopX, stopY, mPaint);
            canvas.rotate(ROTATION_ANGLE, centerX, centerY);
        }
        canvas.restore();
    }
    //绘制文本（包括圆盘外面的文本和圆盘内的文本）
    private void onDrawText(Canvas canvas) {

        tPaint.setTextSize(45);
        canvas.drawCircle(centerX, centerY, 120, iPaint);
        canvas.drawText("35.0°C", centerX, centerY, tPaint);

        //刻度文字绘制
        tPaint.setStrokeWidth(TITLE_STROKE_WIDTH);
        tPaint.setTextSize(POINT_TEXT_SIZE);

        int num = 0;
        for (int i = -200; i <= 15; i ++) {

            double distanceX = (CIRCLE_RADIUS_OUT + 30) * Math.cos(i * Math.PI / 180);
            double distanceY = (CIRCLE_RADIUS_OUT + 30) * Math.sin(i * Math.PI / 180);

            if (i % 15 == 0) {
                currentX = (float) (centerX + distanceX);
                currentY = (float) (centerY + distanceY);
                canvas.drawText(String.valueOf(-20 + num * 5), currentX, currentY, tPaint);
                num ++;
            }
        }

        tPaint.setTextSize(TITLE_TEXT_SIZE);
        canvas.drawText(string, centerX, centerY + 100, tPaint);

    }
    //绘制指针
    private void onDrawPoint(Canvas canvas) {

        canvas.save();

        Path path = new Path();

        canvas.rotate(60, centerX, centerY);

        path.moveTo(centerX, centerY - 300);
        path.lineTo(centerX - 10, centerY);
        path.lineTo(centerX + 10, centerY);
        path.lineTo(centerX, centerY - 300);
        path.close();

        canvas.drawPath(path, pPaint);

        canvas.restore();
    }

}
