package robsonmachczew.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
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

        //basic confg
        //overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        overridePendingTransition(R.anim.right_in, R.anim.right_out);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        //verify user login
        usuario = Utils.loadFromSharedPreferences(this);
        if(usuario.getId_usuario() != 0){
            Intent content = new Intent(this, Descontos.class);
            startActivity(content);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }


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
        LayoutInflater inflaterAbout = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View aboutView = inflaterAbout.inflate(R.layout.layout_sobre, null);
        final AlertDialog dialogAlertabout = new AlertDialog.Builder(Main.this).create();
        dialogAlertabout.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAlertabout.getWindow().setDimAmount(0.8f);
        dialogAlertabout.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //ANIMATION
        dialogAlertabout.setView(aboutView);
        dialogAlertabout.show();
    }
    public void development(View view) {
        Toast t = Toast.makeText(this, "Desenvolvido por\nElton Rasch (埃尔顿) &\nRobson Machczew (胡博嵩)", Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER, 0, 50);
        t.show();
    }
    public void phone(View view) {
        //gerar um sair do app 哈哈
        Intent user = new Intent(this, Nav.class);
        finish();
    }


}

