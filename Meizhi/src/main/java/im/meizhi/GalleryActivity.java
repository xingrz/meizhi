package im.meizhi;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;

import com.android.volley.VolleyError;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.util.List;

import im.meizhi.net.Album;
import im.meizhi.net.GalleryRequest;
import im.meizhi.widget.HeaderSpanSizeLookup;

public class GalleryActivity extends ActionBarActivity implements
        SwipeRefreshLayout.OnRefreshListener,
        GalleryAdapter.OnAlbumItemClickListener,
        ObservableScrollViewCallbacks {

    private static final String TAG = "GalleryActivity";

    private static final int DEFAULT_CIRCLE_TARGET = 64;

    private static final int REQUEST_ALBUM = 1;

    private MeizhiApplication application;

    private SwipeRefreshLayout refresh;

    private GalleryAdapter adapter;
    private GridLayoutManager layoutManager;
    private ObservableRecyclerView albums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        application = MeizhiApplication.from(this);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int actionBarHeight = getSupportActionBar().getHeight();
        int swipeEndTarget = actionBarHeight + (int) (DEFAULT_CIRCLE_TARGET * metrics.density);

        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        refresh.setColorSchemeResources(R.color.accent);
        refresh.setProgressViewOffset(false, actionBarHeight, swipeEndTarget);
        refresh.setOnRefreshListener(this);

        adapter = new GalleryAdapter(this, this);

        layoutManager = new GridLayoutManager(this, getSpanCount());
        layoutManager.setSpanSizeLookup(new HeaderSpanSizeLookup(layoutManager));

        albums = (ObservableRecyclerView) findViewById(R.id.albums);
        albums.setHasFixedSize(true);
        albums.setLayoutManager(layoutManager);
        albums.setAdapter(adapter);
        albums.setScrollViewCallbacks(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!application.getGalleryCache().isEmpty()) {
            adapter.setList(application.getGalleryCache());
        } else {
            onRefresh();
        }

        getSupportActionBar().show();
    }

    @Override
    public void onRefresh() {
        refresh.setRefreshing(true);
        new GalleryRequest() {
            @Override
            protected void deliverResponse(List<Album> response) {
                Log.d(TAG, "total: " + response.size());
                refresh.setRefreshing(false);
                adapter.setList(response);
                application.getGalleryCache().addAll(response);
            }

            @Override
            public void deliverError(VolleyError error) {
                refresh.setRefreshing(false);
                Log.e(TAG, "error", error);
            }
        }.enqueue(application.getRequestQueue());
    }

    @Override
    public void onAlbumItemClick(int position) {
        Intent photosIntent = new Intent(this, AlbumActivity.class);
        photosIntent.putExtra("index", position);
        startActivityForResult(photosIntent, REQUEST_ALBUM);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ALBUM:
                albums.scrollToPosition(data.getIntExtra("index", 0));
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        layoutManager.setSpanCount(getSpanCount());
    }

    private int getSpanCount() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return 3;
        } else {
            return 2;
        }
    }

    @Override
    public void onScrollChanged(int i, boolean b, boolean b2) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        ActionBar actionBar = getSupportActionBar();
        if (scrollState == ScrollState.UP) {
            if (actionBar.isShowing()) {
                actionBar.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!actionBar.isShowing()) {
                actionBar.show();
            }
        }
    }

}
