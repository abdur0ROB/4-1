package com.shadhinsoft.radiomama;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Activity;
import java.io.IOException;


import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;


public class MainActivity extends Activity {


    private static final String TAG = "MainActivity";
    //private ImageButton buttonPlay;
    ProgressDialog progressDialog;

    private ImageButton buttonPlay;
    private boolean buttonStopPlay=true,running=false;
    private MediaPlayer player;

    private Context context;
    private SeekBar mediaPlayer;
    private AudioManager audioManager;
    private Button chatBtn, aboutBtn;
    private InterstitialAd aboutButtonInterstitialAd;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        Log.v(TAG, "" + "onCreate");
        setContentView(R.layout.activity_main);
        LinearLayout ll = (LinearLayout) findViewById(R.id.linear);
        ll.setBackgroundResource(R.drawable.bg_final_final);
//    Ad Initialized code
        MobileAds.initialize(this, getString(R.string.admob_app_id));

        aboutButtonInterstitialAd = new InterstitialAd(this);
        aboutButtonInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen_back_about));
        aboutButtonInterstitialAd.loadAd(new AdRequest.Builder().build());


        initializeMediaPlayer();
        initializeUIElements();
        soundMedia();

        chatBtn = (Button) findViewById(R.id.chat);

        chatBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                startActivity(intent);
            }
        });

        aboutBtn = (Button) findViewById(R.id.about_us);
        aboutBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (aboutButtonInterstitialAd.isLoaded()) {
                    aboutButtonInterstitialAd.show();
                }else {
                    Log.d("TAG", "About_The interstitial wasn't loaded yet.");
                }
                Intent intent = new Intent(MainActivity.this, About.class);
                startActivity(intent);
            }
        });

        aboutButtonInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when when the interstitial ad is closed.
            }
        });


    }

    private void initializeUIElements() {



        buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOnline()) {
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(MainActivity.this);
                    builder.setCancelable(false);
                    builder.setMessage("No internet connection");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                            Intent intent = getIntent();


                            finish();
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivity(intent);
                        }
                    });
                    android.support.v7.app.AlertDialog alertDialog= builder.create();
                    alertDialog.show();


                } else {


                    if (buttonStopPlay) {
                        startPlaying();
                        buttonStopPlay = false;
                        buttonPlay.setImageResource(R.drawable.pause);
                        progressDialog = new ProgressDialog(MainActivity.this);
                        progressDialog.setMessage("Loading..."); // Setting Message
                        progressDialog.setTitle("Please Wait until Radio Play."); // Setting Title
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
                        progressDialog.show(); // Display Progress Dialog
                        progressDialog.setCancelable(false);
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Thread.sleep(7000);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();
                            }
                        }).start();
                    } else {
                        stopPlaying();
                        buttonStopPlay = true;
                        buttonPlay.setImageResource(R.drawable.play);
                        Intent intent = getIntent();
                        finish();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                    }
                }
            }
        });



    }


    private void startPlaying() {

        player.prepareAsync();
        player.setOnPreparedListener(new OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                player.start();
            }
        });

    }

    private void stopPlaying() {
        if (player.isPlaying()) {
            player.stop();
            player.release();
            initializeMediaPlayer();
        }


    }

    private void initializeMediaPlayer() {
        player = new MediaPlayer();
        try {
            Log.v(TAG, "" + "initializeMediaPlayer");
            player.setDataSource("http://redirects.shadhinsoft.com/radio/radio.mp3");
            Log.v("Buffering", "" + "initializeMediaPlayer");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        player.setOnBufferingUpdateListener(new OnBufferingUpdateListener() {

            public void onBufferingUpdate(MediaPlayer mp, int percent) {

                Log.v("Buffering", "" + percent);
            }
        });
    }


    private void soundMedia() {
        mediaPlayer = (SeekBar) findViewById(R.id.seekBar3);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mediaPlayer.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));

        mediaPlayer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    protected boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    protected void onStop() {
        super.onStop();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);

        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
            player = null;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
    @Override
    public void onBackPressed() {
        android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setMessage("Do you want to Exit?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        android.support.v7.app.AlertDialog alertDialog= builder.create();
        alertDialog.show();
    }

}
