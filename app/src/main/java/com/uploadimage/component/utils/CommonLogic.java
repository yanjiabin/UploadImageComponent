package com.uploadimage.component.utils;

import android.graphics.Rect;
import android.view.Gravity;

import java.util.List;

/**
 * Created by EDZ on 2018/10/16.
 */

public class CommonLogic {
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    public CommonLogic() {
    }

    public static void calculateLinesAndChildPosition(List<LineDefinition> lines) {
        int prevLinesThickness = 0;
        int linesCount = lines.size();

        for(int i = 0; i < linesCount; ++i) {
            LineDefinition line = (LineDefinition)lines.get(i);
            line.setLineStartThickness(prevLinesThickness);
            prevLinesThickness += line.getLineThickness();
            int prevChildThickness = 0;
            List<ViewDefinition> childViews = line.getViews();
            int childCount = childViews.size();

            for(int j = 0; j < childCount; ++j) {
                ViewDefinition child = (ViewDefinition)childViews.get(j);
                child.setInlineStartLength(prevChildThickness);
                prevChildThickness += child.getLength() + child.getSpacingLength();
            }
        }

    }

    public static void applyGravityToLines(List<LineDefinition> lines, int realControlLength, int realControlThickness, ConfigDefinition config) {
        int linesCount = lines.size();
        if(linesCount > 0) {
            int totalWeight = linesCount;
            LineDefinition lastLine = (LineDefinition)lines.get(linesCount - 1);
            int excessThickness = realControlThickness - (lastLine.getLineThickness() + lastLine.getLineStartThickness());
            if(excessThickness < 0) {
                excessThickness = 0;
            }

            int excessOffset = 0;

            for(int i = 0; i < linesCount; ++i) {
                LineDefinition child = (LineDefinition)lines.get(i);
                int weight = 1;
                int gravity = getGravity((ViewDefinition)null, config);
                int extraThickness = Math.round((float)(excessThickness * weight / totalWeight));
                int childLength = child.getLineLength();
                int childThickness = child.getLineThickness();
                Rect container = new Rect();
                container.top = excessOffset;
                container.left = 0;
                container.right = realControlLength;
                container.bottom = childThickness + extraThickness + excessOffset;
                Rect result = new Rect();
                Gravity.apply(gravity, childLength, childThickness, container, result);
                excessOffset += extraThickness;
                child.setLineStartLength(child.getLineStartLength() + result.left);
                child.setLineStartThickness(child.getLineStartThickness() + result.top);
                child.setLength(result.width());
                child.setThickness(result.height());
                applyGravityToLine(child, config);
            }

        }
    }

    public static void applyGravityToLine(LineDefinition line, ConfigDefinition config) {
        List<ViewDefinition> views = line.getViews();
        int viewCount = views.size();
        if(viewCount > 0) {
            float totalWeight = 0.0F;

            for(int i = 0; i < viewCount; ++i) {
                ViewDefinition child = (ViewDefinition)views.get(i);
                totalWeight += getWeight(child, config);
            }

            ViewDefinition lastChild = (ViewDefinition)views.get(viewCount - 1);
            int excessLength = line.getLineLength() - (lastChild.getLength() + lastChild.getSpacingLength() + lastChild.getInlineStartLength());
            int excessOffset = 0;

            for(int i = 0; i < viewCount; ++i) {
                ViewDefinition child = (ViewDefinition)views.get(i);
                float weight = getWeight(child, config);
                int gravity = getGravity(child, config);
                int extraLength;
                if(totalWeight == 0.0F) {
                    extraLength = excessLength / viewCount;
                } else {
                    extraLength = Math.round((float)excessLength * weight / totalWeight);
                }

                int childLength = child.getLength() + child.getSpacingLength();
                int childThickness = child.getThickness() + child.getSpacingThickness();
                Rect container = new Rect();
                container.top = 0;
                container.left = excessOffset;
                container.right = childLength + extraLength + excessOffset;
                container.bottom = line.getLineThickness();
                Rect result = new Rect();
                Gravity.apply(gravity, childLength, childThickness, container, result);
                excessOffset += extraLength;
                child.setInlineStartLength(result.left + child.getInlineStartLength());
                child.setInlineStartThickness(result.top);
                child.setLength(result.width() - child.getSpacingLength());
                child.setThickness(result.height() - child.getSpacingThickness());
            }

        }
    }

    public static int findSize(int modeSize, int controlMaxSize, int contentSize) {
        int realControlSize;
        switch(modeSize) {
            case -2147483648:
                realControlSize = Math.min(contentSize, controlMaxSize);
                break;
            case 0:
                realControlSize = contentSize;
                break;
            case 1073741824:
                realControlSize = controlMaxSize;
                break;
            default:
                realControlSize = contentSize;
        }

        return realControlSize;
    }

    private static float getWeight(ViewDefinition child, ConfigDefinition config) {
        return child.weightSpecified()?child.getWeight():config.getWeightDefault();
    }

    private static int getGravity(ViewDefinition child, ConfigDefinition config) {
        int parentGravity = config.getGravity();
        int childGravity;
        if(child != null && child.gravitySpecified()) {
            childGravity = child.getGravity();
        } else {
            childGravity = parentGravity;
        }

        childGravity = getGravityFromRelative(childGravity, config);
        parentGravity = getGravityFromRelative(parentGravity, config);
        if((childGravity & 7) == 0) {
            childGravity |= parentGravity & 7;
        }

        if((childGravity & 112) == 0) {
            childGravity |= parentGravity & 112;
        }

        if((childGravity & 7) == 0) {
            childGravity |= 3;
        }

        if((childGravity & 112) == 0) {
            childGravity |= 48;
        }

        return childGravity;
    }

    public static int getGravityFromRelative(int childGravity, ConfigDefinition config) {
        int ltrGravity;
        if(config.getOrientation() == 1 && (childGravity & 8388608) == 0) {
            ltrGravity = childGravity;
            childGravity = 0;
            childGravity = childGravity | (ltrGravity & 7) >> 0 << 4;
            childGravity |= (ltrGravity & 112) >> 4 << 0;
        }

        if(config.getLayoutDirection() == 1 && (childGravity & 8388608) != 0) {
            ltrGravity = childGravity;
            childGravity = 0;
            childGravity = childGravity | ((ltrGravity & 3) == 3?5:0);
            childGravity |= (ltrGravity & 5) == 5?3:0;
        }

        return childGravity;
    }

    public static void fillLines(List<ViewDefinition> views, List<LineDefinition> lines, ConfigDefinition config) {
        LineDefinition currentLine = new LineDefinition(config);
        lines.add(currentLine);
        int count = views.size();

        for(int i = 0; i < count; ++i) {
            ViewDefinition child = (ViewDefinition)views.get(i);
            boolean newLine = child.isNewLine() || config.isCheckCanFit() && !currentLine.canFit(child);
            if(newLine) {
                currentLine = new LineDefinition(config);
                if(config.getOrientation() == 1 && config.getLayoutDirection() == 1) {
                    lines.add(0, currentLine);
                } else {
                    lines.add(currentLine);
                }
            }

            if(config.getOrientation() == 0 && config.getLayoutDirection() == 1) {
                currentLine.addView(0, child);
            } else {
                currentLine.addView(child);
            }
        }

    }
}
