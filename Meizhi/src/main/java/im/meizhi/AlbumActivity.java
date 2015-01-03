package im.meizhi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import java.util.List;

import im.meizhi.net.Album;

public class AlbumActivity extends ActionBarActivity {

    private ViewPager pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_album);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final List<Album> albums = MeizhiApplication.from(this).getGalleryCache();

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Bundle arguments = new Bundle();
                arguments.putString("id", albums.get(position).id);

                Fragment fragment = new AlbumFragment();
                fragment.setArguments(arguments);

                return fragment;
            }

            @Override
            public int getCount() {
                return albums.size();
            }
        });
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                ActionBar actionBar = getSupportActionBar();
                if (!actionBar.isShowing()) {
                    actionBar.show();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        pager.setCurrentItem(getIntent().getIntExtra("index", 0), false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportActionBar().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void finish() {
        setResult(RESULT_OK, new Intent().putExtra("index", pager.getCurrentItem()));
        super.finish();
    }

}
