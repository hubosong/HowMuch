package robsonmachczew.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import entidade.Lista;
import entidade.Produto;
import entidade.ProdutoAbaixoMedia;
import entidade.Usuario;
import entidade.Utils;

public class Lista_Compras extends Nav {

    private MaterialSearchView searchView;
    private ProgressBar progWait;
    private TextView txtWait, txtInfoLupa;
    private Button btnOptLista, btnFinalizar;
    private LinearLayout btnsOptions;
    private RecyclerView recyclerView;

    private final Activity activity = this;
    private Animation alpha_in, alpha_out;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_lista__compras, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Lista Compras");

        alpha_in = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        alpha_out = AnimationUtils.loadAnimation(this, R.anim.alpha_out);
        progWait = findViewById(R.id.progWait);
        txtWait = findViewById(R.id.txtWait);
        txtInfoLupa = findViewById(R.id.txtInfoLupa);
        btnOptLista = findViewById(R.id.btnOptLista);
        btnFinalizar = findViewById(R.id.btnFinalizar);
        btnsOptions = findViewById(R.id.btnsOptions);
        usuario = Utils.loadFromSharedPreferences(this);
        fab.hide();


        //searchview
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView();

        //recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //test internet connection
        if(!Utils.estaConectado(this)){
            Toast.makeText(this, "Sem conexão", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void Options(View view) {
        Toast.makeText(activity, "Options", Toast.LENGTH_SHORT).show();
    }

    public void Finish(View view) {
        Toast.makeText(activity, "Finish", Toast.LENGTH_SHORT).show();
    }

    //searchView
    public void searchView() {
        searchView.setHint("Consultar Produto..");
        searchView.setHintTextColor(R.color.hint_nav_login);
        searchView.setVoiceSearch(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public boolean onQueryTextSubmit(final String query) {
                new AsyncTask<String, Void, ArrayList<Item_NFe>>() {

                    @Override
                    protected void onPreExecute() {
                        progWait.setVisibility(View.VISIBLE);
                        txtWait.setVisibility(View.VISIBLE);
                        txtInfoLupa.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        super.onPreExecute();
                    }

                    @Override
                    protected ArrayList<Item_NFe> doInBackground(String... params) {
                        ArrayList<Item_NFe> list = null;
                        try {
                            String urlParameters = "funcao=GET_PRODUTOS_PESQUISA_APP&descricao=" + query;
                            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                            URL url = new URL(Utils.URL+"produto");
                            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                            urlCon.setRequestMethod("POST");
                            urlCon.setDoOutput(true);
                            urlCon.setDoInput(true);

                            DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
                            wr.write(postData);
                            wr.close();
                            wr.flush();

                            ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream());
                            list = (ArrayList<Item_NFe>) ois.readObject();
                            ois.close();

                        } catch (ClassNotFoundException | IOException e) {
                            e.printStackTrace();
                        }
                        return list;
                    }

                    @Override
                    protected void onPostExecute(ArrayList<Item_NFe> list) {
                        progWait.setVisibility(View.GONE);
                        txtWait.setVisibility(View.GONE);
                        txtInfoLupa.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        //Se voltar nulo é porque deu algum erro
                        if (list != null) {
                            Toast.makeText(Lista_Compras.this, list.size() + " itens encontrados", Toast.LENGTH_LONG).show();
                            Item_NFeAdapter adapter = new Item_NFeAdapter(Lista_Compras.this, list);
                            recyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(Lista_Compras.this, "Nenhum Item Encontrado", Toast.LENGTH_LONG).show();
                        }

                    }
                }.execute();
                return false;
            }

            @SuppressLint("StaticFieldLeak")
            @Override
            public boolean onQueryTextChange(final String newText) {
                if(newText.length() == 3){
                    new AsyncTask<String, Void, ArrayList<Produto>>() {

                        @Override
                        protected void onPreExecute() {
                            progWait.setVisibility(View.VISIBLE);
                            txtWait.setVisibility(View.VISIBLE);
                            txtInfoLupa.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                            super.onPreExecute();
                        }

                        @Override
                        protected ArrayList<Produto> doInBackground(String... params) {
                            ArrayList<Produto> list = null;
                            try {
                                String urlParameters = "funcao=GET_BY_DESCRICAO&descricao=" + newText;
                                byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                                URL url = new URL(Utils.URL+"produto");
                                HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                                urlCon.setRequestMethod("POST");
                                urlCon.setDoOutput(true);
                                urlCon.setDoInput(true);

                                DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
                                wr.write(postData);
                                wr.close();
                                wr.flush();

                                ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream());
                                list = (ArrayList<Produto>) ois.readObject();
                                ois.close();

                            } catch (ClassNotFoundException | IOException e) {
                                e.printStackTrace();
                            }
                            return list;
                        }

                        @Override
                        protected void onPostExecute(ArrayList<Produto> list) {
                            progWait.setVisibility(View.GONE);
                            txtWait.setVisibility(View.GONE);
                            txtInfoLupa.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);

                            String[] listaProds = new String[list.size()];
                            for(int i=0; i<list.size(); i++){
                                listaProds[i] = list.get(i).getDescricao();
                            }
                            searchView.setSuggestions(listaProds);
                        }
                    }.execute();

                }
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                Window window = activity.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(activity, R.color.searchview_status));

                btnsOptions.setVisibility(View.GONE);
                btnsOptions.startAnimation(alpha_out);

            }

            @Override
            public void onSearchViewClosed() {
                Window window = activity.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(activity, R.color.toolbar_status));

                btnsOptions.setVisibility(View.VISIBLE);
                btnsOptions.startAnimation(alpha_in);
            }
        });
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

        Intent main = new Intent(Lista_Compras.this, Main.class);
        startActivity(main);
        finish();

    }

}
