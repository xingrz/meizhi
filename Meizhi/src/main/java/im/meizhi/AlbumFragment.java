package im.meizhi;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.VolleyError;
import com.github.ksoichiro.android.observablescrollview.ObservableRecyclerView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;

import java.util.List;

import im.meizhi.net.AlbumRequest;
import im.meizhi.net.Image;
import im.meizhi.widget.HeaderSpanSizeLookup;

public class AlbumFragment extends Fragment implements
        AlbumAdapter.OnPhotoItemClickListener,
        ObservableScrollViewCallbacks {

    private static final String TAG = "PhotosFragment";

    private static final int REQUEST_VIEW = 1;

    private MeizhiApplication application;

    private AlbumActivity activity;
    private GridLayoutManager layoutManager;
    private ObservableRecyclerView photos;

    private String albumId;
    private AlbumAdapter adapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (AlbumActivity) activity;

        application = MeizhiApplication.from(activity);
        adapter = new AlbumAdapter(activity, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photos, container, false);

        layoutManager = new GridLayoutManager(activity, getSpanCount());
        layoutManager.setSpanSizeLookup(new HeaderSpanSizeLookup(layoutManager));

        photos = (ObservableRecyclerView) rootView.findViewById(R.id.photos);
        photos.setHasFixedSize(true);
        photos.setLayoutManager(layoutManager);
        photos.setAdapter(adapter);
        photos.setScrollViewCallbacks(this);

        albumId = getArguments().getString("id");

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        List<Image> cache = application.getAlbumsLruCache().get(albumId);
        if (cache != null) {
            adapter.setList(cache);
        } else {
            new AlbumRequest(albumId) {
                @Override
                protected void deliverResponse(List<Image> response) {
                    Log.d(TAG, "total: " + response.size());
                    adapter.setList(response);
                    application.getAlbumsLruCache().put(albumId, response);
                }

                @Override
                public void deliverError(VolleyError error) {
                    Log.e(TAG, "error", error);
                }
            }.enqueue(application.getRequestQueue());
        }
    }

    @Override
    public void onPhotoItemClick(int position) {
        Intent viewerIntent = new Intent(activity, ViewerActivity.class);
        viewerIntent.putExtra("album", albumId);
        viewerIntent.putExtra("index", position);
        startActivityForResult(viewerIntent, REQUEST_VIEW);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_VIEW:
                photos.scrollToPosition(data.getIntExtra("index", 0));
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
            return 5;
        } else {
            return 3;
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
        ActionBar actionBar = activity.getSupportActionBar();
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
