package com.androweb.engine.widget.state;

import com.androweb.engine.widget.EmptyStateRecyclerView;

public abstract class AbstractStateDisplay implements EmptyStateRecyclerView.StateDisplay {
    /* Stores padding dimensions (left, top, right, bottom) */
    private final int[] padding = { 0, 0, 0, 0 };


    public void setPadding(int left, int top, int right, int bottom) {
        this.padding[0] = left;
        this.padding[1] = top;
        this.padding[2] = right;
        this.padding[3] = bottom;
    }

    protected final int getPaddingLeft() {
        return padding[0];
    }

    protected final int getPaddingRight() {
        return padding[2];
    }

    protected final int getPaddingTop() {
        return padding[1];
    }

    protected final int getPaddingBottom() {
        return padding[3];
    }
}
