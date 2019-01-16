package robsonmachczew.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import entidade.Lista;
import entidade.NFe;
import entidade.Produto;
import entidade.Usuario;
import entidade.Utils;

public class Criar_Lista_Compras extends Nav {

    private boolean permiteVoltar;
    private Usuario usuario;
    private Lista lista_compras;
    private Dialog dialog_pesquisa;
    private LinearLayout layout_produtos_lista;
    private TextView tvQuantProdutosLista;
    private EditText editTextNomeLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_lista__compras, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Criar Lista");

        permiteVoltar = getIntent().getBooleanExtra("PERMITE_VOLTAR", false);

        usuario = Utils.loadFromSharedPreferences(this);
        layout_produtos_lista = findViewById(R.id.layout_produtos_da_lista);
        tvQuantProdutosLista = findViewById(R.id.textView2);
        editTextNomeLista = findViewById(R.id.editText2);

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
                Button btFinalizar = findViewById(R.id.button3);
                btFinalizar.setText("SALVAR");
                getSupportActionBar().setTitle("Editar Lista de Compras");
            }
        }
        renderizaListaDeProdutos();
    }


    public void adicionarProduto(View v) {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        dialog_pesquisa = new Dialog(this);
        dialog_pesquisa.requestWindowFeature(Window.FEATURE_NO_TITLE); //no toolbar
        dialog_pesquisa.setContentView(R.layout.dialog_pesquisa_produtos_lista);

        //change alpha intensity
        WindowManager.LayoutParams lp = dialog_pesquisa.getWindow().getAttributes();
        lp.dimAmount=0.8f;
        dialog_pesquisa.getWindow().setAttributes(lp);
        dialog_pesquisa.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);


        EditText text = dialog_pesquisa.findViewById(R.id.editText_Procura_Prod_Lista);
        final LinearLayout layoutPesq = dialog_pesquisa.findViewById(R.id.layout_produtos_pesquisados);
        final Button btConcluir = dialog_pesquisa.findViewById(R.id.btConcluirPesq);
        ((Button) dialog_pesquisa.findViewById(R.id.button2)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_pesquisa.cancel();
            }
        });
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

    public void preFinalizarLista(View v) {
        if (lista_compras.getListaProdutos().size() == 0) {
            Toast.makeText(this, "Lista sem Itens", Toast.LENGTH_SHORT).show();
            return;
        }
        if (editTextNomeLista.getText().toString().trim().equalsIgnoreCase("") && lista_compras.getId_lista() == 0) {
            Toast.makeText(this, "Lista sem Nome", Toast.LENGTH_SHORT).show();
            return;
        } else {
            finalizarLista();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void finalizarLista() {
        lista_compras.setNome(editTextNomeLista.getText().toString());
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
                    Toast.makeText(Criar_Lista_Compras.this, "Lista Salva!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(Criar_Lista_Compras.this, Minhas_Listas.class);
                    startActivity(intent);
                    finish();
                }
            }
        }.execute();
        Button btFinalizar = findViewById(R.id.button3);
        btFinalizar.setText("SALVAR");
    }

    @SuppressLint("StaticFieldLeak")
    private void realizaPesquisaProdutos(final String pesquisa, final LinearLayout layoutProdutos, final Button btConcluir) {
        new AsyncTask<String, Void, ArrayList<Produto>>() {
            @Override
            protected void onPreExecute() {
                layoutProdutos.removeAllViews();
                TextView txtCarregando = new TextView(Criar_Lista_Compras.this);
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
                    CheckBox cb = new CheckBox(Criar_Lista_Compras.this);
                    cb.setText(produto.getDescricao());
                    cb.setTextColor(Color.parseColor("#ffffff"));
                    layoutProdutos.addView(cb);
                }
                btConcluir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        for (int i = 0; i < layoutProdutos.getChildCount(); i++) {
                            if (((CheckBox) layoutProdutos.getChildAt(i)).isChecked()) {
                                lista_compras.getListaProdutos().add(list.get(i));
                            }
                        }
                        renderizaListaDeProdutos();
                    }
                });
            }
        }.execute();
    }

    private void renderizaListaDeProdutos() {
        if (dialog_pesquisa != null && dialog_pesquisa.isShowing()) {
            dialog_pesquisa.dismiss();
        }
        if (lista_compras.getData() != null && !lista_compras.getData().trim().equalsIgnoreCase("")) {
            tvQuantProdutosLista.setText(lista_compras.getData().substring(0, 19) + " - Produtos da Lista (" + lista_compras.getListaProdutos().size() + "):");
        } else {
            tvQuantProdutosLista.setText("Produtos da Lista (" + lista_compras.getListaProdutos().size() + "):");
        }

        layout_produtos_lista.removeAllViews();
        if (lista_compras.getNome() != null && !lista_compras.getNome().trim().equalsIgnoreCase("")) {
            editTextNomeLista.setText(lista_compras.getNome());
        }
        for (Produto prod : lista_compras.getListaProdutos()) {
            System.out.println("PROD: " + prod);
            if (prod.getDescricao2() != null && !prod.getDescricao2().trim().equalsIgnoreCase("") && !prod.getDescricao2().trim().equalsIgnoreCase("NULL")) {
                prod.setDescricao(prod.getDescricao2());
            }
            TextView tv = new TextView(this);
            tv.setText(prod.getDescricao());
            tv.setTextColor(Color.WHITE);
            layout_produtos_lista.addView(tv);
        }
    }

    //on back
    @Override
    public void onBackPressed() {
        if (permiteVoltar) {
            super.onBackPressed();
        }
        else {
            if (usuario.getId_usuario() != 0) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                System.exit(0);

            } else {
                Intent main = new Intent(Criar_Lista_Compras.this, Main.class);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(main);
                finish();
            }
        }
    }
}
