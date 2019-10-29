package com.prasanth.lastmile.utils;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VerticalItemDecorator extends RecyclerView.ItemDecoration {

    private final int mVerticalSpaceHeight;

    public VerticalItemDecorator(int verticalSpaceHeight) {
        this.mVerticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {

        outRect.top = mVerticalSpaceHeight;
    }
}
