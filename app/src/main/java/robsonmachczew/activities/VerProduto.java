package robsonmachczew.activities;

import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import entidade.Produto;
import entidade.Usuario;
import entidade.Utils;

public class VerProduto extends Nav {

    private Produto produto;
    private Usuario usuario;
    private TextView tv_descricao_produto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_ver_produto, contentFrameLayout);
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

}
