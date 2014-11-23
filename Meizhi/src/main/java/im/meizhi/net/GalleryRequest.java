package im.meizhi.net;

import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public abstract class GalleryRequest extends Request<List<Album>> {

    public GalleryRequest() {
        super(Method.GET, "http://meizhi.im", null);
    }

    @Override
    protected Response<List<Album>> parseNetworkResponse(NetworkResponse response) {
        try {
            List<Album> albums = new ArrayList<>();

            Document document = Jsoup.parse(new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers)));

            for (Element box : document.select(".container.main .box")) {
                Elements link = box.select("a[href]");
                if (link.isEmpty()) {
                    continue;
                }

                String url = link.attr("href");
                if (!url.startsWith("/m/")) {
                    continue;
                }

                Elements img = link.select("img");
                if (img.isEmpty()) {
                    continue;
                }

                String cover = img.attr("src");
                if (TextUtils.isEmpty(cover)) {
                    continue;
                }

                if (cover.contains("?imageView2")) {
                    cover = cover.substring(0, cover.indexOf("?imageView2"));
                }

                Album album = new Album();
                album.id = url.substring(3);
                album.cover = cover;

                albums.add(album);
            }

            return Response.success(albums, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    public void enqueue(RequestQueue queue) {
        queue.add(this);
    }

}
