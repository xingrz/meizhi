package im.meizhi;

import android.app.Application;
import android.content.Context;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import im.meizhi.net.Album;
import im.meizhi.net.Image;

public class MeizhiApplication extends Application {

    public static MeizhiApplication from(Context context) {
        return (MeizhiApplication) context.getApplicationContext();
    }

    private final List<Album> galleryCache = new ArrayList<>();

    private final LruCache<String, List<Image>> albumsLruCache
            = new LruCache<>(5);

    private RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(this);
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public List<Album> getGalleryCache() {
        return galleryCache;
    }

    public LruCache<String, List<Image>> getAlbumsLruCache() {
        return albumsLruCache;
    }

}
