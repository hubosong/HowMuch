package robsonmachczew.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import adapter.ListaAdapter;
import adapter.ProdutoAbaixoMediaAdapter;
import entidade.Lista;
import entidade.ProdutoAbaixoMedia;
import entidade.Usuario;
import entidade.Utils;

public class Minhas_Listas extends Nav {

    private Usuario usuario;
    private LinearLayout layout_listas_de_listas;
    private TextView tv_quant_listas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_minhas_listas, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle(R.string.bar_my_buys);

        layout_listas_de_listas = findViewById(R.id.layout_listas_de_listas);
        tv_quant_listas = findViewById(R.id.tv_quant_listas);

        if(!Utils.estaConectado(this)){
            Toast.makeText(this, "Sem conexão", Toast.LENGTH_LONG).show();
        }else{
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

    @Override
    public void onBackPressed() {
        Intent main = new Intent(Minhas_Listas.this, Main.class);
        startActivity(main);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void rederizaListas(ArrayList<Lista> list){
        if (list != null) {
            layout_listas_de_listas.removeAllViews();
            tv_quant_listas.setText("Listas Encontradas ("+list.size()+"):");
            for(Lista lista : list){
                String s = lista.getNome();
                if(!s.trim().equalsIgnoreCase("")){
                    s += " - ";
                }
                s += lista.getData();
                String s2 = lista.getListaProdutos().size()+" Produtos";
                LinearLayout ll = new LinearLayout(this);
                ll.setOrientation(LinearLayout.VERTICAL);
                TextView tv1 = new TextView(this);
                TextView tv2 = new TextView(this);
                tv1.setText(s);
                tv2.setText(s2);
                ll.addView(tv1);
                ll.addView(tv2);
                layout_listas_de_listas.addView(ll);
            }
        } else {
            Toast.makeText(Minhas_Listas.this, "Nenhuma Lista Encontrada", Toast.LENGTH_LONG).show();
        }
    }

}
