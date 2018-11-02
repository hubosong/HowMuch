package robsonmachczew.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import adapter.ComparacaoListasAdapter;
import adapter.ProdutoAbaixoMediaAdapter;
import entidade.Lista;
import entidade.ProdutoAbaixoMedia;

public class VerComparacaoLista extends NavActivity {

    private ProgressBar progWait;
    private TextView txtQRCode, txtWait, txtMercado, txtData;
    //private MaterialSearchView searchView;
    private RecyclerView recyclerView;
    private final Activity activity = this;
    public Animation alpha_in, alpha_out;
    private ArrayList<Lista> listas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_ver_comparacao_lista, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle(R.string.bar_qrcode);

        listas = (ArrayList<Lista>) getIntent().getSerializableExtra("LISTAS");

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

        verComparacaoListas();
    }

    private void verComparacaoListas() {
        ComparacaoListasAdapter adapter = new ComparacaoListasAdapter(this, listas);
        recyclerView.setAdapter(adapter);

        //hide floating button when scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                /*if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                    fab.startAnimation(alpha_out);
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                    fab.startAnimation(alpha_in);
                }*/
            }
        });
    }

}
