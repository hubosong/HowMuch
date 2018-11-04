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

import adapter.NFeAdapter;
import adapter.ProdutoAbaixoMediaAdapter;
import dao.NFe_DAO;
import entidade.NFe;
import entidade.ProdutoAbaixoMedia;
import entidade.Usuario;
import entidade.Utils;

public class Minhas_NFes extends NavActivity {

    private Usuario usuario;
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
        getLayoutInflater().inflate(R.layout.activity_my_nfe, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(3).setChecked(true);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle(R.string.bar_my_nfe);

        progWait = findViewById(R.id.progWait);
        txtWait = findViewById(R.id.txtWait);

        alpha_in = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        alpha_out = AnimationUtils.loadAnimation(this, R.anim.alpha_out);

        //recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        ProdutoAbaixoMediaAdapter tmp_adapter = new ProdutoAbaixoMediaAdapter(this, new ArrayList<ProdutoAbaixoMedia>());
        recyclerView.setAdapter(tmp_adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        carregaMinhasNFes();
    }


    @SuppressLint("StaticFieldLeak")
    private void carregaMinhasNFes() {
        if (usuario == null) {
            usuario = Utils.loadFromSharedPreferences(this);
        }

        if (usuario != null && usuario.getId_usuario() > 0) {
            new AsyncTask<String, Void, ArrayList<NFe>>() {

                @Override
                protected void onPreExecute() {
                    progWait.setVisibility(View.VISIBLE);
                    txtWait.setVisibility(View.VISIBLE);
                    super.onPreExecute();
                }

                @Override
                protected ArrayList<NFe> doInBackground(String... params) {
                    ArrayList<NFe> list = null;
                    if (Utils.servidorDePe()) {

                        try {
                            String urlParameters = "funcao=GET_ALL_BY_ID_USUARIO&id_usuario=" + usuario.getId_usuario();
                            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                            URL url = new URL(Utils.URL + "nfe");
                            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                            urlCon.setRequestMethod("POST");
                            urlCon.setDoOutput(true);
                            urlCon.setDoInput(true);

                            DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
                            wr.write(postData);
                            wr.close();
                            wr.flush();

                            ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream());
                            list = (ArrayList<NFe>) ois.readObject();
                            ois.close();

                        } catch (ClassNotFoundException | IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // SE O SERVIDOR NÃO ESTIVER DE PÉ OU NÃO HOUVER CONEXÃO COM INTERNET, TENTA PEGAR AS NFES DO APP.
                        list = new NFe_DAO(Minhas_NFes.this).getAllNFes();
                    }
                    return list;
                }

                @Override
                protected void onPostExecute(final ArrayList<NFe> list) {
                    progWait.setVisibility(View.GONE);
                    txtWait.setVisibility(View.GONE);
                    if (list != null) {
                        Toast.makeText(Minhas_NFes.this, list.size() + " NFes Encontrados", Toast.LENGTH_LONG).show();
                        NFeAdapter adapter = new NFeAdapter(Minhas_NFes.this, list);
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

                        //Thread para sincronizar as nfes do servidor com as nfes locais
                        new Thread() {
                            public void run() {
                                NFe_DAO nFe_dao = new NFe_DAO(getApplicationContext());
                                ArrayList<Long> lista_ids_nfes = nFe_dao.getIdsNFesCadastradasLocamente();
                                if(lista_ids_nfes.size() < list.size()){
                                    if(lista_ids_nfes.size() == 0) {
                                        nFe_dao.insertAllFromServer(list);
                                    }else{
                                        int cadastros = 0;
                                        for (int i = 0; i < list.size(); i++) {
                                            if (!lista_ids_nfes.contains(list.get(i).getId_nfe()) && cadastros < 20) {
                                                nFe_dao.insertFromServer(list.get(i));
                                                cadastros++;
                                            }
                                        }
                                    }
                                }
                            }
                        }.start();

                    } else {
                        Toast.makeText(Minhas_NFes.this, "Nenhuma NFe Encontrada", Toast.LENGTH_LONG).show();
                    }
                }
            }.execute();
        } else {
            Toast.makeText(this, "Você não está logado!", Toast.LENGTH_LONG).show();
        }
    }


    //onBack
    @Override
    public void onBackPressed() {
        Intent main = new Intent(Minhas_NFes.this, MainActivity.class);
        startActivity(main);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}
