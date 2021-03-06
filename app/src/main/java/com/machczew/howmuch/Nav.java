package com.machczew.howmuch;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import entidade.Usuario;
import entidade.Utils;

public class Nav extends AppCompatActivity {

    public DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private final Activity activity = this;
    private TextView navNome, navEmail;
    private Usuario usuario;
    public int code = 0; //evitar conflitos leitura codigos


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
        /*
        if (usuario.getId_usuario() != 0) {
            navNome.setText(usuario.getNome());
            navEmail.setText(usuario.getEmail());
        } else {
            navNome.setVisibility(View.GONE);
            navEmail.setVisibility(View.GONE);
            Menu nav_menu = navigationView.getMenu();
            nav_menu.findItem(R.id.nav_my_buy).setVisible(false);
            nav_menu.findItem(R.id.nav_my_nfe).setVisible(false);
            nav_menu.findItem(R.id.nav_alertas).setVisible(false);
            nav_menu.findItem(R.id.nav_financas).setVisible(false);
            nav_menu.findItem(R.id.nav_logout).setTitle("Realizar LOGIN");
            nav_menu.findItem(R.id.nav_logout).setIcon(R.drawable.ic_access);

        }

         */
        navNome.setVisibility(View.VISIBLE);
        navEmail.setVisibility(View.VISIBLE);
        Menu nav_menu = navigationView.getMenu();
        nav_menu.findItem(R.id.nav_my_buy).setVisible(true);
        nav_menu.findItem(R.id.nav_my_nfe).setVisible(true);
        nav_menu.findItem(R.id.nav_alertas).setVisible(true);
        nav_menu.findItem(R.id.nav_financas).setVisible(true);
        nav_menu.findItem(R.id.nav_logout).setTitle("Realizar LOGIN");
        nav_menu.findItem(R.id.nav_logout).setIcon(R.drawable.ic_access);

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
                    break;

                case R.id.nav_new_list:
                    Intent buyList = new Intent(activity, Criar_lista_Compras.class);
                    buyList.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(buyList);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;

                case R.id.nav_my_buy:
                    Intent my_buy = new Intent(activity, Minhas_Listas.class);
                    my_buy.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(my_buy);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;

                case R.id.nav_my_nfe:
                    Intent my_nfe = new Intent(activity, Minhas_NFes.class);
                    my_nfe.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(my_nfe);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;

                case R.id.nav_send_nfe:
                    LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View mView = inflater.inflate(R.layout.layout_enviar_nfe_manualmente, null);

                    final AlertDialog dialogAlert = new AlertDialog.Builder(Nav.this).create();
                    final EditText edtNomeLista = mView.findViewById(R.id.userInputDialog);
                    final Button btnCancelar = mView.findViewById(R.id.btnCancelar);
                    final Button btnSalvar = mView.findViewById(R.id.btnSalvar);

                    dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogAlert.getWindow().setDimAmount(0.8f);
                    dialogAlert.getWindow().getAttributes().windowAnimations = R.style.AllDialogAnimation; //ANIMATION

                    btnSalvar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String code = edtNomeLista.getText().toString();
                            if(code.length() == 44){
                                Intent verNFe = new Intent(activity, VerNFe.class);
                                verNFe.putExtra("code", code);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                startActivity(verNFe);
                                dialogAlert.dismiss();
                            } else {
                                Toast.makeText(activity, "Ops! É necessário 44 números.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                    btnCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialogAlert.dismiss();
                        }
                    });

                    dialogAlert.setView(mView);
                    dialogAlert.show();
                    break;

                    /*
                    IntentIntegrator integrator = new IntentIntegrator(activity);
                    integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                    integrator.setPrompt("");
                    integrator.setCameraId(0);
                    integrator.initiateScan();
                    integrator.setBarcodeImageEnabled(false);
                    integrator.setOrientationLocked(false);
                    integrator.setBeepEnabled(true);
                    code = 1;
                    break;
                    */

                case R.id.nav_alertas:
                    startActivity(new Intent(activity, Alerta.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    break;

                case R.id.nav_financas:
                    Toast.makeText(Nav.this, "Não Implementado", Toast.LENGTH_LONG).show();
                    break;

                case R.id.nav_sobre:
                    LayoutInflater inflaterAbout = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View aboutView = inflaterAbout.inflate(R.layout.layout_sobre, null);
                    final AlertDialog dialogAlertabout = new AlertDialog.Builder(Nav.this).create();
                    dialogAlertabout.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogAlertabout.getWindow().setDimAmount(0.8f);
                    dialogAlertabout.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation; //ANIMATION
                    dialogAlertabout.setView(aboutView);
                    dialogAlertabout.show();

                    break;

                case R.id.nav_logout:
                    if(item.getTitle() == "Realizar LOGIN"){
                        Intent login = new Intent(Nav.this, Login.class);
                        startActivity(login);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        if (Utils.logout(activity)) {
                            Intent main = new Intent(Nav.this, Main.class);
                            main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(main);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Toast.makeText(Nav.this, "Não foi possível deslogar", Toast.LENGTH_LONG).show();
                        }
                    }

                    break;

            }
            drawerLayout.closeDrawers();
            return false;
        }
    }


    //qrcode and barcode reader
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if qrcode reader
        if(code == 1){
            IntentResult qrcode = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (qrcode != null) {
                if (qrcode.getContents() != null) {
                    String cut = qrcode.getContents();
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
        //if barcode reader
        else if (code == 2){
            IntentResult barcode = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (barcode != null) {
                if (barcode.getContents() != null) {
                    String codigo_de_barras = barcode.getContents();
                    Descontos d = new Descontos();
                    d.pegaProdutoPorCodigoDeBarras(codigo_de_barras);
                } else {
                    Toast.makeText(this, "Leitura do código de barras cancelado", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Erro ao ler código de barras do Produto", Toast.LENGTH_LONG).show();
            }
        }

    }


    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_qrcode:
                IntentIntegrator integrator = new IntentIntegrator(activity);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Alinhe o Código\n\n\n\n");
                integrator.setCameraId(0);
                integrator.initiateScan();
                integrator.setBarcodeImageEnabled(false);
                integrator.setOrientationLocked(false);
                integrator.setBeepEnabled(true);
                code = 1;
                return  true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
        }else {
            super.onBackPressed();
        }
    }


}
