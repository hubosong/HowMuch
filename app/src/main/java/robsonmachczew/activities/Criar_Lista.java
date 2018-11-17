package robsonmachczew.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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

import entidade.Lista;
import entidade.Produto;
import entidade.Usuario;
import entidade.Utils;

public class Criar_Lista extends Nav {

    private TextView txtList;
    private EditText edtSearch;
    private LinearLayout layoutProdutos, txtDialogList;
    private ArrayList<Produto> lista_compras;

    private final Activity activity = this;
    private Animation alpha_in, alpha_out;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        //called by nav
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_criar_lista, contentFrameLayout);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);

        //basic config
        getSupportActionBar().setTitle(R.string.bar_buy_list);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        alpha_in = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        alpha_out = AnimationUtils.loadAnimation(this, R.anim.alpha_out);

        txtList = findViewById(R.id.txtList);
        usuario = Utils.loadFromSharedPreferences(this);

        Lista l = (Lista) getIntent().getSerializableExtra("LISTA");
        if (l != null) {
            lista_compras = l.getListaProdutos();
            txtList.setText(" " + lista_compras.size() + " ");
        }

        txtDialogList = findViewById(R.id.txtDialogList);
        txtDialogList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Criar_Lista.this);
                dialog.setContentView(R.layout.dialog_minha_lista);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView txtTitle = dialog.findViewById(R.id.txt_title);
                txtTitle.setText("Minha Lista de Compras");

                //Lista de Compras Adicionada
                ListView listView = dialog.findViewById(R.id.list_dialog);
                listView.setVisibility(View.VISIBLE);
                String[] lista = new String[lista_compras.size()];
                for (int i = 0; i < lista_compras.size(); i++) {
                    lista[i] = (i + 1) + " | " + lista_compras.get(i).getDescricao() + " | " + lista_compras.get(i).getTransient_quantidade() + " (" + lista_compras.get(i).getUnidade_comercial() + ")";
                }

                //String animalList[] = {"Lion", "Tiger", "Monkey", "Elephant", "Dog", "Cat", "Camel", "Tiger", "Monkey", "Elephant", "Dog", "Cat", "Camel"};

                ArrayAdapter<String> adapter = new ArrayAdapter<String>(Criar_Lista.this, android.R.layout.simple_list_item_1, lista);
                listView.setAdapter(adapter);


                //funcoes
                Button btnSalvar = dialog.findViewById(R.id.btn_salvar);
                btnSalvar.setVisibility(View.VISIBLE);
                btnSalvar.setOnClickListener(new View.OnClickListener() {
                    @SuppressLint("StaticFieldLeak")
                    public void onClick(View v) {
                        new AsyncTask<String, Void, Long>() {
                            @Override
                            protected void onPreExecute() {
                                layoutProdutos.removeAllViews();

                                TextView txtCarregando = new TextView(Criar_Lista.this);
                                txtCarregando.setText(R.string.txt_progress);
                                txtCarregando.setTextSize(16);
                                txtCarregando.setTextColor(Color.parseColor("#ffffff"));
                                layoutProdutos.addView(txtCarregando);

                                super.onPreExecute();
                            }

                            @Override
                            protected Long doInBackground(String... params) {
                                try {
                                    String urlParameters = "funcao=SALVAR_LISTA&id_usuario=" + usuario.getId_usuario();
                                    byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                                    URL url = new URL(Utils.URL + "lista");
                                    HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                                    urlCon.setRequestMethod("POST");
                                    urlCon.setDoOutput(true);
                                    urlCon.setDoInput(true);

                                    OutputStream os = urlCon.getOutputStream();
                                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
                                    objectOutputStream.writeUTF("SALVAR_LISTA");
                                    objectOutputStream.writeLong(usuario.getId_usuario());
                                    Lista lista = new Lista();
                                    lista.setNome("LISTA TESTE");
                                    lista.setListaProdutos(lista_compras);
                                    objectOutputStream.writeObject(lista);

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
                                    Toast.makeText(activity, "Lista Salva!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }.execute();
                    }
                });

                Button btnCancelar = dialog.findViewById(R.id.btn_cancelar);
                btnCancelar.setVisibility(View.VISIBLE);
                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Toast.makeText(activity, "Cancelado!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });

                Button btnSalvarComparar = dialog.findViewById(R.id.btn_salvar_comparar);
                btnSalvarComparar.setVisibility(View.VISIBLE);
                btnSalvarComparar.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.create();
                dialog.show();


                /*
                AlertDialog.Builder dialog = new AlertDialog.Builder(Criar_Lista.this);
                TextView title = new TextView(Criar_Lista.this);
                title.setText("Minha Lista de Compras");
                title.setBackgroundColor(ContextCompat.getColor(Criar_Lista.this, R.color.toolbar_status));
                title.setPadding(10, 10, 10, 10);
                title.setGravity(Gravity.CENTER);
                title.setTextColor(Color.WHITE);
                title.setTextSize(20);
                dialog.setCustomTitle(title);

                //list
                dialog.setView(LayoutInflater.from(Criar_Lista.this).inflate(android.R.layout.simple_list_item_1, null));
                String[] lista = new String[lista_compras.size()];
                for (int i = 0; i < lista_compras.size(); i++) {
                    lista[i] = (i + 1) + " | " + lista_compras.get(i).getDescricao() + " | " + lista_compras.get(i).getTransient_quantidade() + " (" + lista_compras.get(i).getUnidade_comercial() + ")";
                }
                dialog.setItems(lista, null);
                dialog.setCancelable(true);

                dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(activity, "Cancelado!", Toast.LENGTH_SHORT).show();
                    }
                });

                dialog.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    @SuppressLint("StaticFieldLeak")
                    public void onClick(DialogInterface dialog, int id) {
                        new AsyncTask<String, Void, Long>() {
                            @Override
                            protected void onPreExecute() {
                                layoutProdutos.removeAllViews();

                                TextView txtCarregando = new TextView(Criar_Lista.this);
                                txtCarregando.setText(R.string.txt_progress);
                                txtCarregando.setTextSize(16);
                                txtCarregando.setTextColor(Color.parseColor("#ffffff"));
                                layoutProdutos.addView(txtCarregando);

                                super.onPreExecute();
                            }

                            @Override
                            protected Long doInBackground(String... params) {
                                try {
                                    String urlParameters = "funcao=SALVAR_LISTA&id_usuario=" + usuario.getId_usuario();
                                    byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                                    URL url = new URL(Utils.URL+"lista");
                                    HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                                    urlCon.setRequestMethod("POST");
                                    urlCon.setDoOutput(true);
                                    urlCon.setDoInput(true);

                                    OutputStream os = urlCon.getOutputStream();
                                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
                                    objectOutputStream.writeUTF("SALVAR_LISTA");
                                    objectOutputStream.writeLong(usuario.getId_usuario());
                                    Lista lista = new Lista();
                                    lista.setNome("LISTA TESTE");
                                    lista.setListaProdutos(lista_compras);
                                    objectOutputStream.writeObject(lista);

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
                                    Toast.makeText(activity, "Lista Salva!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }.execute();
                    }
                });

                dialog.setNeutralButton("Salvar e Comparar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog alert = dialog.create();
                alert.show();

                */

            }
        });

        layoutProdutos = (LinearLayout) findViewById(R.id.layoutProdutos);
        if(lista_compras == null) {
            lista_compras = new ArrayList<>();
        }

        edtSearch = findViewById(R.id.edtSearch);
        edtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView imgLogo = findViewById(R.id.imgLogo);
                imgLogo.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
                imgLogo.setAnimation(alpha_out);
                fab.setAnimation(alpha_out);
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    realizaPesquisaProdutos(s.toString());
                }
            }
        });

    }

    @SuppressLint("StaticFieldLeak")
    private void realizaPesquisaProdutos(final String pesquisa) {
        new AsyncTask<String, Void, ArrayList<Produto>>() {
            @Override
            protected void onPreExecute() {
                layoutProdutos.removeAllViews();

                TextView txtCarregando = new TextView(Criar_Lista.this);
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
            protected void onPostExecute(ArrayList<Produto> list) {

                layoutProdutos.removeAllViews();

                for (final Produto produto : list) {

                    ImageView imgLogo = findViewById(R.id.imgLogo);
                    imgLogo.setVisibility(View.GONE);

                    //layout subscribe linear layout of xml
                    LinearLayout l = new LinearLayout(Criar_Lista.this);
                    l.setOrientation(LinearLayout.HORIZONTAL);

                    Button btnAdd = new Button(Criar_Lista.this);
                    btnAdd.setText("+");
                    btnAdd.setMinimumHeight(0);
                    btnAdd.setHeight(80);
                    btnAdd.setGravity(Gravity.CENTER);
                    btnAdd.setTextColor(Color.parseColor("#ffffff"));
                    btnAdd.setBackgroundResource(R.drawable.buttons_login);

                    final EditText edtQtd = new EditText(Criar_Lista.this);
                    edtQtd.setText("1.0");
                    edtQtd.setTextColor(Color.parseColor("#a8acb1"));

                    final TextView txtDesc = new TextView(Criar_Lista.this);
                    txtDesc.setText(produto.getDescricao());
                    txtDesc.setTextColor(Color.parseColor("#ffffff"));
                    txtDesc.setPadding(5, 0, 0, 0);

                    l.addView(btnAdd);
                    l.addView(edtQtd);
                    l.addView(txtDesc);
                    layoutProdutos.addView(l);

                    btnAdd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            boolean adicionado = false;
                            produto.setTransient_quantidade(Float.valueOf(edtQtd.getText().toString()));
                            for (int i = 0; i < lista_compras.size(); i++) {
                                if (lista_compras.get(i).getId_produto() == produto.getId_produto()) {
                                    lista_compras.get(i).setTransient_quantidade(lista_compras.get(i).getTransient_quantidade() + produto.getTransient_quantidade());
                                    adicionado = true;
                                    break;
                                }
                            }
                            if (!adicionado) {
                                lista_compras.add(produto);
                            }
                            txtList.setText(" " + lista_compras.size() + " ");
                        }
                    });

                }
            }
        }.execute();
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(Criar_Lista.this, Main.class);
        startActivity(main);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}
