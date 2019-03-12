package com.granbyte.gasto_pouco;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

import entidade.Usuario;
import entidade.Utils;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private Animation animation;
    private ImageView image;
    private VideoView mVideoView;

    private Context mCtx;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mCtx = this;

        //basic config
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        //background video
        mVideoView = findViewById(R.id.bgVideoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.market);
        mVideoView.setVideoURI(uri);
        mVideoView.start();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });



        progressBar = findViewById(R.id.splash_screen_progress_bar);
        //progressBar.getProgressDrawable().setColorFilter(R.drawable.gradient_progress, PorterDuff.Mode.SRC_IN);
        image = findViewById(R.id.image);

        animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        image.startAnimation(animation);
        progressBar.startAnimation(animation);

        usuario = Utils.loadFromSharedPreferences(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int progress=0; progress<=100; progress+=10) {
                    try {
                        Thread.sleep(250);
                        progressBar.setProgress(progress);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(usuario.getId_usuario() != 0){
                    startActivity(new Intent(mCtx, Descontos.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else{
                    startActivity(new Intent(mCtx, Main.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
                finish();
            }
        }).start();

    }
}
