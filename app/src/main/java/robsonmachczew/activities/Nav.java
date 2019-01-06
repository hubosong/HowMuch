package robsonmachczew.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.content.IntentCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import entidade.Usuario;
import entidade.Utils;

public class Nav extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private final Activity activity = this;

    public FloatingActionButton fab;

    private TextView navNome, navEmail;

    private Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        usuario = Utils.loadFromSharedPreferences(this);

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
                Intent editRegister = new Intent(activity, Editar_Cadastros.class);
                startActivity(editRegister);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        });

        navEmail = headerView.findViewById(R.id.txtEmail);
        navNome = headerView.findViewById(R.id.txtNome);
        if (usuario.getId_usuario() != 0) {
            navNome.setText(usuario.getNome());
            navEmail.setText(usuario.getEmail());
        } else {
            navNome.setVisibility(View.GONE);
            navEmail.setVisibility(View.GONE);
            Menu nav_menu = navigationView.getMenu();
            nav_menu.findItem(R.id.nav_my_buy).setVisible(false);
            nav_menu.findItem(R.id.nav_my_nfe).setVisible(false);
        }

    }

    //nav listener items
    class navItem implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_off:
                    Intent off = new Intent(activity, Descontos.class);
                    off.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); //Limpar todas as stacks de activities
                    startActivity(off);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    drawerLayout.closeDrawers();
                    break;

                case R.id.nav_new_list:
                    Intent buyList = new Intent(activity, Criar_Lista_Compras.class);
                    buyList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(buyList);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    drawerLayout.closeDrawers();
                    break;

                case R.id.nav_my_buy:
                    Intent my_buy = new Intent(activity, Minhas_Listas.class);
                    my_buy.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(my_buy);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    drawerLayout.closeDrawers();
                    break;

                case R.id.nav_my_nfe:
                    Intent my_nfe = new Intent(activity, Minhas_NFes.class);
                    my_nfe.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(my_nfe);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

                case R.id.nav_alertas:
                    drawerLayout.closeDrawers();
                    Toast.makeText(Nav.this, "Não Implementado", Toast.LENGTH_LONG).show();
                    break;

                case R.id.nav_financas:
                    drawerLayout.closeDrawers();
                    Toast.makeText(Nav.this, "Não Implementado", Toast.LENGTH_LONG).show();
                    break;

                case R.id.nav_logout:
                    if (Utils.logout(activity)) {
                        Intent main = new Intent(Nav.this, Main.class);
                        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(main);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        drawerLayout.closeDrawers();
                    } else {
                        Toast.makeText(Nav.this, "Não foi possível deslogar", Toast.LENGTH_LONG).show();
                    }
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
                int corte = cut.indexOf("=") + 1;
                final String code = cut.substring(corte, corte + 44);
                Intent readQRcode = new Intent(activity, VerNFe.class);
                readQRcode.putExtra("code", code);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(readQRcode);
            } else {
                Toast.makeText(activity, R.string.toast_cancel_read_qrcode, Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawers();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        drawerLayout.closeDrawers();
        super.onBackPressed();
    }
}
