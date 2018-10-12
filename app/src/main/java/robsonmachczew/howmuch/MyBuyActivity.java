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
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;

import adapter.ProdutoAbaixoMediaAdapter;
import adapter.ReadQRCodeAdapter;
import entidade.ProdutoAbaixoMedia;
import entidade.Usuario;

public class MyBuyActivity extends NavActivity {

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
        getLayoutInflater().inflate(R.layout.activity_my_buy, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle(R.string.bar_my_buys);

        progWait = findViewById(R.id.progWait);
        txtWait = findViewById(R.id.txtWait);


        //recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        ProdutoAbaixoMediaAdapter tmp_adapter = new ProdutoAbaixoMediaAdapter(this, new ArrayList<ProdutoAbaixoMedia>());
        recyclerView.setAdapter(tmp_adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        rvlistTeste();

    }


    @SuppressLint("StaticFieldLeak")
    public void rvlistTeste(){
        new AsyncTask<String, Void, ArrayList<ProductQRCode>>() {
            @Override
            protected ArrayList<ProductQRCode> doInBackground(String... strings) {
                ArrayList<ProductQRCode>productList = new ArrayList<>();
                productList.add( new ProductQRCode( 1,"Lista 1", "Mercado Hu",220.00,"10/09/2018 00:00:00",100.00, R.drawable.market_carrefour, 1, 1));

                ReadQRCodeAdapter adapter = new ReadQRCodeAdapter(MyBuyActivity.this, productList);

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


    //onBack
    @Override
    public void onBackPressed() {
        Intent main = new Intent(MyBuyActivity.this, MainActivity.class);
        startActivity(main);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
