package robsonmachczew.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import entidade.Lista;
import entidade.NFe;
import entidade.Produto;
import entidade.Usuario;
import entidade.Utils;

public class BuyList extends Nav {

    private Usuario usuario;
    private EditText edtSearchProducts;
    private LinearLayout layout_produtos_lista;
    private Lista lista_compras;
    private TextView txtManyProducts;
    private Button btnFinalizarLista;
    public Animation alpha_in, alpha_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_buy_list, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Criar Lista");

        //alpha effects
        alpha_in = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        alpha_out = AnimationUtils.loadAnimation(this, R.anim.alpha_out);

        usuario = Utils.loadFromSharedPreferences(this);
        layout_produtos_lista = findViewById(R.id.llProducts);
        txtManyProducts = findViewById(R.id.txtManyProducts);
        btnFinalizarLista = findViewById(R.id.btnFinalizarList);
        final LinearLayout layoutPesq = findViewById(R.id.llProducts);

        edtSearchProducts = findViewById(R.id.edtSearchProducts);
        edtSearchProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_produtos_lista.removeAllViews();

                txtManyProducts.setVisibility(View.GONE);
                btnFinalizarLista.setVisibility(View.GONE);
                txtManyProducts.setAnimation(alpha_out);
                btnFinalizarLista.setAnimation(alpha_out);
            }
        });
        edtSearchProducts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 2) {
                    realizaPesquisaProdutos(s.toString(), layoutPesq);
                }
            }
        });


        if (!Utils.estaConectado(this)) {
            Toast.makeText(this, "Sem conexão", Toast.LENGTH_LONG).show();
            return;
        }

        if (getIntent().getSerializableExtra("NFE") != null) {
            //SE É PARA TRANSFORMAR NFe EM LISTA DE COMPRAS
            lista_compras = new Lista();
            NFe nfe = (NFe) getIntent().getSerializableExtra("NFE");
            for (int i = 0; i < nfe.getLista_items().size(); i++) {
                lista_compras.getListaProdutos().add(nfe.getLista_items().get(i).getProduto());
            }
        } else if (getIntent().getSerializableExtra("PRODUTO") != null) {
            //SE É PARA ADICIONAR UM PRODUTO À UMA LISTA
            if (getIntent().getSerializableExtra("LISTA") != null) {
                //ADICIONAR À UMA LISTA EXISTENTE
                lista_compras = (Lista) getIntent().getSerializableExtra("LISTA");
                getSupportActionBar().setTitle("Editar Lista de Compras");
            } else {
                //ADICIONAR À UMA NOVA LISTA
                lista_compras = new Lista();
            }
            lista_compras.getListaProdutos().add((Produto) getIntent().getSerializableExtra("PRODUTO"));
        } else {
            lista_compras = (Lista) getIntent().getSerializableExtra("LISTA");
            if (lista_compras == null) {
                lista_compras = new Lista();
            } else {
                Button btFinalizar = findViewById(R.id.btnFinalizarList);
                btFinalizar.setText("SALVAR");
                getSupportActionBar().setTitle("Editar Lista de Compras");
            }
        }

    }


    @SuppressLint("StaticFieldLeak")
    private void realizaPesquisaProdutos(final String pesquisa, final LinearLayout layoutProdutos) {
        new AsyncTask<String, Void, ArrayList<Produto>>() {
            @Override
            protected void onPreExecute() {
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
                Collections.sort(list, new Comparator<Produto>() {
                    @Override
                    public int compare(Produto o1, Produto o2) {
                        return o1.getDescricao().compareTo(o2.getDescricao());
                    }
                });
                for (final Produto produto : list) {
                    if (produto.getDescricao2() != null && !produto.getDescricao2().trim().equalsIgnoreCase("") && !produto.getDescricao2().trim().equalsIgnoreCase("NULL")) {
                        produto.setDescricao(produto.getDescricao2());
                    }

                    LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.layout_buy_list, null);

                    TextView txtProducts = view.findViewById(R.id.txtBuyProduct);
                    txtProducts.setText(produto.getDescricao());
                    layoutProdutos.addView(view);

                    txtProducts.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            lista_compras.getListaProdutos().add(produto);
                            renderizaListaDeProdutos();

                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edtSearchProducts.getWindowToken(), 0);

                            txtManyProducts.setVisibility(View.VISIBLE);
                            btnFinalizarLista.setVisibility(View.VISIBLE);
                            btnFinalizarLista.setAnimation(alpha_in);
                        }
                    });


                    ImageButton excluir = view.findViewById(R.id.btnAdd);
                    excluir.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            lista_compras.getListaProdutos().add(produto);
                            renderizaListaDeProdutos();

                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edtSearchProducts.getWindowToken(), 0);

                            txtManyProducts.setVisibility(View.VISIBLE);
                            btnFinalizarLista.setVisibility(View.VISIBLE);
                            btnFinalizarLista.setAnimation(alpha_in);
                        }
                    });

                }
            }
        }.execute();
    }

    private void renderizaListaDeProdutos() {
        if (lista_compras.getData() != null && !lista_compras.getData().trim().equalsIgnoreCase("")) {
            txtManyProducts.setText(lista_compras.getData().substring(0, 19) + " - Produtos da Lista (" + lista_compras.getListaProdutos().size() + "):");
        } else {
            txtManyProducts.setText("Produtos da Lista (" + lista_compras.getListaProdutos().size() + "):");
        }

        if (lista_compras.getNome() != null && !lista_compras.getNome().trim().equalsIgnoreCase("")) {
            txtManyProducts.setText(lista_compras.getNome());
        }

        layout_produtos_lista.removeAllViews();
        for (final Produto prod : lista_compras.getListaProdutos()) {
            LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.layout_lista_compras, null);
            EditText editTextQtd = view.findViewById(R.id.editQuantidade);
            editTextQtd.setText(""+prod.getTransient_quantidade());
            editTextQtd.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try{
                        prod.setTransient_quantidade(Float.valueOf(s.toString()));
                    }catch (Exception e){}
                }
            });
            ((TextView) view.findViewById(R.id.txtProduct)).setText(prod.getDescricao());
            ImageButton excluir = view.findViewById(R.id.btnProduct);
            excluir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    lista_compras.getListaProdutos().remove(prod);
                    renderizaListaDeProdutos();
                }
            });
            layout_produtos_lista.addView(view);
        }
    }


    public void preFinalizarLista(View v) {
        if (lista_compras.getListaProdutos().size() == 0) {
            Toast.makeText(this, "Lista sem Itens", Toast.LENGTH_SHORT).show();
            return;
        } else {
            finalizarLista();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void finalizarLista() {
        SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy");
        Date data = new Date();
        String dataFormatada = formataData.format(data);

        lista_compras.setNome(dataFormatada);
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
                    objectOutputStream.writeUTF("SALVAR_LISTA");
                    objectOutputStream.writeLong(usuario.getId_usuario());
                    objectOutputStream.writeObject(lista_compras);

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
                    Toast.makeText(BuyList.this, "Lista Salva!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(BuyList.this, Minhas_Listas.class);
                    startActivity(intent);
                    finish();
                }
            }
        }.execute();
        Button btFinalizar = findViewById(R.id.btnFinalizarList);
        btFinalizar.setText("SALVAR");
    }

}
