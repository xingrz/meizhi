package im.meizhi.widget;

import android.support.v7.widget.GridLayoutManager;

public class HeaderSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {

    private final GridLayoutManager layoutManager;

    public HeaderSpanSizeLookup(GridLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public int getSpanSize(int position) {
        return position == 0 ? layoutManager.getSpanCount() : 1;
    }

}
