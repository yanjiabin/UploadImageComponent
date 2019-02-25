package com.uploadimage.component.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;

import com.uploadimage.component.R;
import com.uploadimage.component.utils.CommonLogic;
import com.uploadimage.component.utils.ConfigDefinition;
import com.uploadimage.component.utils.LineDefinition;
import com.uploadimage.component.utils.ViewDefinition;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by EDZ on 2018/10/16.
 */

public class FlowLayout extends ViewGroup {

    private static final int DEFAULT_HORIZONTAL_SPACING = 5;
    private static final int DEFAULT_VERTICAL_SPACING = 5;
    private int mVerticalSpacing;
    private int mHorizontalSpacing;
    private final ConfigDefinition config = new ConfigDefinition();
    List<LineDefinition> lines = new ArrayList();
    List<ViewDefinition> views = new ArrayList();

    public FlowLayout(Context context) {
        super(context);
        this.readStyleParameters(context, (AttributeSet) null);
    }

    public FlowLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.readStyleParameters(context, attributeSet);
    }

    public FlowLayout(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.readStyleParameters(context, attributeSet);
    }

    private void readStyleParameters(Context context, AttributeSet attributeSet) {
        TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.FlowLayout);

        try {
            this.config.setOrientation(a.getInteger(R.styleable.FlowLayout_android_orientation, 0));
            this.config.setDebugDraw(a.getBoolean(R.styleable.FlowLayout_debugDraw, false));
            this.config.setWeightDefault(a.getFloat(R.styleable.FlowLayout_weightDefault, 0.0F));
            this.config.setGravity(a.getInteger(R.styleable.FlowLayout_android_gravity, 0));
            this.config.setLayoutDirection(a.getInteger(R.styleable.FlowLayout_layoutDirection, 0));
        } finally {
            a.recycle();
        }

    }

    public void setHorizontalSpacing(int pixelSize) {
        this.mHorizontalSpacing = pixelSize;
    }

    public void setVerticalSpacing(int pixelSize) {
        this.mVerticalSpacing = pixelSize;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = this.getChildCount();
        this.views.clear();
        this.lines.clear();

        int contentLength;
        for (contentLength = 0; contentLength < count; ++contentLength) {
            View child = this.getChildAt(contentLength);
            if (child.getVisibility() != 8) {
                FlowLayout.LayoutParams lp = (FlowLayout.LayoutParams) child.getLayoutParams();
                child.measure(getChildMeasureSpec(widthMeasureSpec, this.getPaddingLeft() + this.getPaddingRight(), lp.width), getChildMeasureSpec(heightMeasureSpec, this.getPaddingTop() + this.getPaddingBottom(), lp.height));
                if (contentLength != count - 1 && this.mHorizontalSpacing != 0) {
                    lp.rightMargin = this.mHorizontalSpacing;
                }

                if (this.mVerticalSpacing != 0) {
                    lp.topMargin = this.mVerticalSpacing;
                }

                ViewDefinition view = new ViewDefinition(this.config, child);
                view.setWidth(child.getMeasuredWidth());
                view.setHeight(child.getMeasuredHeight());
                view.setNewLine(lp.isNewLine());
                view.setGravity(lp.getGravity());
                view.setWeight(lp.getWeight());
                view.setMargins(lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin);
                this.views.add(view);
            }
        }

        this.config.setMaxWidth(MeasureSpec.getSize(widthMeasureSpec) - this.getPaddingRight() - this.getPaddingLeft());
        this.config.setMaxHeight(MeasureSpec.getSize(heightMeasureSpec) - this.getPaddingTop() - this.getPaddingBottom());
        this.config.setWidthMode(MeasureSpec.getMode(widthMeasureSpec));
        this.config.setHeightMode(MeasureSpec.getMode(heightMeasureSpec));
        this.config.setCheckCanFit(this.config.getLengthMode() != 0);
        CommonLogic.fillLines(this.views, this.lines, this.config);
        CommonLogic.calculateLinesAndChildPosition(this.lines);
        contentLength = 0;
        int linesCount = this.lines.size();

        for (int i = 0; i < linesCount; ++i) {
            LineDefinition l = (LineDefinition) this.lines.get(i);
            contentLength = Math.max(contentLength, l.getLineLength());
        }

        LineDefinition currentLine = (LineDefinition) this.lines.get(this.lines.size() - 1);
        int contentThickness = currentLine.getLineStartThickness() + currentLine.getLineThickness();
        int realControlLength = CommonLogic.findSize(this.config.getLengthMode(), this.config.getMaxLength(), contentLength);
        int realControlThickness = CommonLogic.findSize(this.config.getThicknessMode(), this.config.getMaxThickness(), contentThickness);
        CommonLogic.applyGravityToLines(this.lines, realControlLength, realControlThickness, this.config);
        int totalControlWidth;
        for (totalControlWidth = 0; totalControlWidth < linesCount; ++totalControlWidth) {
            LineDefinition line = (LineDefinition) this.lines.get(totalControlWidth);
            this.applyPositionsToViews(line);
        }
        totalControlWidth = this.getPaddingLeft() + this.getPaddingRight();
        int totalControlHeight = this.getPaddingBottom() + this.getPaddingTop();
        if (this.config.getOrientation() == 0) {
            totalControlWidth += contentLength;
            totalControlHeight += contentThickness;
        } else {
            totalControlWidth += contentThickness;
            totalControlHeight += contentLength;
        }

        this.setMeasuredDimension(resolveSize(totalControlWidth, widthMeasureSpec), resolveSize(totalControlHeight, heightMeasureSpec));
    }

    private void applyPositionsToViews(LineDefinition line) {
        List<ViewDefinition> childViews = line.getViews();
        int childCount = childViews.size();

        for (int i = 0; i < childCount; ++i) {
            ViewDefinition child = (ViewDefinition) childViews.get(i);
            View view = child.getView();
            view.measure(MeasureSpec.makeMeasureSpec(child.getWidth(), 1073741824), MeasureSpec.makeMeasureSpec(child.getHeight(), 1073741824));
        }

    }

    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int linesCount = this.lines.size();

        for (int i = 0; i < linesCount; ++i) {
            LineDefinition line = (LineDefinition) this.lines.get(i);
            int count = line.getViews().size();

            for (int j = 0; j < count; ++j) {
                ViewDefinition child = (ViewDefinition) line.getViews().get(j);
                View view = child.getView();
                FlowLayout.LayoutParams lp = (FlowLayout.LayoutParams) view.getLayoutParams();
                view.layout(this.getPaddingLeft() + line.getX() + child.getInlineX() + lp.leftMargin, this.getPaddingTop() + line.getY() + child.getInlineY() + lp.topMargin, this.getPaddingLeft() + line.getX() + child.getInlineX() + lp.leftMargin + child.getWidth(), this.getPaddingTop() + line.getY() + child.getInlineY() + lp.topMargin + child.getHeight());
            }
        }

    }

    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean more = super.drawChild(canvas, child, drawingTime);
        this.drawDebugInfo(canvas, child);
        return more;
    }

    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof FlowLayout.LayoutParams;
    }

    protected FlowLayout.LayoutParams generateDefaultLayoutParams() {
        return new FlowLayout.LayoutParams(-2, -2);
    }

    public FlowLayout.LayoutParams generateLayoutParams(AttributeSet attributeSet) {
        return new FlowLayout.LayoutParams(this.getContext(), attributeSet);
    }

    protected FlowLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new FlowLayout.LayoutParams(p);
    }

    private void drawDebugInfo(Canvas canvas, View child) {
        if (this.isDebugDraw()) {
            Paint childPaint = this.createPaint(-256);
            Paint newLinePaint = this.createPaint(-65536);
            FlowLayout.LayoutParams lp = (FlowLayout.LayoutParams) child.getLayoutParams();
            float x;
            float y;
            if (lp.rightMargin > 0) {
                x = (float) child.getRight();
                y = (float) child.getTop() + (float) child.getHeight() / 2.0F;
                canvas.drawLine(x, y, x + (float) lp.rightMargin, y, childPaint);
                canvas.drawLine(x + (float) lp.rightMargin - 4.0F, y - 4.0F, x + (float) lp.rightMargin, y, childPaint);
                canvas.drawLine(x + (float) lp.rightMargin - 4.0F, y + 4.0F, x + (float) lp.rightMargin, y, childPaint);
            }

            if (lp.leftMargin > 0) {
                x = (float) child.getLeft();
                y = (float) child.getTop() + (float) child.getHeight() / 2.0F;
                canvas.drawLine(x, y, x - (float) lp.leftMargin, y, childPaint);
                canvas.drawLine(x - (float) lp.leftMargin + 4.0F, y - 4.0F, x - (float) lp.leftMargin, y, childPaint);
                canvas.drawLine(x - (float) lp.leftMargin + 4.0F, y + 4.0F, x - (float) lp.leftMargin, y, childPaint);
            }

            if (lp.bottomMargin > 0) {
                x = (float) child.getLeft() + (float) child.getWidth() / 2.0F;
                y = (float) child.getBottom();
                canvas.drawLine(x, y, x, y + (float) lp.bottomMargin, childPaint);
                canvas.drawLine(x - 4.0F, y + (float) lp.bottomMargin - 4.0F, x, y + (float) lp.bottomMargin, childPaint);
                canvas.drawLine(x + 4.0F, y + (float) lp.bottomMargin - 4.0F, x, y + (float) lp.bottomMargin, childPaint);
            }

            if (lp.topMargin > 0) {
                x = (float) child.getLeft() + (float) child.getWidth() / 2.0F;
                y = (float) child.getTop();
                canvas.drawLine(x, y, x, y - (float) lp.topMargin, childPaint);
                canvas.drawLine(x - 4.0F, y - (float) lp.topMargin + 4.0F, x, y - (float) lp.topMargin, childPaint);
                canvas.drawLine(x + 4.0F, y - (float) lp.topMargin + 4.0F, x, y - (float) lp.topMargin, childPaint);
            }

            if (lp.isNewLine()) {
                if (this.config.getOrientation() == 0) {
                    x = (float) child.getLeft();
                    y = (float) child.getTop() + (float) child.getHeight() / 2.0F;
                    canvas.drawLine(x, y - 6.0F, x, y + 6.0F, newLinePaint);
                } else {
                    x = (float) child.getLeft() + (float) child.getWidth() / 2.0F;
                    y = (float) child.getTop();
                    canvas.drawLine(x - 6.0F, y, x + 6.0F, y, newLinePaint);
                }
            }

        }
    }

    private Paint createPaint(int color) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(color);
        paint.setStrokeWidth(2.0F);
        return paint;
    }

    public int getOrientation() {
        return this.config.getOrientation();
    }

    public void setOrientation(int orientation) {
        this.config.setOrientation(orientation);
        this.requestLayout();
    }

    public boolean isDebugDraw() {
        return this.config.isDebugDraw() || this.debugDraw();
    }

    public void setDebugDraw(boolean debugDraw) {
        this.config.setDebugDraw(debugDraw);
        this.invalidate();
    }

    private boolean debugDraw() {
        try {
            Method m = ViewGroup.class.getDeclaredMethod("debugDraw", (Class[]) null);
            m.setAccessible(true);
            return ((Boolean) m.invoke(this, new Object[]{null})).booleanValue();
        } catch (Exception var2) {
            return false;
        }
    }

    public float getWeightDefault() {
        return this.config.getWeightDefault();
    }

    public void setWeightDefault(float weightDefault) {
        this.config.setWeightDefault(weightDefault);
        this.requestLayout();
    }

    public int getGravity() {
        return this.config.getGravity();
    }

    public void setGravity(int gravity) {
        this.config.setGravity(gravity);
        this.requestLayout();
    }

    public int getLayoutDirection() {
        return this.config == null ? 0 : this.config.getLayoutDirection();
    }

    public void setLayoutDirection(int layoutDirection) {
        this.config.setLayoutDirection(layoutDirection);
        this.requestLayout();
    }

    public static class LayoutParams extends MarginLayoutParams {
        @ViewDebug.ExportedProperty(
                mapping = {@ViewDebug.IntToString(
                        from = 0,
                        to = "NONE"
                ), @ViewDebug.IntToString(
                        from = 48,
                        to = "TOP"
                ), @ViewDebug.IntToString(
                        from = 80,
                        to = "BOTTOM"
                ), @ViewDebug.IntToString(
                        from = 3,
                        to = "LEFT"
                ), @ViewDebug.IntToString(
                        from = 5,
                        to = "RIGHT"
                ), @ViewDebug.IntToString(
                        from = 16,
                        to = "CENTER_VERTICAL"
                ), @ViewDebug.IntToString(
                        from = 112,
                        to = "FILL_VERTICAL"
                ), @ViewDebug.IntToString(
                        from = 1,
                        to = "CENTER_HORIZONTAL"
                ), @ViewDebug.IntToString(
                        from = 7,
                        to = "FILL_HORIZONTAL"
                ), @ViewDebug.IntToString(
                        from = 17,
                        to = "CENTER"
                ), @ViewDebug.IntToString(
                        from = 119,
                        to = "FILL"
                )}
        )
        private boolean newLine = false;
        private int gravity = 0;
        private float weight = -1.0F;

        public LayoutParams(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            this.readStyleParameters(context, attributeSet);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams layoutParams) {
            super(layoutParams);
        }

        private void readStyleParameters(Context context, AttributeSet attributeSet) {
            TypedArray a = context.obtainStyledAttributes(attributeSet, R.styleable.FlowLayout_LayoutParams);

            try {
                this.newLine = a.getBoolean(R.styleable.FlowLayout_LayoutParams_layout_newLine, false);
                this.gravity = a.getInt(R.styleable.FlowLayout_LayoutParams_android_layout_gravity, 0);
                this.weight = a.getFloat(R.styleable.FlowLayout_LayoutParams_layout_weight, -1.0F);
            } finally {
                a.recycle();
            }

        }

        public int getGravity() {
            return this.gravity;
        }

        public void setGravity(int gravity) {
            this.gravity = gravity;
        }

        public float getWeight() {
            return this.weight;
        }

        public void setWeight(float weight) {
            this.weight = weight;
        }

        public boolean isNewLine() {
            return this.newLine;
        }

        public void setNewLine(boolean newLine) {
            this.newLine = newLine;
        }
    }

}
