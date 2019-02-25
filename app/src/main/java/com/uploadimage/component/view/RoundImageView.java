package com.uploadimage.component.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.uploadimage.component.R;


/**
 * Created by EDZ on 2018/10/16.
 */

public class RoundImageView extends ImageView {
    /**
     * 四个角的弧度
     */
    private float mLeftTopRadius = 4,mLeftBottomRadius=4,mRightBottomRadius=4,mRightTopRadius=4;
    /*圆角的半径，依次为左上角xy半径，右上角，右下角，左下角*/
    private float[] rids =  new float[]{mLeftTopRadius,mLeftTopRadius, mRightTopRadius,mRightTopRadius, mRightBottomRadius,mRightBottomRadius, mLeftBottomRadius,mLeftBottomRadius};
    Path path = new Path();
    RectF rectF=new RectF();
    public RoundImageView(Context context) {
        this(context,null);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView);
        float allRadius=ta.getDimension(R.styleable.RoundImageView_Radius,0);
        mLeftTopRadius = ta.getDimension(R.styleable.RoundImageView_leftTopRadius,allRadius);
        mLeftBottomRadius = ta.getDimension(R.styleable.RoundImageView_leftBottomRadius,allRadius);
        mRightTopRadius = ta.getDimension(R.styleable.RoundImageView_rightTopRadius,allRadius);
        mRightBottomRadius = ta.getDimension(R.styleable.RoundImageView_rightBottomRadius,allRadius);
        rids = new float[]{mLeftTopRadius,mLeftTopRadius, mRightTopRadius,mRightTopRadius, mRightBottomRadius,mRightBottomRadius, mLeftBottomRadius,mLeftBottomRadius};
        ta.recycle();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }

    /**
     * 画图
     *
     * @param canvas
     */
    protected void onDraw(Canvas canvas) {
        int w = this.getWidth();
        int h = this.getHeight();
        rectF.set(0, 0, w, h);
        /*向路径中添加圆角矩形。radii数组定义圆角矩形的四个圆角的x,y半径。radii长度必须为8*/
        path.addRoundRect(rectF, rids, Path.Direction.CW);
        canvas.clipPath(path);
        super.onDraw(canvas);
    }

    public void setLeftTopRadius(float mLeftTopRadius) {
        this.mLeftTopRadius = mLeftTopRadius;
        resetRadius();
    }

    public void setLeftBottomRadius(float mLeftBottomRadius) {
        this.mLeftBottomRadius = mLeftBottomRadius;
        resetRadius();
    }

    public void setRightBottomRadius(float mRightBottomRadius) {
        this.mRightBottomRadius = mRightBottomRadius;
        resetRadius();
    }

    public void setRightTopRadius(float mRightTopRadius) {
        this.mRightTopRadius = mRightTopRadius;
        resetRadius();
    }

    public void setRadius(float radius) {
        setLeftBottomRadius(radius);
        setLeftTopRadius(radius);
        setRightBottomRadius(radius);
        setRightTopRadius(radius);
    }
    void resetRadius(){
        rids = new float[]{mLeftTopRadius,mLeftTopRadius, mRightTopRadius,mRightTopRadius, mRightBottomRadius,mRightBottomRadius, mLeftBottomRadius,mLeftBottomRadius};
    }
}
