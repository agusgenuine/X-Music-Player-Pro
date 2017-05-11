package com.riseapps.xmusic.view.Activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.riseapps.xmusic.R;
import com.riseapps.xmusic.component.SharedPreferenceSingelton;
import com.riseapps.xmusic.executor.MyApplication;
import com.riseapps.xmusic.executor.UpdateSongs;
import com.riseapps.xmusic.model.Pojo.Album;
import com.riseapps.xmusic.model.Pojo.Artist;
import com.riseapps.xmusic.model.Pojo.Song;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Walkthrough extends AppCompatActivity {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private int[] layouts;
    private Button btn1,btn2,btn3;
    private RelativeLayout btnSkip;
    private LinearLayout loading;

    Async async = new Async();
    final int textLimit = 26;
    private static final int REQUEST_PERMISSION = 0;

    ArrayList<Song> songList = new ArrayList<>();
    ArrayList<Album> albumList = new ArrayList<>();
    ArrayList<Artist> artistList = new ArrayList<>();
    private Intent intent;
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_walkthrough);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        btnSkip = (RelativeLayout) findViewById(R.id.btn_skip);
        btn1= (Button) findViewById(R.id.bt1);
        btn2= (Button) findViewById(R.id.bt2);
        btn3= (Button) findViewById(R.id.bt3);


        loading= (LinearLayout) findViewById(R.id.loading);

        layouts = new int[]{
                R.layout.walkthrough1,
                R.layout.walkthrough2,
                R.layout.walkthrough3};

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(async.getStatus()== AsyncTask.Status.FINISHED){
                    launchHomeScreen();
                }
                else {
                    if(loading.getVisibility()==View.GONE)
                    loading.setVisibility(View.VISIBLE);
                }

            }
        });

        checkPermission();


    }


    private void launchHomeScreen() {
        //if (async.getStatus() == AsyncTask.Status.FINISHED) {
            if(songList.size()==0){
                openEmptyStateDialog();
            }
            else{
                new SharedPreferenceSingelton().saveAs(Walkthrough.this, "opened_before", true);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }

        /*} else {
            Snackbar.make(viewPager, R.string.fetched,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(songList.size()==0){
                                openEmptyStateDialog();
                            }
                            else{
                                new SharedPreferenceSingelton().saveAs(Walkthrough.this, "opened_before", true);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                finish();
                            }
                        }
                    }).show();
        }*/

    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            if (position == layouts.length - 1) {
                btn3.setBackground(getResources().getDrawable(R.drawable.walkthrough_selected));
                btn2.setBackground(getResources().getDrawable(R.drawable.walkthrough_unselected));
            }
            else if(position==1){
                // still pages are left
                btn1.setBackground(getResources().getDrawable(R.drawable.walkthrough_unselected));
                btn3.setBackground(getResources().getDrawable(R.drawable.walkthrough_unselected));
                btn2.setBackground(getResources().getDrawable(R.drawable.walkthrough_selected));

            }
            else {
                btn1.setBackground(getResources().getDrawable(R.drawable.walkthrough_selected));
                btn2.setBackground(getResources().getDrawable(R.drawable.walkthrough_unselected));
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    public void checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Snackbar.make(findViewById(android.R.id.content),
                            "Permission needed to Access Songs",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(Walkthrough.this,
                                            new String[]{Manifest.permission
                                                    .WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                            REQUEST_PERMISSION);
                                }
                            }).show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
                }
            } else {
                async.execute();
            }
        } else {
            async.execute();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //updateSongs.fetchSongs();
                    async.execute();
                } else {
                    Snackbar.make(viewPager, R.string.permission_rationale,
                            Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.ok, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    ActivityCompat.requestPermissions(Walkthrough.this,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            REQUEST_PERMISSION);
                                }
                            }).show();
                }
                break;
        }
    }

    private class Async extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            new UpdateSongs(Walkthrough.this).getSongList();
            songList = new MyApplication(Walkthrough.this).getWritableDatabase().readSongs();
            artistList = new MyApplication(Walkthrough.this).getWritableDatabase().readArtists();
            albumList = new MyApplication(Walkthrough.this).getWritableDatabase().readAlbums();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            intent = new Intent(Walkthrough.this, MainActivity.class);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Song>>() {
            }.getType();
            String songJson = gson.toJson(songList, type);
            if(songList.size()>6) {
                String songSubJson = gson.toJson(songList.subList(0, 6), type);
                intent.putExtra("songSubList", songSubJson);
            }

            type = new TypeToken<ArrayList<Album>>() {
            }.getType();
            String albumJson = gson.toJson(albumList, type);

            type = new TypeToken<ArrayList<Artist>>() {
            }.getType();
            String artistJson = gson.toJson(artistList, type);

            intent.putExtra("songList", songJson);
            intent.putExtra("albumList", albumJson);
            intent.putExtra("artistList", artistJson);

            if(loading.getVisibility()==View.VISIBLE)
                launchHomeScreen();
        }
    }

    public void openEmptyStateDialog(){
        dialog=new Dialog(this);
        dialog.setContentView(R.layout.empty_state_dialog);
        dialog.show();

    }
}