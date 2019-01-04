package robsonmachczew.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import adapter.Item_NFeAdapter;
import adapter.ProdutoAbaixoMediaAdapter;
import entidade.Item_NFe;
import entidade.Produto;
import entidade.ProdutoAbaixoMedia;
import entidade.Utils;

public class Descontos extends Nav {

    private LinearLayout layout_produtos_desconto;
    private TextView tv_quant_prods_desconto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_descontos, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle(R.string.bar_off);

        layout_produtos_desconto = findViewById(R.id.layout_prods_desconto);
        tv_quant_prods_desconto = findViewById(R.id.tv_quant_prods_abaixo_media);

        //test internet connection
        if(!Utils.estaConectado(this)){
            Toast.makeText(this, "Sem conexão", Toast.LENGTH_LONG).show();
        }else{
            rvListStart();
        }
    }

    //recyclerview list off == Descontos
    @SuppressLint("StaticFieldLeak")
    public void rvListStart() {
        new AsyncTask<String, Void, ArrayList<ProdutoAbaixoMedia>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected ArrayList<ProdutoAbaixoMedia> doInBackground(String... params) {
                ArrayList<ProdutoAbaixoMedia> list = null;
                //if(Utils.servidorDePe()) {
                    try {
                        String urlParameters = "funcao=GET_PRODUTOS_ABAIXO_MEDIA";
                        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                        URL url = new URL(Utils.URL + "produto");
                        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                        urlCon.setRequestMethod("POST");
                        urlCon.setDoOutput(true);
                        urlCon.setDoInput(true);

                        DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
                        wr.write(postData);
                        wr.close();
                        wr.flush();

                        ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream());
                        list = (ArrayList<ProdutoAbaixoMedia>) ois.readObject();
                        ois.close();

                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                //}
                return list;
            }

            @Override
            protected void onPostExecute(ArrayList<ProdutoAbaixoMedia> list) {
                renderizaProdutosComDesconto(list);
            }
        }.execute();

    }

    private void renderizaProdutosComDesconto(ArrayList<ProdutoAbaixoMedia> list){
        if (list != null) {
            tv_quant_prods_desconto.setText("Produtos Abaixo do Valor Médio ("+list.size()+"):");
            for(ProdutoAbaixoMedia produto : list){

            }
        } else {
            Toast.makeText(Descontos.this, "Nenhum Item Encontrado", Toast.LENGTH_LONG).show();
        }
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        //search bar
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //onBack
    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }

        Intent main = new Intent(Descontos.this, Main.class);
        startActivity(main);
        finish();

    }

}
