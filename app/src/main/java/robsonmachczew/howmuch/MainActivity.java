package robsonmachczew.howmuch;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import entidade.Utils;

public class MainActivity extends AppCompatActivity {

    private Button btnLogin, btnAccess;
    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Utils.loadFromSharedPreferences(this).getId_usuario() != 0){
            Intent content = new Intent(this, OffActivity.class);
            startActivity(content);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //background video
        mVideoView = (VideoView) findViewById(R.id.bgVideoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.market);
        mVideoView.setVideoURI(uri);
        mVideoView.start();
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });


        //button access
        btnAccess = findViewById(R.id.btn_no_login);
        btnAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent content = new Intent(MainActivity.this, OffActivity.class);
                startActivity(content);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(login);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

    }


    public void contact(View view) {
        Toast t = Toast.makeText(this, "胡 博 嵩 - 1146920702@qq.com", Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER, 0, 50);
        t.show();
    }
    public void development(View view) {
        Toast.makeText(this, "Robson Machczew", Toast.LENGTH_SHORT).show();
    }
    public void phone(View view) {
        //gerar um sair do app 哈哈
        Intent user = new Intent(this, NavActivity.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
        Toast.makeText(this, R.string.toast_got_out, Toast.LENGTH_SHORT).show();
    }
}
