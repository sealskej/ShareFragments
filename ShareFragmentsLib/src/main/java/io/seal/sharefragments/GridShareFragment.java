package io.seal.sharefragments;

import android.widget.AbsListView;
import android.widget.GridView;

public class GridShareFragment extends BaseShareFragment {

    @Override
    protected AbsListView createAdapterView() {
        GridView gridView = new GridView(getActivity());
        gridView.setNumColumns(getNumColumns());
        return gridView;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.layout_grid_share_item;
    }

    protected int getNumColumns() {
        return 2;
    }
}
