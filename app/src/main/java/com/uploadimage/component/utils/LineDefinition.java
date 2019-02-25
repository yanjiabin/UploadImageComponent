package com.uploadimage.component.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by EDZ on 2018/10/16.
 */

public class LineDefinition {
    private final List<ViewDefinition> views = new ArrayList();
    private final ConfigDefinition config;
    private int lineLength;
    private int lineThickness;
    private int lineStartThickness;
    private int lineStartLength;

    public LineDefinition(ConfigDefinition config) {
        this.config = config;
        this.lineStartThickness = 0;
        this.lineStartLength = 0;
    }

    public void addView(ViewDefinition child) {
        this.addView(this.views.size(), child);
    }

    public void addView(int i, ViewDefinition child) {
        this.views.add(i, child);
        this.lineLength = this.lineLength + child.getLength() + child.getSpacingLength();
        this.lineThickness = Math.max(this.lineThickness, child.getThickness() + child.getSpacingThickness());
    }

    public boolean canFit(ViewDefinition child) {
        return this.lineLength + child.getLength() + child.getSpacingLength() <= this.config.getMaxLength();
    }

    public int getLineStartThickness() {
        return this.lineStartThickness;
    }

    public void setLineStartThickness(int lineStartThickness) {
        this.lineStartThickness = lineStartThickness;
    }

    public int getLineThickness() {
        return this.lineThickness;
    }

    public int getLineLength() {
        return this.lineLength;
    }

    public int getLineStartLength() {
        return this.lineStartLength;
    }

    public void setLineStartLength(int lineStartLength) {
        this.lineStartLength = lineStartLength;
    }

    public List<ViewDefinition> getViews() {
        return this.views;
    }

    public void setThickness(int thickness) {
        this.lineThickness = thickness;
    }

    public void setLength(int length) {
        this.lineLength = length;
    }

    public int getX() {
        return this.config.getOrientation() == 0?this.lineStartLength:this.lineStartThickness;
    }

    public int getY() {
        return this.config.getOrientation() == 0?this.lineStartThickness:this.lineStartLength;
    }
}
