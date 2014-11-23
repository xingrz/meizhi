package im.meizhi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ortiz.touch.TouchImageView;
import com.squareup.picasso.Picasso;

public class ViewerFragment extends Fragment {

    private ViewerActivity activity;
    private TouchImageView image;

    private String url;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ViewerActivity) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_viewer, container, false);

        final ActionBar actionBar = activity.getSupportActionBar();

        image = (TouchImageView) rootView.findViewById(R.id.image);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (actionBar.isShowing()) {
                    actionBar.hide();
                } else {
                    actionBar.show();
                }
            }
        });

        url = getArguments().getString("url");

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Picasso picasso = Picasso.with(activity);
        picasso.setLoggingEnabled(true);
        picasso.load(url).into(image);
    }

}
