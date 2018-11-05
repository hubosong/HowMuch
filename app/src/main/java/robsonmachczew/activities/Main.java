package robsonmachczew.activities;

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

import entidade.Usuario;
import entidade.Utils;

public class Main extends AppCompatActivity {

    private Button btnLogin, btnAccess;
    private VideoView mVideoView;
    private Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuario = Utils.loadFromSharedPreferences(this);

        if(usuario.getId_usuario() != 0){
            Intent content = new Intent(this, Descontos.class);
            startActivity(content);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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


        //button access
        btnAccess = findViewById(R.id.btn_no_login);
        btnAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent content = new Intent(Main.this, Descontos.class);
                startActivity(content);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(Main.this, Login.class);
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
        Intent user = new Intent(this, Nav.class);
        finish();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(this, R.string.toast_got_out, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();


    }
}

