package com.example.jordan.meeting.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;

/* The idea of this ListView hack has been found on Stackoverflow here :
 * https://stackoverflow.com/questions/18353515/how-to-make-multiplelistview-in-scrollview/18354096#18354096
 */
public class UnrolledListView extends ListView
{
    public UnrolledListView(Context context)
    {
        super(context);
    }

    public UnrolledListView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public UnrolledListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        /* Calculate entire height by providing a very large height hint.
        But do not use the highest 2 bits of this integer; those are
        reserved for the MeasureSpec mode. */
        int unrollSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, unrollSpec);

        ViewGroup.LayoutParams params = getLayoutParams();
        params.height = getMeasuredHeight();
    }
}