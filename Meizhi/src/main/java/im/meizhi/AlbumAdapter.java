package im.meizhi;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import im.meizhi.net.Image;
import im.meizhi.widget.HeaderListRecyclerAdapter;

public class AlbumAdapter extends HeaderListRecyclerAdapter<Image, AlbumAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView thumb;

        public ViewHolder(View itemView) {
            super(itemView);
            thumb = (ImageView) itemView.findViewById(R.id.thumb);

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

    public static interface OnPhotoItemClickListener {
        public void onPhotoItemClick(int position);
    }

    private final Context context;
    private final OnPhotoItemClickListener listener;

    public AlbumAdapter(Context context, OnPhotoItemClickListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateItemViewHolder(ViewGroup parent) {
        return new ViewHolder(inflate(R.layout.item_photo, parent));
    }

    @Override
    public void onBindItemViewHolder(ViewHolder holder, final int position) {
        Image item = getItem(position);

        Picasso picasso = Picasso.with(context);
        picasso.setLoggingEnabled(true);
        picasso.load(item.url + "?imageView2/1/w/360/h/360").into(holder.thumb);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onPhotoItemClick(position);
            }
        });
    }

}
