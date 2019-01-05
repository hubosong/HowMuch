package robsonmachczew.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;

import adapter.Item_NFeAdapter;
import adapter.ProdutoAbaixoMediaAdapter;
import entidade.Item_NFe;
import entidade.Lista;
import entidade.Produto;
import entidade.ProdutoAbaixoMedia;
import entidade.Usuario;
import entidade.Utils;

public class Descontos extends Nav {

    private LinearLayout layout_produtos_desconto;
    private TextView tv_quant_prods_desconto;
    private ArrayList<Lista> lista_de_listas;
    private Usuario usuario;

    private void pegaListas() {
        if (usuario == null) {
            usuario = Utils.loadFromSharedPreferences(this);
        }
        if (usuario != null && usuario.getId_usuario() > 0) {
            new AsyncTask<String, Void, ArrayList<Lista>>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected ArrayList<Lista> doInBackground(String... params) {
                    ArrayList<Lista> list = null;
                    try {
                        URL url = new URL(Utils.URL + "lista");
                        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                        urlCon.setRequestMethod("POST");
                        urlCon.setDoOutput(true);
                        urlCon.setDoInput(true);

                        ObjectOutputStream wr = new ObjectOutputStream(urlCon.getOutputStream());
                        wr.writeUTF("GET_BY_ID_USUARIO");
                        wr.writeLong(usuario.getId_usuario());
                        wr.close();
                        wr.flush();

                        ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream());
                        list = (ArrayList<Lista>) ois.readObject();
                        ois.close();

                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                    return list;
                }

                @Override
                protected void onPostExecute(ArrayList<Lista> list) {
                    lista_de_listas = list;
                }
            }.execute();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pegaListas();

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_descontos, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle(R.string.bar_off);

        layout_produtos_desconto = findViewById(R.id.layout_prods_desconto);
        tv_quant_prods_desconto = findViewById(R.id.tv_quant_prods_abaixo_media);

        if (!Utils.estaConectado(this)) {
            Toast.makeText(this, "Sem conexão", Toast.LENGTH_LONG).show();
        } else {
            rvListStart();
        }
    }

    //recyclerview list off == Descontos
    @SuppressLint("StaticFieldLeak")
    public void rvListStart() {
        new AsyncTask<String, Void, ArrayList<ProdutoAbaixoMedia>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected ArrayList<ProdutoAbaixoMedia> doInBackground(String... params) {
                ArrayList<ProdutoAbaixoMedia> list = null;
                //if(Utils.servidorDePe()) {
                try {
                    String urlParameters = "funcao=GET_PRODUTOS_ABAIXO_MEDIA";
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
                    list = (ArrayList<ProdutoAbaixoMedia>) ois.readObject();
                    ois.close();

                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
                //}
                return list;
            }

            @Override
            protected void onPostExecute(ArrayList<ProdutoAbaixoMedia> list) {
                renderizaProdutosComDesconto(list);
            }
        }.execute();

    }

    private void renderizaProdutosComDesconto(ArrayList<ProdutoAbaixoMedia> list) {
        if (list != null) {
            tv_quant_prods_desconto.setText("Produtos Abaixo do Valor Médio (" + list.size() + "):");
            layout_produtos_desconto.removeAllViews();
            DecimalFormat df = new DecimalFormat("0.00");
            for (final ProdutoAbaixoMedia produto : list) {
                View item; // Creating an instance for View Object
                LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                item = inflater.inflate(R.layout.layout_products, null);
                ((TextView) item.findViewById(R.id.txtNomeProduto)).setText(produto.getDescricao_produto());
                ((TextView) item.findViewById(R.id.txtNomeMercado)).setText(produto.getNome_mercado());
                ((TextView) item.findViewById(R.id.txtDataNFe)).setText(produto.getData());
                ((TextView) item.findViewById(R.id.txtMediumPrice)).setText("R$: " + produto.getValor_medio());
                ((TextView) item.findViewById(R.id.txtOff)).setText("R$: " + df.format(produto.getValor_medio() - produto.getValor()).replace(",", "."));
                ((TextView) item.findViewById(R.id.txtPrice)).setText("R$: " + produto.getValor());
                ((TextView) item.findViewById(R.id.txtOptions)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog_opcoes_produto = new Dialog(Descontos.this);
                        dialog_opcoes_produto.setContentView(R.layout.dialog_opcoes_produto_abaixo_media);
                        ((Button) dialog_opcoes_produto.findViewById(R.id.bt_adiciona_produto_nova_lista)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Descontos.this, Lista_Compras.class);
                                intent.putExtra("PRODUTO", produto);
                                startActivity(intent);
                            }
                        });
                        ((Button) dialog_opcoes_produto.findViewById(R.id.bt_adiciona_produto_lista_existente)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (lista_de_listas != null) {
                                    Dialog dialog_adicionar_produto_lista = new Dialog(Descontos.this);
                                    dialog_adicionar_produto_lista.setContentView(R.layout.dialog_pesquisa_lista_de_listas_add_produto);
                                    ((TextView) dialog_adicionar_produto_lista.findViewById(R.id.txtTituloDialog)).setText("Adicionar \"" + produto.getDescricao_produto() + "\" à Lista:");
                                    final LinearLayout layout_lista_de_listas = dialog_adicionar_produto_lista.findViewById(R.id.layout_lista_de_listas);
                                    layout_lista_de_listas.removeAllViews();
                                    for (Lista lista : lista_de_listas) {
                                        CheckBox cb = new CheckBox(dialog_adicionar_produto_lista.getContext());
                                        cb.setText(lista.getNome() + " " + lista.getData());
                                        cb.setTextColor(Color.WHITE);
                                        layout_lista_de_listas.addView(cb);
                                    }
                                    ((Button) dialog_adicionar_produto_lista.findViewById(R.id.bt_AdicionarProdutoLista)).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            ArrayList<Long> ids_listas = new ArrayList<>();
                                            for(int i=0; i<layout_lista_de_listas.getChildCount(); i++){
                                                if( ((CheckBox) layout_lista_de_listas.getChildAt(i)).isChecked()){
                                                    ids_listas.add(lista_de_listas.get(i).getId_lista());
                                                }
                                            }
                                        }
                                    });
                                    dialog_opcoes_produto.cancel();
                                    dialog_adicionar_produto_lista.show();
                                }
                            }
                        });
                        dialog_opcoes_produto.show();
                    }
                });
                layout_produtos_desconto.addView(item);
            }
            layout_produtos_desconto.requestFocus();
        } else {
            Toast.makeText(Descontos.this, "Nenhum Item Encontrado", Toast.LENGTH_LONG).show();
        }
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    //onBack
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent main = new Intent(Descontos.this, Main.class);
        startActivity(main);
        finish();

    }

}
