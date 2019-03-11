package robsonmachczew.activities;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import entidade.Usuario;
import entidade.Utils;

public class Alerta extends Nav {

    private boolean backAlreadyPressed = false;
    private boolean permiteVoltar = false;
    private Usuario usuario;

    private final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_alerta, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(5).setChecked(true);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Config. Alertas");

        permiteVoltar = getIntent().getBooleanExtra("PERMITE_VOLTAR", false);

        if (!Utils.estaConectado(this)) {
            Toast.makeText(this, "Sem conexão", Toast.LENGTH_LONG).show();
        }


        /*
        final String notification = "false";
        if(notification == "true"){
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent p = PendingIntent.getActivity(activity, 0, new Intent(activity, Descontos.class), 0);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(activity);
            builder.setTicker("hu");
            builder.setContentTitle("PRODUTO");
            builder.setContentText("Descricao Produto");
            builder.setSmallIcon(R.drawable.filtrapreco_g);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.filtrapreco_g));
            builder.setContentIntent(p);
            builder.setDefaults(Notification.DEFAULT_ALL);
            nm.notify(0, builder.build());

        }
        */

        findViewById(R.id.btnPush).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoteViews remoteCollapsedViews = new RemoteViews(getPackageName(), R.layout.layout_push);
                RemoteViews remoteExpandedViews = new RemoteViews(getPackageName(), R.layout.layout_push);

                NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                PendingIntent p = PendingIntent.getActivity(activity, 0, new Intent(activity, Descontos.class), 0);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(activity);
                builder.setSmallIcon(R.drawable.filtrapreco_g);
                builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
                builder.setCustomContentView(remoteCollapsedViews);
                builder.setCustomBigContentView(remoteExpandedViews);
                builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.filtrapreco_g));
                builder.setContentIntent(p);
                builder.setAutoCancel(true);
                builder.setDefaults(Notification.DEFAULT_ALL);

                nm.notify(0, builder.build());



                /*
                PendingIntent pIntent = PendingIntent.getActivity(activity, 0, new Intent(activity, Descontos.class), 0);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(activity)
                        .setSmallIcon(R.drawable.filtrapreco_g)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true)
                        .setContentIntent(pIntent)
                        .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                        .setCustomContentView(remoteCollapsedViews)
                        .setCustomBigContentView(remoteExpandedViews);

                NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationmanager.notify(0, builder.build());
                */

            }
        });


    }

}
