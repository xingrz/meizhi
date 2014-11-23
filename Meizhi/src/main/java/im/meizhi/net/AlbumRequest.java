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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public abstract class AlbumRequest extends Request<List<Image>> {

    public AlbumRequest(String id) {
        super(Method.GET, String.format("http://meizhi.im/m/%s", id), null);
    }

    @Override
    protected Response<List<Image>> parseNetworkResponse(NetworkResponse response) {
        try {
            List<Image> images = new ArrayList<>();

            Document document = Jsoup.parse(new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers)));

            for (Element img : document.select(".container.main .box.show-box img")) {
                String url = img.attr("src");
                if (TextUtils.isEmpty(url)) {
                    continue;
                }

                Image image = new Image();
                image.url = url;

                images.add(image);
            }

            return Response.success(images, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }

    public void enqueue(RequestQueue queue) {
        queue.add(this);
    }

}
