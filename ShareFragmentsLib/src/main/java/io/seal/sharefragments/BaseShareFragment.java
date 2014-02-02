package io.seal.sharefragments;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public abstract class BaseShareFragment extends Fragment {

    private static final String TAG = "BaseShareFragment";

    public static final String EXTRA_EMAIL = Intent.EXTRA_EMAIL;
    public static final String EXTRA_CC = Intent.EXTRA_BCC;
    public static final String EXTRA_BCC = Intent.EXTRA_CC;
    public static final String EXTRA_STREAM = Intent.EXTRA_STREAM;
    public static final String EXTRA_SUBJECT = Intent.EXTRA_SUBJECT;
    public static final String EXTRA_TITLE = Intent.EXTRA_TITLE;
    public static final String EXTRA_TEXT = Intent.EXTRA_TEXT;

    public static final String EXTRA_HTML_TEXT;

    static {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            EXTRA_HTML_TEXT = Intent.EXTRA_HTML_TEXT;
        } else {
            EXTRA_HTML_TEXT = null;
        }
    }

    private AbsListView mAbsListView;

    protected abstract AbsListView createAdapterView();

    public BaseShareFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mAbsListView = createAdapterView();
        return mAbsListView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager == null) {
            super.onViewCreated(view, savedInstanceState);
            Log.w(TAG, "PackageManager is null!");
            return;
        }
        AppsAdapter adapter = new AppsAdapter(getActivity(), packageManager,
                packageManager.queryIntentActivities(getShareIntent(), 0));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mAbsListView.setAdapter(adapter);
        } else {
            if (mAbsListView instanceof ListView) {
                //noinspection RedundantCast
                ((ListView) mAbsListView).setAdapter(adapter);
            } else if (mAbsListView instanceof GridView) {
                //noinspection RedundantCast
                ((GridView) mAbsListView).setAdapter(adapter);
            } else {
                throw new IllegalArgumentException(
                        "View created in createAdapterView() is not ListView, neither GridView");
            }
        }
        mAbsListView.setOnItemClickListener(mOnItemClickListener);
    }

    private AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ActivityInfo activity = ((ResolveInfo) mAbsListView.getAdapter().getItem(position)).activityInfo;
            ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
            Intent intent = getShareIntent();
            intent.setComponent(name);
            startActivity(intent);
        }
    };

    private class AppsAdapter extends ArrayAdapter<ResolveInfo> {

        private final LayoutInflater mInflater;
        private final PackageManager mPackageManager;

        public AppsAdapter(Context context, PackageManager packageManager, List<ResolveInfo> objects) {
            super(context, android.R.layout.simple_list_item_1, objects);
            mInflater = LayoutInflater.from(context);
            mPackageManager = packageManager;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(getLayoutResId(), null);
            }
            TextView textView = (TextView) convertView.findViewById(R.id.text);
            ImageView imageView = (ImageView) convertView.findViewById(R.id.img);

            ResolveInfo item = getItem(position);

            if (textView != null) {
                textView.setText(item.loadLabel(mPackageManager));
            } else {
                logMissingView("TextView", R.id.text, "label");
            }
            if (imageView != null) {
                Drawable drawable = item.loadIcon(mPackageManager);
                imageView.setImageDrawable(drawable);
            } else {
                logMissingView("ImageView", R.id.img, "icon");
            }
            return convertView;
        }

        private void logMissingView(String viewName, int resId, String itemName) {
            if (BuildConfig.DEBUG) {
                Log.w(TAG, String.format("Missing %s with %s, app %s skipped!", viewName, resId, itemName));
            }
        }

    }

    protected int getLayoutResId() {
        return R.layout.layout_share_item;
    }

    public Intent getShareIntent() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        Bundle arguments = getArguments();
        if (arguments != null) {
            intent.putExtra(Intent.EXTRA_EMAIL, arguments.getString(EXTRA_EMAIL));
            intent.putExtra(Intent.EXTRA_CC, arguments.getString(EXTRA_CC));
            intent.putExtra(Intent.EXTRA_BCC, arguments.getString(EXTRA_BCC));
            intent.putExtra(Intent.EXTRA_STREAM, arguments.getString(EXTRA_STREAM));
            intent.putExtra(Intent.EXTRA_SUBJECT, arguments.getString(EXTRA_SUBJECT));
            intent.putExtra(Intent.EXTRA_TITLE, arguments.getString(EXTRA_TITLE));
            intent.putExtra(Intent.EXTRA_TEXT, arguments.getString(EXTRA_TEXT));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                intent.putExtra(Intent.EXTRA_HTML_TEXT, arguments.getString(EXTRA_HTML_TEXT));
            }
        }
        return intent;
    }
}
