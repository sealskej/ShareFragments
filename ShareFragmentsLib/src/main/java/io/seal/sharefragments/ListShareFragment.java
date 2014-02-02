package io.seal.sharefragments;

import android.widget.AbsListView;
import android.widget.ListView;

public class ListShareFragment extends BaseShareFragment {

    public ListShareFragment() {
    }

    @Override
    protected AbsListView createAdapterView() {
        return new ListView(getActivity());
    }

}
