package im.meizhi;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;
import java.util.concurrent.TimeUnit;

import im.meizhi.net.Image;

public class ViewerActivity extends ActionBarActivity {

    private List<Image> photos;

    private ViewPager pager;

    private MenuItem play;
    private MenuItem stop;

    private final Handler player = new Handler(Looper.getMainLooper());
    private final Runnable program = new Runnable() {
        @Override
        public void run() {
            int index = pager.getCurrentItem();
            index = (index == photos.size() - 1) ? 0 : index + 1;

            pager.setCurrentItem(index);
            player.postDelayed(this, TimeUnit.SECONDS.toMillis(5));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_viewer);

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        actionBar.setDisplayHomeAsUpEnabled(true);

        String albumId = getIntent().getStringExtra("album");
        photos = MeizhiApplication.from(this).getAlbumsLruCache().get(albumId);

        pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                Bundle arguments = new Bundle();
                arguments.putString("url", photos.get(i).url);

                Fragment viewer = new ViewerFragment();
                viewer.setArguments(arguments);

                return viewer;
            }

            @Override
            public int getCount() {
                return photos.size();
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return String.format("%s / %s", position + 1, getCount());
            }
        });
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setTitle(String.format("%s / %s", position + 1, photos.size()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_DRAGGING && actionBar.isShowing()) {
                    actionBar.hide();
                }
            }
        });
        pager.setCurrentItem(getIntent().getIntExtra("index", 0), false);

        setTitle(pager.getAdapter().getPageTitle(pager.getCurrentItem()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.viewer, menu);
        play = menu.findItem(R.id.play);
        stop = menu.findItem(R.id.stop);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.play:
                play();
                return true;
            case R.id.stop:
                stop();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void play() {
        player.postDelayed(program, TimeUnit.SECONDS.toMillis(5));
        play.setVisible(false);
        stop.setVisible(true);
    }

    private void stop() {
        player.removeCallbacksAndMessages(null);
        stop.setVisible(false);
        play.setVisible(true);
    }

    @Override
    protected void onPause() {
        stop();
        super.onPause();
    }

    @Override
    public void finish() {
        setResult(RESULT_OK, new Intent().putExtra("index", pager.getCurrentItem()));
        super.finish();
    }

}
