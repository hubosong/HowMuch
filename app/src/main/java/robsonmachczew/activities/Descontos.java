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

    private ProgressBar progWait;
    private TextView txtResult, txtWait;
    private MaterialSearchView searchView;
    private RecyclerView recyclerView;
    private final Activity activity = this;
    public Animation alpha_in, alpha_out;

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

        alpha_in = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        alpha_out = AnimationUtils.loadAnimation(this, R.anim.alpha_out);

        progWait = findViewById(R.id.progWait);
        txtWait = findViewById(R.id.txtWait);
        txtResult = findViewById(R.id.txtResult);

        //searchview
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView();

        //recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        ProdutoAbaixoMediaAdapter tmp_adapter = new ProdutoAbaixoMediaAdapter(this, new ArrayList<ProdutoAbaixoMedia>());
        recyclerView.setAdapter(tmp_adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //test internet connection
        if(!Utils.estaConectado(this)){
            Toast.makeText(this, "Sem conexão", Toast.LENGTH_LONG).show();
            return;
        }
        rvListStart();
    }

    //searchView
    public void searchView() {
        searchView.setHint("Consultar Produto..");
        searchView.setHintTextColor(R.color.hint_nav_login);
        searchView.setVoiceSearch(true);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                new AsyncTask<String, Void, ArrayList<Item_NFe>>() {

                    @Override
                    protected void onPreExecute() {
                        progWait.setVisibility(View.VISIBLE);
                        txtWait.setVisibility(View.VISIBLE);
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
                        //Se voltar nulo é porque deu algum erro
                        if (list != null) {
                            Toast.makeText(Descontos.this, list.size() + " itens encontrados", Toast.LENGTH_LONG).show();
                            Item_NFeAdapter adapter = new Item_NFeAdapter(Descontos.this, list);
                            recyclerView.setAdapter(adapter);

                            //hide floating button when scroll
                            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                @Override
                                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                    super.onScrolled(recyclerView, dx, dy);
                                    if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                                        fab.hide();
                                        fab.startAnimation(alpha_out);
                                    } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                                        fab.show();
                                        fab.startAnimation(alpha_in);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(Descontos.this, "Nenhum Item Encontrado", Toast.LENGTH_LONG).show();
                        }

                    }
                }.execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if(newText.length() == 3){
                    new AsyncTask<String, Void, ArrayList<Produto>>() {

                        @Override
                        protected void onPreExecute() {
                            progWait.setVisibility(View.VISIBLE);
                            txtWait.setVisibility(View.VISIBLE);
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

                fab.setVisibility(View.GONE);
                fab.startAnimation(alpha_out);
            }

            @Override
            public void onSearchViewClosed() {
                Window window = activity.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(activity, R.color.toolbar_status));

                fab.setVisibility(View.VISIBLE);
                fab.startAnimation(alpha_in);
            }
        });
    }

    //recyclerview list off == Descontos
    @SuppressLint("StaticFieldLeak")
    public void rvListStart() {
        new AsyncTask<String, Void, ArrayList<ProdutoAbaixoMedia>>() {
            @Override
            protected void onPreExecute() {
                progWait.setVisibility(View.VISIBLE);
                txtWait.setVisibility(View.VISIBLE);
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
                progWait.setVisibility(View.GONE);
                txtWait.setVisibility(View.GONE);
                //Se voltar nulo é porque deu algum erro
                if (list != null) {
                    Toast.makeText(Descontos.this, list.size() + " itens encontrados", Toast.LENGTH_LONG).show();
                    ProdutoAbaixoMediaAdapter adapter = new ProdutoAbaixoMediaAdapter(Descontos.this, list);
                    recyclerView.setAdapter(adapter);

                    //hide floating button when scroll
                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);
                            if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                                fab.hide();
                                fab.startAnimation(alpha_out);
                            } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                                fab.show();
                                fab.startAnimation(alpha_in);
                            }
                        }
                    });
                } else {
                    Toast.makeText(Descontos.this, "Nenhum Item Encontrado", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();

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
