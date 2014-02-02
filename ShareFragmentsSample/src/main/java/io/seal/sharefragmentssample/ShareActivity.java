package io.seal.sharefragmentssample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import io.seal.sharefragments.BaseShareFragment;
import io.seal.sharefragments.GridShareFragment;
import io.seal.sharefragments.ListShareFragment;

public class ShareActivity extends ActionBarActivity {

    private static final String TAG = "ShareActivity";

    public static final String EXTRA_TYPE = "io.seal.sharefragmentssample.intent.extra.EXTRA_TYPE";

    public static final int LIST = 0;
    public static final int GRID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        int type = getIntent().getIntExtra(EXTRA_TYPE, LIST);
        if (savedInstanceState == null) {
            BaseShareFragment shareFragment = type == LIST ? new ListShareFragment() : new GridShareFragment();

            Bundle bundle = new Bundle();
            bundle.putString(BaseShareFragment.EXTRA_SUBJECT, "subject");
            bundle.putString(BaseShareFragment.EXTRA_TITLE, "title");
            bundle.putString(BaseShareFragment.EXTRA_TEXT, "text");
            shareFragment.setArguments(bundle);

            getSupportFragmentManager().beginTransaction().add(R.id.container, shareFragment).commit();
        }

        setTitle(type == LIST ? R.string.list_share : R.string.grid_share);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

}
