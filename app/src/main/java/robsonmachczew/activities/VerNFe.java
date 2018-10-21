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

import adapter.ItemNFeAdapter;
import adapter.ProdutoAbaixoMediaAdapter;
import entidade.NFe;
import entidade.ProdutoAbaixoMedia;
import entidade.Utils;

public class VerNFe extends NavActivity {

    private ProgressBar progWait;
    private TextView txtQRCode, txtWait, txtMercado, txtData;
    private MaterialSearchView searchView;
    private RecyclerView recyclerView;
    private final Activity activity = this;
    public Animation alpha_in, alpha_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_read_qrcode, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle(R.string.bar_qrcode);

        alpha_in = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        alpha_out = AnimationUtils.loadAnimation(this, R.anim.alpha_out);

        progWait = findViewById(R.id.progWait);
        txtWait = findViewById(R.id.txtWait);
        txtQRCode = findViewById(R.id.txtQRCode);
        txtMercado = findViewById(R.id.txtDataLista);
        txtData = findViewById(R.id.txtQtdItems);

        //recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        ProdutoAbaixoMediaAdapter tmp_adapter = new ProdutoAbaixoMediaAdapter(this, new ArrayList<ProdutoAbaixoMedia>());
        recyclerView.setAdapter(tmp_adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String code = getIntent().getStringExtra("code");
        postHttpQRCode(code);
    }


    @SuppressLint("StaticFieldLeak")
    public void postHttpQRCode(final String code) {
        new AsyncTask<String, Void, NFe>() {
            @Override
            protected void onPreExecute() {
                progWait.setVisibility(View.VISIBLE);
                txtWait.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected NFe doInBackground(String... params) {
                NFe nfe = null;
                try {
                    String urlParameters = "chavenfe=" + code;
                    System.out.println("Enviando chave: "+code);
                    byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                    URL url = new URL(Utils.URL+"enviar_id_nfe");
                    HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                    urlCon.setRequestMethod("POST");
                    urlCon.setDoOutput(true); // Habilita o envio da chave por stream
                    urlCon.setDoInput(true); // Habilita o recebimento via stream

                    DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream()); // Stream que envia a chave para o servidor
                    wr.write(postData); // Envia a chave
                    wr.close();
                    wr.flush();

                    ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream()); // Stream que vai receber um objeto do tipo NFe
                    nfe = (NFe) ois.readObject();
                    ois.close();

                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }

                return nfe;
            }

            @Override
            protected void onPostExecute(NFe nfe) {
                txtQRCode.setText(code);
                preencherViewsProdutosNFe(nfe);
            }
        }.execute(code);
    }


    public void preencherViewsProdutosNFe(NFe nfe) {
        progWait.setVisibility(View.GONE);
        txtWait.setVisibility(View.GONE);
        txtMercado.setText(nfe.getMercado().getNome());
        txtData.setText(nfe.getData());

        TextView txtMarket = findViewById(R.id.txtDataLista);
        TextView txtDate = findViewById(R.id.txtQtdItems);

        Toast.makeText(VerNFe.this, nfe.getLista_items().size() + " itens encontrados", Toast.LENGTH_LONG).show();

        ItemNFeAdapter adapter = new ItemNFeAdapter(this, nfe.getLista_items());
        recyclerView.setAdapter(adapter);

        txtMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VerNFe.this, R.string.toast_error, Toast.LENGTH_SHORT).show();
            }
        });

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
    }


    //onBack
    @Override
    public void onBackPressed() {
        Intent off = new Intent(VerNFe.this, Descontos.class);
        startActivity(off);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}