package robsonmachczew.activities;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import entidade.Item_NFe;
import entidade.Produto;
import entidade.Usuario;
import entidade.Utils;

public class VerProduto extends AppCompatActivity {

    private Item_NFe item_nFe;
    private Produto produto;

    private ProgressBar progWait;
    private TextView txtQRCode, txtWait, txtMercado, txtData;
    private MaterialSearchView searchView;
    private RecyclerView recyclerView;
    public Animation alpha_in, alpha_out;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_ver_produto);
        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_ver_produto, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Detalhes de Produto");

        getIntent().getSerializableExtra("ITEM");
        if(item_nFe != null){
            produto = item_nFe.getProduto();
        }

        alpha_in = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        alpha_out = AnimationUtils.loadAnimation(this, R.anim.alpha_out);

        progWait = findViewById(R.id.progWait);
        txtWait = findViewById(R.id.txtWait);
        txtQRCode = findViewById(R.id.txtQRCode);
        txtMercado = findViewById(R.id.txtDataLista);
        txtData = findViewById(R.id.txtQtdItems);

        usuario = Utils.loadFromSharedPreferences(this);

        //recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
