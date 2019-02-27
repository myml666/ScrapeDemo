package com.itfitness.scrapedemo.widget;


import android.content.Context;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @ProjectName: ScrapeDemo
 * @Package: com.itfitness.scrapedemo.widget
 * @ClassName: ScrapeView
 * @Description: java类作用描述 ：
 * @Author: 作者名：lml
 * @CreateDate: 2019/2/27 12:44
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/2/27 12:44
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */

public class ScrapeView extends View {
    private Paint mPaint;
    private Path mPath;
    private PointF mPointF;
    public ScrapeView(Context context) {
        super(context);
        init();
    }

    public ScrapeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrapeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        setLayerType(LAYER_TYPE_SOFTWARE,null);//关闭硬件加速

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(20);

        mPath = new Path();

        mPointF = new PointF();
    }
    /**
     * 修改高度
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec),measuredHeight(heightMeasureSpec));
    }

    /**
     * 测量宽
     * @param widthMeasureSpec
     */
    private int measureWidth(int widthMeasureSpec) {
        int result ;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else {
            result = 200;
            if (specMode == MeasureSpec.AT_MOST){
                result = Math.min(result,specSize);
            }
        }
        return result;
    }

    /**
     * 测量高
     * @param heightMeasureSpec
     */
    private int measuredHeight(int heightMeasureSpec) {
        int result ;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY){
            result = specSize;
        }else{
            result = 200;
            if(specMode == MeasureSpec.AT_MOST){
                result = Math.min(result,specSize);
            }
        }
        return  result;
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mPath.reset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mPointF.x = event.getX();
                mPointF.y = event.getY();
                mPath.moveTo(event.getX(),event.getY());
                invalidate();
                return true;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                mPath.quadTo(mPointF.x,mPointF.y,(mPointF.x+event.getX())/2,(mPointF.y+event.getY())/2);//贝赛尔曲线让路径更圆润
                mPointF.x = event.getX();
                mPointF.y = event.getY();
                //下面这行代码依然可以实现效果（只是不够圆润）
//                mPath.lineTo(event.getX(),event.getY());
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();//存储画布状态(离屏绘制)
        mPaint.setColor(Color.GRAY);
        mPaint.setMaskFilter(new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL));//给卡片周围加上柔光效果
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(new RectF(0,0,getWidth(),getHeight()),mPaint);
        mPaint.setMaskFilter(null);//清空MaskFilter，否则刮卡时也是模糊的效果
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));//利用混合模式将手指移动的区域（mPath的路径）清空
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(mPath,mPaint);
        mPaint.setXfermode(null);//清除混合模式
        canvas.restore();//恢复上面存储的画布状态
    }
}
