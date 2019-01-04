package robsonmachczew.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

import entidade.Lista;
import entidade.Produto;
import entidade.Usuario;
import entidade.Utils;

public class Lista_Compras extends Nav {

    private Usuario usuario;
    private Lista lista_compras;
    private Dialog dialog_pesquisa;
    private LinearLayout layout_produtos_lista;
    private TextView tvQuantProdutosLista;

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
        layout_produtos_lista = findViewById(R.id.layout_produtos_da_lista);
        tvQuantProdutosLista = findViewById(R.id.textView2);

        if(!Utils.estaConectado(this)){
            Toast.makeText(this, "Sem conexão", Toast.LENGTH_LONG).show();
            return;
        }
        if(lista_compras == null){
            lista_compras = new Lista();
        }
    }

    public void adicionarProduto(View v){
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        dialog_pesquisa = new Dialog(this);
        dialog_pesquisa.setContentView(R.layout.dialog_pesquisa_produtos_lista);
        dialog_pesquisa.setTitle("Adicionar Produto à Lista");
        dialog_pesquisa.getWindow().setLayout(width-16, height-250);
        EditText text = dialog_pesquisa.findViewById(R.id.editText_Procura_Prod_Lista);
        final LinearLayout layoutPesq = dialog_pesquisa.findViewById(R.id.layout_produtos_pesquisados);
        final Button btConcluir = dialog_pesquisa.findViewById(R.id.btConcluirPesq);
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    realizaPesquisaProdutos(s.toString(), layoutPesq, btConcluir);
                }
            }
        });
        dialog_pesquisa.show();
    }

    @SuppressLint("StaticFieldLeak")
    private void realizaPesquisaProdutos(final String pesquisa, final LinearLayout layoutProdutos, final Button btConcluir) {

        new AsyncTask<String, Void, ArrayList<Produto>>() {
            @Override
            protected void onPreExecute() {
                layoutProdutos.removeAllViews();
                TextView txtCarregando = new TextView(Lista_Compras.this);
                txtCarregando.setText(R.string.txt_progress);
                txtCarregando.setTextSize(16);
                txtCarregando.setTextColor(Color.parseColor("#ffffff"));
                layoutProdutos.addView(txtCarregando);
                super.onPreExecute();
            }

            @Override
            protected ArrayList<Produto> doInBackground(String... params) {
                ArrayList<Produto> list = null;
                try {
                    String urlParameters = "funcao=GET_BY_DESCRICAO&descricao=" + pesquisa;
                    byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                    URL url = new URL(Utils.URL + "produto");
                    HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                    urlCon.setRequestMethod("POST");
                    urlCon.setDoOutput(true);
                    urlCon.setDoInput(true);

                    DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
                    wr.write(postData);
                    wr.close();
                    wr.flush();

                    ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream());
                    list = (ArrayList<Produto>) ois.readObject();
                    ois.close();

                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
                return list;
            }

            @Override
            protected void onPostExecute(final ArrayList<Produto> list) {
                layoutProdutos.removeAllViews();
                for (final Produto produto : list) {
                    CheckBox cb = new CheckBox(Lista_Compras.this);
                    cb.setText(produto.getDescricao());
                    cb.setTextColor(Color.parseColor("#ffffff"));
                    layoutProdutos.addView(cb);
                }
                btConcluir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for(int i=0; i<layoutProdutos.getChildCount(); i++){
                            if( ((CheckBox) layoutProdutos.getChildAt(i)).isChecked()){
                                lista_compras.getListaProdutos().add(list.get(i));
                            }
                        }
                        atualizaListaProdutos();
                    }
                });
            }
        }.execute();
    }

    private void atualizaListaProdutos(){
        dialog_pesquisa.dismiss();
        tvQuantProdutosLista.setText("Produtos da Lista ("+lista_compras.getListaProdutos().size()+"):");
        layout_produtos_lista.removeAllViews();
        for(Produto prod : lista_compras.getListaProdutos()){
            TextView tv = new TextView(this);
            tv.setText(prod.getDescricao());
            tv.setTextColor(Color.WHITE);
            layout_produtos_lista.addView(tv);
        }
    }
}
