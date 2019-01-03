package robsonmachczew.activities;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.Display;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import entidade.Usuario;
import entidade.Utils;

public class Lista_Compras extends Nav {

    private Usuario usuario;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_lista__compras, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Lista Compras");

        usuario = Utils.loadFromSharedPreferences(this);
        scrollView = findViewById(R.id.scroll_prods_lista);


        if(!Utils.estaConectado(this)){
            Toast.makeText(this, "Sem conexão", Toast.LENGTH_LONG).show();
            return;
        }
    }

    public void adicionarProduto(View v){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_pesquisa_produtos_lista);
        dialog.setTitle("Adicionar Produto à Lista");
        dialog.getWindow().setLayout(width-16, height-250);
        TextView text = (TextView) dialog.findViewById(R.id.editText_Procura_Prod_Lista);
        dialog.show();
    }

}
