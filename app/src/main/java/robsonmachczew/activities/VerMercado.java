package robsonmachczew.activities;

import android.os.Bundle;
import android.widget.FrameLayout;

import entidade.Mercado;

public class VerMercado extends Nav {

    private Mercado mercado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_ver_mercado, contentFrameLayout);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Detalhes Do Mercado");

        mercado = (Mercado) getIntent().getSerializableExtra("MERCADO");
        renderizaMercado();
    }

    private void renderizaMercado() {
        if (mercado != null) {

        }
    }
}
