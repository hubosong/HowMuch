package robsonmachczew.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;

import entidade.Item_NFe;
import entidade.Lista;
import entidade.Produto;
import entidade.ProdutoAbaixoMedia;
import entidade.Usuario;
import entidade.Utils;

public class Descontos extends Nav {

    private boolean permiteVoltar = false;
    private LinearLayout layout_produtos_desconto;
    private TextView tv_quant_prods_desconto;
    private EditText txt_pesquisa_produtos;
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
        permiteVoltar = getIntent().getBooleanExtra("PERMITE_VOLTAR", false);

        layout_produtos_desconto = findViewById(R.id.layout_prods_desconto);
        tv_quant_prods_desconto = findViewById(R.id.tv_quant_prods_abaixo_media);

        setEditTextProcuraProdutos();

        if (!Utils.estaConectado(this)) {
            Toast.makeText(this, "Sem conexão", Toast.LENGTH_LONG).show();
        } else {
            rvListStart();
        }
    }

    private void setEditTextProcuraProdutos() {
        txt_pesquisa_produtos = findViewById(R.id.editText);
        TextWatcher tw = new TextWatcher() {
            public void afterTextChanged(final Editable s) {
                if (s.length() > 2) {
                    new AsyncTask<String, Void, ArrayList<Item_NFe>>() {

                        @Override
                        protected ArrayList<Item_NFe> doInBackground(String... params) {
                            ArrayList<Item_NFe> list = null;
                            try {
                                String urlParameters = "funcao=GET_PRODUTOS_PESQUISA_APP&descricao=" + s;
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
                                list = (ArrayList<Item_NFe>) ois.readObject();
                                ois.close();

                            } catch (ClassNotFoundException | IOException e) {
                                e.printStackTrace();
                            }
                            return list;
                        }

                        @Override
                        protected void onPostExecute(ArrayList<Item_NFe> list) {
                            renderizaProdutosDaPesquisa(list);
                        }
                    }.execute();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // you can check for enter key here
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        };
        txt_pesquisa_produtos.addTextChangedListener(tw);
    }

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
                ((TextView) item.findViewById(R.id.txtPrice)).setText("R$: " + df.format(produto.getValor()).replace(",","."));
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog_opcoes_produto = new Dialog(Descontos.this);
                        dialog_opcoes_produto.setContentView(R.layout.dialog_opcoes_produto_abaixo_media);
                        ((Button) dialog_opcoes_produto.findViewById(R.id.bt_adiciona_produto_nova_lista)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Descontos.this, Criar_Lista_Compras.class);
                                intent.putExtra("PERMITE_VOLTAR", true);
                                Produto p = new Produto();
                                p.setId_produto(produto.getId_produto());
                                p.setDescricao(produto.getDescricao_produto());
                                p.setDescricao2(produto.getDescricao_produto2());
                                intent.putExtra("PRODUTO", p);
                                startActivity(intent);
                                dialog_opcoes_produto.cancel();
                            }
                        });
                        ((Button) dialog_opcoes_produto.findViewById(R.id.bt_adiciona_produto_lista_existente)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (lista_de_listas != null) {
                                    final Dialog dialog_adicionar_produto_lista = new Dialog(Descontos.this);
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
                                            for (int i = 0; i < layout_lista_de_listas.getChildCount(); i++) {
                                                if (((CheckBox) layout_lista_de_listas.getChildAt(i)).isChecked()) {
                                                    ids_listas.add(lista_de_listas.get(i).getId_lista());
                                                }
                                            }
                                            adicionarProdutoListas(ids_listas, produto.getId_produto());
                                            dialog_adicionar_produto_lista.cancel();
                                        }
                                    });
                                    dialog_opcoes_produto.cancel();
                                    dialog_adicionar_produto_lista.show();
                                }
                            }
                        });
                        ((Button) dialog_opcoes_produto.findViewById(R.id.bt_compartilhar_produto_abaixo_media)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                String s = "Olá, talvez você goste desta oferta: " + produto.getDescricao_produto() + " - R$ " + produto.getValor();
                                s += "\nBaixe o app HowMuch e confira: www.howmuch.com";
                                sendIntent.putExtra(Intent.EXTRA_TEXT, s);
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                                dialog_opcoes_produto.cancel();
                            }
                        });
                        ((Button) dialog_opcoes_produto.findViewById(R.id.bt_criar_alerta)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(Descontos.this, "Não Implementado", Toast.LENGTH_LONG).show();
                            }
                        });
                        ((Button) dialog_opcoes_produto.findViewById(R.id.bt_historico_precos_produto)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Descontos.this, VerProduto.class);
                                Produto p = new Produto();
                                p.setId_produto(produto.getId_produto());
                                p.setDescricao(produto.getDescricao_produto());
                                p.setUnidade_comercial(produto.getUnidade_comercial());
                                intent.putExtra("PRODUTO", p);
                                startActivity(intent);
                                dialog_opcoes_produto.cancel();
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

    private void renderizaProdutosDaPesquisa(ArrayList<Item_NFe> list) {
        if (list != null) {
            tv_quant_prods_desconto.setText("Produtos Encontrados (" + list.size() + "):");
            layout_produtos_desconto.removeAllViews();
            DecimalFormat df = new DecimalFormat("0.00");
            for (final Item_NFe item : list) {
                View view; // Creating an instance for View Object
                LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.layout_products, null);
                ((TextView) view.findViewById(R.id.txtNomeProduto)).setText(item.getProduto().getDescricao());
                ((TextView) view.findViewById(R.id.txtNomeMercado)).setText(item.getTransient_mercado().getNome());
                ((TextView) view.findViewById(R.id.txtDataNFe)).setText(item.getData());
                ((TextView) view.findViewById(R.id.txtMediumPrice)).setText("R$: -");
                ((TextView) view.findViewById(R.id.txtOff)).setText("R$: -");
                ((TextView) view.findViewById(R.id.txtPrice)).setText("R$: " + df.format(item.getValor()).replace(",","."));
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog_opcoes_produto = new Dialog(Descontos.this);
                        dialog_opcoes_produto.setContentView(R.layout.dialog_opcoes_produto_abaixo_media);
                        ((Button) dialog_opcoes_produto.findViewById(R.id.bt_adiciona_produto_nova_lista)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Descontos.this, Criar_Lista_Compras.class);
                                intent.putExtra("PERMITE_VOLTAR", true);
                                intent.putExtra("PRODUTO", item.getProduto());
                                startActivity(intent);
                                dialog_opcoes_produto.cancel();
                            }
                        });
                        ((Button) dialog_opcoes_produto.findViewById(R.id.bt_adiciona_produto_lista_existente)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (lista_de_listas != null) {
                                    final Dialog dialog_adicionar_produto_lista = new Dialog(Descontos.this);
                                    dialog_adicionar_produto_lista.setContentView(R.layout.dialog_pesquisa_lista_de_listas_add_produto);
                                    ((TextView) dialog_adicionar_produto_lista.findViewById(R.id.txtTituloDialog)).setText("Adicionar \"" + item.getProduto().getDescricao() + "\" à Lista:");
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
                                            for (int i = 0; i < layout_lista_de_listas.getChildCount(); i++) {
                                                if (((CheckBox) layout_lista_de_listas.getChildAt(i)).isChecked()) {
                                                    ids_listas.add(lista_de_listas.get(i).getId_lista());
                                                }
                                            }
                                            adicionarProdutoListas(ids_listas, item.getProduto().getId_produto());
                                            dialog_adicionar_produto_lista.cancel();
                                        }
                                    });
                                    dialog_opcoes_produto.cancel();
                                    dialog_adicionar_produto_lista.show();
                                }
                            }
                        });
                        ((Button) dialog_opcoes_produto.findViewById(R.id.bt_compartilhar_produto_abaixo_media)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                String s = "Olá, talvez você goste desta oferta: " + item.getProduto().getDescricao() + " - R$ " + item.getValor();
                                s += "\nBaixe o app HowMuch e confira: www.howmuch.com";
                                sendIntent.putExtra(Intent.EXTRA_TEXT, s);
                                sendIntent.setType("text/plain");
                                startActivity(sendIntent);
                                dialog_opcoes_produto.cancel();
                            }
                        });
                        ((Button) dialog_opcoes_produto.findViewById(R.id.bt_criar_alerta)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(Descontos.this, "Não Implementado", Toast.LENGTH_LONG).show();
                            }
                        });
                        ((Button) dialog_opcoes_produto.findViewById(R.id.bt_historico_precos_produto)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Descontos.this, VerProduto.class);
                                Produto p = new Produto();
                                p.setId_produto(item.getProduto().getId_produto());
                                p.setDescricao(item.getProduto().getDescricao());
                                p.setUnidade_comercial(item.getProduto().getUnidade_comercial());
                                intent.putExtra("PRODUTO", p);
                                startActivity(intent);
                                dialog_opcoes_produto.cancel();
                            }
                        });
                        dialog_opcoes_produto.show();
                    }
                });
                layout_produtos_desconto.addView(view);
            }
            layout_produtos_desconto.requestFocus();
        } else {
            Toast.makeText(Descontos.this, "Nenhum Item Encontrado", Toast.LENGTH_LONG).show();
        }
    }

    private void adicionarProdutoListas(final ArrayList<Long> ids_listas, final Long id_produto) {
        new AsyncTask<String, Void, Long>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Long doInBackground(String... params) {
                try {
                    URL url = new URL(Utils.URL + "lista");
                    HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                    urlCon.setRequestMethod("POST");
                    urlCon.setDoOutput(true);
                    urlCon.setDoInput(true);

                    OutputStream os = urlCon.getOutputStream();
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
                    objectOutputStream.writeUTF("ADD_PROD_LISTAS");
                    objectOutputStream.writeLong(usuario.getId_usuario());
                    objectOutputStream.writeLong(id_produto);
                    objectOutputStream.writeObject(ids_listas);

                    ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream());
                    long id_lista = (long) ois.readLong();
                    ois.close();
                    objectOutputStream.close();
                    objectOutputStream.flush();
                    return id_lista;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new Long(0);
            }

            @Override
            protected void onPostExecute(Long id_lista) {
                if (id_lista > 0) {
                    Toast.makeText(Descontos.this, "Produto Adicionado!", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Descontos.this, "Erro ao Adicionar Produto", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
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

    @Override
    public void onBackPressed() {
        if (permiteVoltar)
            super.onBackPressed();
    }


}
