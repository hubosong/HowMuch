package com.machczew.howmuch;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import entidade.Lista;
import entidade.Usuario;
import entidade.Utils;

public class Minhas_Listas extends Nav {

    private Usuario usuario;
    private LinearLayout layout_listas_de_listas;
    private TextView tv_quant_listas;
    private SimpleDateFormat sdf_bd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private SimpleDateFormat sdf_exibicao = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
    private boolean backAlreadyPressed = false;
    private boolean permiteVoltar = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_minhas_listas, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Minhas Listas");

        permiteVoltar = getIntent().getBooleanExtra("PERMITE_VOLTAR", false);

        layout_listas_de_listas = findViewById(R.id.layout_listas_de_listas);
        tv_quant_listas = findViewById(R.id.tv_quant_listas);

        if (!Utils.estaConectado(this)) {
            Toast.makeText(this, "Sem conexão", Toast.LENGTH_LONG).show();
        } else {
            pegaListaDeCompras();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void pegaListaDeCompras() {
        if (usuario == null) {
            usuario = Utils.loadFromSharedPreferences(this);
        }
        if (usuario != null && usuario.getId_usuario() > 0) {
            new AsyncTask<String, Void, ArrayList<Lista>>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected ArrayList<Lista> doInBackground(String... params) {
                    ArrayList<Lista> list = null;
                    try {
                        URL url = new URL(Utils.URL + "lista");
                        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                        urlCon.setRequestMethod("POST");
                        urlCon.setDoOutput(true);
                        urlCon.setDoInput(true);

                        ObjectOutputStream wr = new ObjectOutputStream(urlCon.getOutputStream());
                        wr.writeUTF("GET_BY_ID_USUARIO");
                        wr.writeLong(usuario.getId_usuario());
                        wr.close();
                        wr.flush();

                        ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream());
                        list = (ArrayList<Lista>) ois.readObject();
                        ois.close();

                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                    return list;
                }

                @Override
                protected void onPostExecute(ArrayList<Lista> list) {
                    rederizaListas(list);
                }
            }.execute();
        } else {
            Toast.makeText(this, "Você não está logado!", Toast.LENGTH_LONG).show();
        }
    }

    private void rederizaListas(ArrayList<Lista> list) {
        if (list != null) {
            try {
                final Context ctx = this;
                layout_listas_de_listas.removeAllViews();
                tv_quant_listas.setText("" + list.size() + "");
                for (final Lista lista : list) {
                    Date date = sdf_bd.parse(lista.getData());
                    lista.setData(sdf_exibicao.format(date));
                    View item; // Creating an instance for View Object
                    LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    item = inflater.inflate(R.layout.layout_lista_de_listas, null);
                    ((TextView) item.findViewById(R.id.txtNomeLista)).setText(lista.getNome());
                    ((TextView) item.findViewById(R.id.txtQtdItems)).setText(lista.getListaProdutos().size() + " Produtos");
                    ((TextView) item.findViewById(R.id.txtDataLista)).setText(lista.getData());
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog dialog_opcoes_lista = new Dialog(Minhas_Listas.this);

                            dialog_opcoes_lista.requestWindowFeature(Window.FEATURE_NO_TITLE); //no toolbar
                            dialog_opcoes_lista.setContentView(R.layout.dialog_opcoes_lista_de_listas);

                            dialog_opcoes_lista.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog_opcoes_lista.getWindow().setDimAmount(0.8f);
                            dialog_opcoes_lista.getWindow().getAttributes().windowAnimations = R.style.AllDialogAnimation; //ANIMATION

                            ((Button) dialog_opcoes_lista.findViewById(R.id.bt_editar_lista)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(ctx, Criar_lista_Compras.class);
                                    intent.putExtra("PERMITE_VOLTAR", true);
                                    intent.putExtra("LISTA", lista);
                                    startActivity(intent);
                                    dialog_opcoes_lista.cancel();
                                }
                            });
                            ((Button) dialog_opcoes_lista.findViewById(R.id.bt_comparar_precos_em_mercados)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(ctx, VerComparacaoLista.class);
                                    intent.putExtra("PERMITE_VOLTAR", true);
                                    intent.putExtra("LISTA", lista);
                                    startActivity(intent);
                                    dialog_opcoes_lista.cancel();
                                }
                            });
                            ((Button) dialog_opcoes_lista.findViewById(R.id.bt_excluir_lista)).setOnClickListener(new View.OnClickListener() {
                                @SuppressLint("StaticFieldLeak")
                                @Override
                                public void onClick(View view) {
                                    new AlertDialog.Builder(Minhas_Listas.this)
                                            .setMessage("Deseja realmente excluir esta\nLISTA DE COMPRAS?")
                                            .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which){
                                                    new AsyncTask<String, Void, Boolean>() {
                                                        @Override
                                                        protected Boolean doInBackground(String... params) {
                                                            try {
                                                                URL url = new URL(Utils.URL + "lista");
                                                                HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                                                                urlCon.setRequestMethod("POST");
                                                                urlCon.setDoOutput(true);
                                                                urlCon.setDoInput(true);

                                                                ObjectOutputStream wr = new ObjectOutputStream(urlCon.getOutputStream());
                                                                wr.writeUTF("APAGAR_LISTA");
                                                                wr.writeLong(usuario.getId_usuario());
                                                                wr.writeLong(lista.getId_lista());
                                                                wr.close();
                                                                wr.flush();

                                                                if(urlCon.getResponseCode() == 204){
                                                                    return true;
                                                                }

                                                            } catch (Exception e) {
                                                                System.out.println(">>> Erro tentando excluir nota: "+e.getMessage());
                                                                e.printStackTrace();
                                                            }
                                                            return false;
                                                        }

                                                        @Override
                                                        protected void onPostExecute(Boolean sucesso) {
                                                            if(sucesso){
                                                                pegaListaDeCompras();
                                                            }
                                                        }
                                                    }.execute();
                                                    Toast.makeText(Minhas_Listas.this, "Lista de Compras Excluída!", Toast.LENGTH_SHORT).show();
                                                    dialog.cancel();
                                                    dialog_opcoes_lista.cancel();
                                                }
                                            })
                                            .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which){
                                                    dialog.cancel();
                                                }
                                            }).show();

                                }
                            });
                            dialog_opcoes_lista.show();
                        }
                    });
                    layout_listas_de_listas.addView(item);
                }
            }catch (Exception e){
                Toast.makeText(this, "Erro ao Exibir Produtos", Toast.LENGTH_LONG).show();
                System.out.println(">>> Erro: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Toast.makeText(Minhas_Listas.this, "Nenhuma Lista Encontrada", Toast.LENGTH_LONG).show();
        }
    }

    //on back
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }
        if (permiteVoltar) {
            super.onBackPressed();
        }
        else {
            if (usuario.getId_usuario() != 0) {
                //snack
                final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_layout);
                if (backAlreadyPressed) {
                    finish();
                    System.exit(0);
                    return;
                }
                backAlreadyPressed = true;
                Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.snackbar, Snackbar.LENGTH_LONG);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() { backAlreadyPressed = false; }
                }, 3000);
                TextView txtSnackBar = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                txtSnackBar.setGravity(Gravity.CENTER_HORIZONTAL);
                txtSnackBar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                txtSnackBar.setTextColor(Color.parseColor("#ffffff"));
                snackbar.getView().setBackgroundResource(R.drawable.gradient_list);
                snackbar.show();

            } else {
                Intent main = new Intent(Minhas_Listas.this, Main.class);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(main);
                finish();
            }
        }

    }
}
