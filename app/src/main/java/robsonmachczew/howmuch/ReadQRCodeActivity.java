package robsonmachczew.howmuch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import java.util.List;

import adapter.ProductQRCodeAdapter;
import adapter.ProdutoAbaixoMediaAdapter;
import entidade.NFe;
import entidade.ProdutoAbaixoMedia;

public class ReadQRCodeActivity extends NavActivity {

    private ProgressBar progWait;
    private TextView txtResult, txtWait;
    private MaterialSearchView searchView;
    private RecyclerView recyclerView;
    private final Activity activity = this;
    public Animation alpha_in, alpha_out;

    private List<ProductQRCode> productList;

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
        txtResult = findViewById(R.id.txtResult);
        txtResult.setVisibility(View.GONE);

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
            protected NFe doInBackground(String... params) {
                NFe nfe = null;
                try {
                    String urlParameters = "chavenfe=" + code;
                    byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                    URL url = new URL("http://187.181.170.135:8080/Mercado/enviar_id_nfe");
                    HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                    urlCon.setRequestMethod("POST");
                    urlCon.setDoOutput(true); // Habilita o envio da chave por stream
                    urlCon.setDoInput(true); // Habilita o recebimento via stream

                    System.out.println("ERROR Send");
                    DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream()); // Stream que envia a chave para o servidor
                    wr.write(postData); // Envia a chave
                    wr.close();
                    wr.flush();

                    System.out.println("ERROR Receive");
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
                rvListQRCode(nfe);
            }
        }.execute(code);
    }

    public void rvListQRCode(NFe nfe) {
        productList = new ArrayList<>();
        //productList.add( new ProductQRCode( 1,"Vinho Hu 0",20.00,"10/09/2018 00:00:00",200.00, R.drawable.market_carrefour));

        for (int i = 0; i < nfe.getLista_items().size(); i++) {
            int prodId = (int) nfe.getLista_items().get(i).getProduto().getId_produto();
            String prodDesc = nfe.getLista_items().get(i).getProduto().getDescricao();
            Double prodPrice = Double.valueOf(nfe.getLista_items().get(i).getValor());
            String prodDate = nfe.getData();

            String markName = nfe.getMercado().getNome_fantasia();
            if (markName.contains("BELTRAME")) {
                productList.add(new ProductQRCode(prodId, prodDesc, prodPrice - 1, prodDate, prodPrice, R.drawable.market_beltrame));
            } else if (markName.contains("CARREFOUR")) {
                productList.add(new ProductQRCode(prodId, prodDesc, prodPrice - 1, prodDate, prodPrice, R.drawable.market_carrefour));
            } else if (markName.contains("BIG")) {
                productList.add(new ProductQRCode(prodId, prodDesc, prodPrice - 1, prodDate, prodPrice, R.drawable.market_big));
            } else {
                productList.add(new ProductQRCode(prodId, prodDesc, prodPrice - 1, prodDate, prodPrice, R.drawable.market_market));
            }

        }

        Toast.makeText(ReadQRCodeActivity.this, nfe.getLista_items().size() + " itens encontrados", Toast.LENGTH_LONG).show();

        ProductQRCodeAdapter adapter = new ProductQRCodeAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        //hide floating button when scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });
    }


    //onBack
    @Override
    public void onBackPressed() {
        Intent off = new Intent(ReadQRCodeActivity.this, OffActivity.class);
        startActivity(off);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}