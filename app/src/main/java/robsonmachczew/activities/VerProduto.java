package robsonmachczew.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import entidade.Item_NFe;
import entidade.ProdutoAbaixoMedia;
import entidade.Produto_Detalhado;
import entidade.Usuario;
import entidade.Utils;

public class VerProduto extends Nav {

    private Produto_Detalhado produto;
    private Usuario usuario;
    private TextView tv_descricao_produto;

    private Item_NFe ultimo_preco;
    private Item_NFe menor_preco_historico;
    private Item_NFe maior_preco_historico;
    private Item_NFe menor_preco_atual;
    private Item_NFe maior_preco_atual;


    private TextView txt_menor_valor_historico;
    private TextView txt_menor_valor_atual;
    private TextView txt_valor_medio;
    private TextView txt_maior_valor_historico;
    private TextView txt_maior_valor_atual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_ver_produto, contentFrameLayout);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Detalhes do Produto");

        tv_descricao_produto = findViewById(R.id.tv_descricao_produto);
        txt_menor_valor_historico = findViewById(R.id.textView6);
        txt_menor_valor_atual = findViewById(R.id.textView9);
        txt_valor_medio = findViewById(R.id.textView4);
        txt_maior_valor_historico = findViewById(R.id.textView5);
        txt_maior_valor_atual = findViewById(R.id.textView7);

        usuario = Utils.loadFromSharedPreferences(this);
        getDetalhesProduto(getIntent().getLongExtra("ID_PRODUTO", 0));
    }

    private void renderizaProduto_Detalhado() {
        if (produto != null) {
            tv_descricao_produto.setText(produto.getDescricao());
            float media = 0.0f;
            float quantidade = 0.0f;
            if (produto.getLista_itens_nfe() != null && produto.getLista_itens_nfe().size() > 0) {
                ultimo_preco = produto.getLista_itens_nfe().get(0);
                menor_preco_historico = produto.getLista_itens_nfe().get(0);
                maior_preco_historico = produto.getLista_itens_nfe().get(0);
                maior_preco_atual = produto.getLista_itens_nfe().get(0);
                menor_preco_atual = produto.getLista_itens_nfe().get(0);
                for (Item_NFe item : produto.getLista_itens_nfe()) {
                    System.out.println("ITEM: "+(item.getValor() / item.getQuantidade())+" - "+item.getData());
                    if ((item.getValor() / item.getQuantidade()) < (menor_preco_historico.getValor() / menor_preco_historico.getQuantidade())) {
                        menor_preco_historico = item;
                    }
                    if ((item.getValor() / item.getQuantidade()) > (maior_preco_historico.getValor() / maior_preco_historico.getQuantidade())) {
                        maior_preco_historico = item;
                    }
                    if (((item.getValor() / item.getQuantidade()) < (menor_preco_atual.getValor() / menor_preco_atual.getQuantidade())) && (menor_preco_atual.getData().compareTo(item.getData()) < 0)) {
                        menor_preco_atual = item;
                    }
                    if (((item.getValor() / item.getQuantidade()) > (maior_preco_atual.getValor() / maior_preco_atual.getQuantidade())) && (maior_preco_atual.getData().compareTo(item.getData()) < 0)) {
                        maior_preco_atual = item;
                    }
                    if (ultimo_preco.getData().compareTo(menor_preco_historico.getData()) < 0) {
                        ultimo_preco = item;
                    }
                    if (item.getTransient_mercado().getNome_fantasia() == null || item.getTransient_mercado().getNome_fantasia().trim().equalsIgnoreCase("")) {
                        item.getTransient_mercado().setNome_fantasia(item.getTransient_mercado().getNome());
                    }
                    media += item.getValor();
                    quantidade += item.getQuantidade();
                }
            }
            txt_menor_valor_historico.setText("R$: " + (menor_preco_historico.getValor() / menor_preco_historico.getQuantidade()) + " - " + menor_preco_historico.getTransient_mercado().getNome_fantasia());
            txt_menor_valor_atual.setText("R$: " + (menor_preco_atual.getValor() / menor_preco_atual.getQuantidade()) + " - " + menor_preco_atual.getTransient_mercado().getNome_fantasia());
            txt_valor_medio.setText("R$: " + media/quantidade);
            txt_maior_valor_atual.setText("R$: " + (maior_preco_atual.getValor() / maior_preco_atual.getQuantidade()) + " - " + maior_preco_atual.getTransient_mercado().getNome_fantasia());
            txt_maior_valor_historico.setText("R$: " + (maior_preco_historico.getValor() / maior_preco_historico.getQuantidade()) + " - " + maior_preco_historico.getTransient_mercado().getNome_fantasia());
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void getDetalhesProduto(final long id_produto) {
        new AsyncTask<String, Void, Produto_Detalhado>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Produto_Detalhado doInBackground(String... params) {
                Produto_Detalhado prod = null;
                //if(Utils.servidorDePe()) {
                try {
                    String urlParameters = "funcao=GET_PRODUTO_DETALHADO&id_produto="+id_produto;
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
                    produto = (Produto_Detalhado) ois.readObject();
                    ois.close();

                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
                //}
                return produto;
            }

            @Override
            protected void onPostExecute(Produto_Detalhado prod) {
                produto = prod;
                if (produto != null) {
                    renderizaProduto_Detalhado();
                }
            }
        }.execute();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
