package com.granbyte.gasto_pouco;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import dao.NCM_DAO;
import entidade.Item_NFe;
import entidade.NCM;
import entidade.Produto_Detalhado;
import entidade.Usuario;
import entidade.Utils;

public class VerProduto extends Nav {

    private Produto_Detalhado produto;
    private NCM ncm;
    private Usuario usuario;
    private TextView tv_descricao_produto;
    private TextView tv_unidade_comercial;
    private TextView tv_codigo_ncm;
    private TextView tv_codigo_ean;
    private TextView tv_categoria;
    private TextView tv_descricao;
    private LinearLayout layout_precos_mercado;


    private DecimalFormat decFormat = new DecimalFormat("'R$ ' #,##0.00");
    private SimpleDateFormat sdf_exibicao = new SimpleDateFormat("dd/MM/yyyy hh:mm");
    private SimpleDateFormat sdf_bd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_ver_produto, contentFrameLayout);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Detalhes do Produto");

        tv_descricao_produto = findViewById(R.id.tv_descricao_produto);
        tv_unidade_comercial = findViewById(R.id.unidade_comercial);
        tv_codigo_ncm = findViewById(R.id.codigo_ncm);
        tv_codigo_ean = findViewById(R.id.cod_EAN_comercial);
        tv_categoria = findViewById(R.id.categoria);
        tv_descricao = findViewById(R.id.descricao);
        layout_precos_mercado = findViewById(R.id.layout_precos_mercados);

        usuario = Utils.loadFromSharedPreferences(this);

        produto = (Produto_Detalhado) getIntent().getSerializableExtra("PRODUTO_DETALHADO");
        if (produto == null) {
            getDetalhesProduto(getIntent().getLongExtra("ID_PRODUTO", 0));
        } else {
            if(produto.getCodigo_ncm() != 0){
                ncm = new NCM_DAO(this).getByNCM(produto.getCodigo_ncm());
            }
            renderizaProduto_Detalhado();
        }
    }

    @SuppressLint("SetTextI18n")
    private void renderizaProduto_Detalhado() {
        if (produto != null) {

            System.out.println(produto.toString());
            tv_descricao_produto.setText(produto.getDescricao());
            tv_codigo_ean.setText("" + produto.getCod_EAN_comercial());
            tv_codigo_ncm.setText("" + produto.getCodigo_ncm());
            tv_unidade_comercial.setText(produto.getUnidade_comercial());
            if(ncm != null){
                tv_categoria.setText(ncm.getCategoria());
                tv_descricao.setText(ncm.getDescricao());
            }
            layout_precos_mercado.removeAllViews();
            try {
                for (Item_NFe item : produto.getLista_itens_nfe()) {
                    View view; // Creating an instance for View Object
                    LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.layout_preco_atual_mercado, null);
                    ((TextView) view.findViewById(R.id.txt_nome_mercado)).setText(item.getTransient_mercado().getNome());
                    Date date = sdf_bd.parse(item.getData());
                    ((TextView) view.findViewById(R.id.txt_data_valor)).setText("Valor em " + sdf_exibicao.format(date));
                    ((TextView) view.findViewById(R.id.txt_valor)).setText(decFormat.format(item.getValor() / item.getQuantidade()));
                    layout_precos_mercado.addView(view);
                }
            }catch (Exception e){
                System.out.println(">>> Erro mostrando ultimos preços de produtos em detalhes: "+e.getMessage());
                e.printStackTrace();
            }
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
                    String urlParameters = "funcao=GET_PRODUTO_DETALHADO&id_produto=" + id_produto;
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
                    if(produto.getCodigo_ncm() != 0){
                        ncm = new NCM_DAO(VerProduto.this).getByNCM(produto.getCodigo_ncm());
                    }
                    renderizaProduto_Detalhado();
                }
            }
        }.execute();

    }


    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }
}
