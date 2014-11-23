package im.meizhi;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import im.meizhi.net.Album;
import im.meizhi.widget.HeaderListRecyclerAdapter;

public class GalleryAdapter extends HeaderListRecyclerAdapter<Album, GalleryAdapter.ItemViewHolder> {

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView cover;

        public ItemViewHolder(View itemView) {
            super(itemView);
            cover = (ImageView) itemView.findViewById(R.id.cover);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                final View ripple = itemView.findViewById(R.id.ripple);
                ripple.setOnTouchListener(new View.OnTouchListener() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        ripple.getBackground().setHotspot(event.getX(), event.getY());
                        return false;
                    }
                });
            }
        }
    }

    public static interface OnAlbumItemClickListener {
        public void onAlbumItemClick(int position);
    }

    private final Context context;
    private final OnAlbumItemClickListener listener;

    public GalleryAdapter(Context context, OnAlbumItemClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ItemViewHolder onCreateItemViewHolder(ViewGroup parent) {
        return new ItemViewHolder(inflate(R.layout.item_album, parent));
    }

    @Override
    public void onBindItemViewHolder(ItemViewHolder holder, final int position) {
        Album item = getItem(position);

        Picasso picasso = Picasso.with(context);
        picasso.setLoggingEnabled(true);
        picasso.load(item.cover + "?imageView2/1/w/540/h/540").into(holder.cover);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAlbumItemClick(position);
            }
        });
    }

}
