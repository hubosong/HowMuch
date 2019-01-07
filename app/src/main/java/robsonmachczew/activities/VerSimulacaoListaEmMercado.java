package robsonmachczew.activities;

import android.os.Bundle;
import android.widget.FrameLayout;

import entidade.Lista;
import entidade.Mercado;

public class VerSimulacaoListaEmMercado extends Nav {

    private Lista lista;
    private Mercado mercado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_ver_simulacao_lista_em_mercado, contentFrameLayout);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Simulação de Lista em Mercado");

        mercado = (Mercado) getIntent().getSerializableExtra("MERCADO");
        renderizaSimulacao();
    }

    private void renderizaSimulacao() {

    }
}
