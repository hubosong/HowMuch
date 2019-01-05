package robsonmachczew.activities;

import android.content.Intent;
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

    private Produto produto;
    private Usuario usuario;
    private TextView tv_descricao_produto;

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

        tv_descricao_produto = findViewById(R.id.tv_descricao_produto);

        usuario = Utils.loadFromSharedPreferences(this);
        produto = (Produto) getIntent().getSerializableExtra("PRODUTO");

        renderizaProduto();
    }

    private void renderizaProduto(){
        if(produto != null){
            tv_descricao_produto.setText(produto.getDescricao());
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Descontos.class);
        startActivity(intent);
        finish();
    }
}
