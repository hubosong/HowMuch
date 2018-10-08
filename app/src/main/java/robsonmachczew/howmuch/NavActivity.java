package robsonmachczew.howmuch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import adapter.ProductQRCodeAdapter;
import entidade.NFe;
import entidade.Usuario;

public class NavActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private final Activity activity = this;

    public FloatingActionButton fab;

    private SharedPreferences prefs;
    private TextView navNome, navEmail;
    private String prefs_user_name, prefs_user_email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.toolbar_status));

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //floating button
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("");
                integrator.setCameraId(0);
                integrator.initiateScan();
                integrator.setBarcodeImageEnabled(false);
                integrator.setOrientationLocked(false);
                integrator.setBeepEnabled(true);
            }
        });


        //nav
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        //call nav listener of navigationView
        navigationView.setNavigationItemSelectedListener(new navItem());


        //call edit register
        View headerView = navigationView.getHeaderView(0);
        ImageView navImg = headerView.findViewById(R.id.imageContact);
        navImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent editRegister = new Intent(activity, EditRegisterActivity.class);
                startActivity(editRegister);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        //user receive for teste
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        navEmail = headerView.findViewById(R.id.txtEmail);
        navNome = headerView.findViewById(R.id.txtNome);
        prefs_user_name = prefs.getString("nome", "--");
        prefs_user_email = prefs.getString("email", "--@--");
        navNome.setText(prefs_user_name);
        navEmail.setText(prefs_user_email);

        //Toast.makeText(activity, navNome.getText(), Toast.LENGTH_SHORT).show();

        /*
        Menu nav_menu = navigationView.getMenu();
        if(navNome.getText() == ("--")){
            Toast.makeText(activity, navNome.getText() + " two", Toast.LENGTH_SHORT).show();
            //nav_menu.findItem(R.id.nav_my_buy).setVisible(false);
            //nav_menu.findItem(R.id.nav_my_nfe).setVisible(false);

            navigationView.getMenu().findItem(R.id.nav_my_buy).setVisible(false);
        }
        */

    }

    //nav listener items
    class navItem implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_off:
                    Intent off = new Intent(NavActivity.this, OffActivity.class);
                    startActivity(off);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    drawerLayout.closeDrawers();
                    break;

                case R.id.nav_new_list:
                    Intent buyList = new Intent(NavActivity.this, BuyListActivity.class);
                    startActivity(buyList);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    drawerLayout.closeDrawers();
                    break;

                case R.id.nav_my_buy:
                    Intent my_buy = new Intent(NavActivity.this, MyBuyActivity.class);
                    startActivity(my_buy);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    drawerLayout.closeDrawers();
                    break;

                case R.id.nav_my_nfe:
                    Intent my_nfe = new Intent(NavActivity.this, MyNFeActivity.class);
                    startActivity(my_nfe);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    drawerLayout.closeDrawers();
                    break;

                case R.id.nav_send_nfe:
                    IntentIntegrator integrator = new IntentIntegrator(activity);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    integrator.setPrompt("");
                    integrator.setCameraId(0);
                    integrator.initiateScan();
                    integrator.setBarcodeImageEnabled(false);
                    integrator.setOrientationLocked(false);
                    integrator.setBeepEnabled(true);
                    drawerLayout.closeDrawers();
                    break;

                case R.id.nav_logout:

                    //clear preferences
                    prefs.edit().remove("nome").apply();
                    prefs.edit().remove("email").apply();

                    Intent main = new Intent(NavActivity.this, MainActivity.class);
                    startActivity(main);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    drawerLayout.closeDrawers();
                    break;

            }
            return false;
        }
    }


    //qrcode reader
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
                String cut = result.getContents();
                String code = cut.substring(53, 97);
                Intent readQRcode = new Intent(activity, ReadQRCodeActivity.class);
                readQRcode.putExtra("code", code);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(readQRcode);
                finish();
                drawerLayout.closeDrawers();
            } else {
                Toast.makeText(activity, R.string.toast_cancel_read_qrcode, Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }
}
