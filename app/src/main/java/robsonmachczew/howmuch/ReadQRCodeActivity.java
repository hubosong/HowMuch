package robsonmachczew.howmuch;

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
import java.util.List;

import adapter.ProdutoAbaixoMediaAdapter;
import adapter.ReadQRCodeAdapter;
import entidade.NFe;
import entidade.ProdutoAbaixoMedia;

public class ReadQRCodeActivity extends NavActivity {

    private ProgressBar progWait;
    private TextView txtQRCode, txtWait;
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
        txtQRCode = findViewById(R.id.txtQRCode);

        //recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        ProdutoAbaixoMediaAdapter tmp_adapter = new ProdutoAbaixoMediaAdapter(this, new ArrayList<ProdutoAbaixoMedia>());
        recyclerView.setAdapter(tmp_adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String code = getIntent().getStringExtra("code");
        postHttpQRCode(code);

        //rvlistQRTeste(code);

    }

    @SuppressLint("StaticFieldLeak")
    public void rvlistQRTeste(final String code){
        new AsyncTask<String, Void, ArrayList<ProductQRCode>>() {
            @Override
            protected ArrayList<ProductQRCode> doInBackground(String... strings) {

                txtQRCode.setText(code);
                TextView txtMarket = findViewById(R.id.txtMarket);
                TextView txtDate = findViewById(R.id.txtDate);
                txtMarket.setText("Mercado Hu");
                txtDate.setText("10/09/2018 00:00:00");

                txtMarket.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ReadQRCodeActivity.this, R.string.toast_error, Toast.LENGTH_SHORT).show();
                    }
                });

                TextView prodHowMany = findViewById(R.id.txtHowMany);

                ArrayList<ProductQRCode>productList = new ArrayList<>();
                productList.add( new ProductQRCode( 1,"Vinho Hu", "Mercado Hu",220.00,"10/09/2018 00:00:00",200.00, R.drawable.market_carrefour, 2, 3));
                productList.add( new ProductQRCode( 1,"Cerveja Hu", "Mercado Bo",220.00,"10/09/2018 00:00:00",200.00, R.drawable.market_carrefour, 2, 3));
                productList.add( new ProductQRCode( 1,"Arroz Hu", "Mercado Song",220.00,"10/09/2018 00:00:00",300.00, R.drawable.market_carrefour, 2, 3));
                productList.add( new ProductQRCode( 1,"Massa Hu", "Mercado Hu",190.00,"10/09/2018 00:00:00",500.00, R.drawable.market_carrefour, 2, 3));
                productList.add( new ProductQRCode( 1,"Picanha Hu", "Mercado Song",220.00,"10/09/2018 00:00:00",100.00, R.drawable.market_carrefour, 2, 3));
                productList.add( new ProductQRCode( 1,"Agua Hu", "Mercado Hu",220.00,"10/09/2018 00:00:00",200.00, R.drawable.market_carrefour, 2, 3));
                productList.add( new ProductQRCode( 1,"Refrigerante Hu", "Mercado Bo",100.00,"10/09/2018 00:00:00",250.00, R.drawable.market_carrefour, 2, 3));

                ReadQRCodeAdapter adapter = new ReadQRCodeAdapter(ReadQRCodeActivity.this, productList);
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

                return null;
            }
        }.execute();
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
                txtQRCode.setText(code);
                rvListQRCode(nfe);
            }
        }.execute(code);
    }


    public void rvListQRCode(NFe nfe) {
        progWait.setVisibility(View.GONE);
        txtWait.setVisibility(View.GONE);

        productList = new ArrayList<>();

        TextView txtMarket = findViewById(R.id.txtMarket);
        TextView txtDate = findViewById(R.id.txtDate);

        for (int i = 0; i < nfe.getLista_items().size(); i++) {

            int prodId = (int) nfe.getLista_items().get(i).getProduto().getId_produto();
            String prodDesc = nfe.getLista_items().get(i).getProduto().getDescricao();
            String prodMarket = nfe.getLista_items().get(i).getMercado();
            Double prodPrice = (double) nfe.getLista_items().get(i).getValor();
            String prodDate = nfe.getData();
            String markName = nfe.getMercado().getNome_fantasia();

            float prodHowMany = nfe.getLista_items().get(i).getQuantidade();
            float prodUnitPrice = (float) (prodPrice / prodHowMany);

            txtMarket.setText(markName);
            txtDate.setText(prodDate);

            productList.add( new ProductQRCode(prodId, prodDesc, prodMarket, prodPrice, prodDate, prodPrice, R.drawable.market, prodUnitPrice, prodHowMany));
        }

        Toast.makeText(ReadQRCodeActivity.this, nfe.getLista_items().size() + " itens encontrados", Toast.LENGTH_LONG).show();

        ReadQRCodeAdapter adapter = new ReadQRCodeAdapter(this, productList);
        recyclerView.setAdapter(adapter);

        txtMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ReadQRCodeActivity.this, R.string.toast_error, Toast.LENGTH_SHORT).show();
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
        Intent off = new Intent(ReadQRCodeActivity.this, OffActivity.class);
        startActivity(off);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}