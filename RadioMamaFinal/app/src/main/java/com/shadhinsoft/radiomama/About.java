package com.shadhinsoft.radiomama;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;


public class About extends AppCompatActivity {

    Button bt1;
    private AdView adViewTopAbout, getAdViewBottomAbout;
    private InterstitialAd mInterstitialAd;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_about);

        MobileAds.initialize(this, getString(R.string.admob_app_id));
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_full_screen_go_web));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
//
        adViewTopAbout = (AdView) findViewById(R.id.adViewAboutHeader);
        getAdViewBottomAbout = (AdView) findViewById(R.id.adViewAboutFooter);
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewTopAbout.loadAd(adRequest);
        getAdViewBottomAbout.loadAd(adRequest);


        bt1=(Button)findViewById(R.id.textView2);

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }else {
                    Log.d("TAG", "About_The interstitial wasn't loaded yet.");
                }
                Intent i= new Intent(About.this,WebViewPage.class);
                startActivity(i);
            }
        });

        mInterstitialAd.setAdListener(new AdListener() {
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
}
