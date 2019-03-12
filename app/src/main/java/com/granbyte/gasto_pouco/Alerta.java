package com.granbyte.gasto_pouco;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.GravityCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import java.util.Comparator;

import entidade.Lista;
import entidade.Produto;
import entidade.Usuario;
import entidade.Utils;

public class Alerta extends Nav {

    private Usuario usuario;
    private EditText edtSearchProducts;
    private LinearLayout layout_produtos_lista;
    private Lista lista_compras;
    private TextView txtManyProducts;
    private Button btnFinalizarLista;
    private Animation alpha_in, alpha_out;
    private RelativeLayout layout_principal;
    private boolean backAlreadyPressed = false;
    private boolean permiteVoltar = false;

    private final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_alerta, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(5).setChecked(true);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Config. Alertas");

        permiteVoltar = getIntent().getBooleanExtra("PERMITE_VOLTAR", false);

        //alpha effects
        alpha_in = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        alpha_out = AnimationUtils.loadAnimation(this, R.anim.alpha_out);

        usuario = Utils.loadFromSharedPreferences(this);
        layout_produtos_lista = findViewById(R.id.llProducts);
        txtManyProducts = findViewById(R.id.txtManyProducts);
        btnFinalizarLista = findViewById(R.id.btnFinalizarList);
        final LinearLayout layoutPesq = findViewById(R.id.llProducts);
        layout_principal = findViewById(R.id.layout_principal);

        edtSearchProducts = findViewById(R.id.edtSearchProducts);
        edtSearchProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_produtos_lista.removeAllViews();
                layout_principal.setBackgroundColor(Color.parseColor("#242a31"));
                layout_principal.setAnimation(alpha_in);

                btnFinalizarLista.setVisibility(View.GONE);
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


        //notification
        final String notification = "true";
        Handler handler = new Handler();
        if (notification == "true"){
            handler.postDelayed(new Runnable() {
                public void run() {
                    String title = "PRODUTO HU";
                    String text = "Mercado HU" + "  >>  " + "0,00";
                    NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    PendingIntent p = PendingIntent.getActivity(activity, 0, new Intent(activity, Descontos.class), 0);
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(activity);
                    builder.setTicker("hu");
                    builder.setContentTitle(title);
                    builder.setContentText(text);
                    builder.setSmallIcon(R.drawable.filtrapreco_g);
                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.filtrapreco_g));
                    builder.setContentIntent(p);
                    builder.setAutoCancel(true);
                    builder.setDefaults(Notification.DEFAULT_ALL);
                    builder.setPriority(NotificationCompat.PRIORITY_HIGH);
                    builder.setStyle(new NotificationCompat.BigTextStyle().bigText(text));
                    builder.addAction(R.drawable.ic_tag_black_shape, "TAG", p);
                    nm.notify(0, builder.build());

                    /*
            RemoteViews remoteCollapsedViews = new RemoteViews(getPackageName(), R.layout.layout_push);
            RemoteViews remoteExpandedViews = new RemoteViews(getPackageName(), R.layout.layout_push);

            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent p = PendingIntent.getActivity(activity, 0, new Intent(activity, Descontos.class), 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(activity);
            builder.setSmallIcon(R.drawable.filtrapreco_g);
            builder.setStyle(new NotificationCompat.DecoratedCustomViewStyle());
            builder.setCustomContentView(remoteCollapsedViews);
            builder.setCustomBigContentView(remoteExpandedViews);
            builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.filtrapreco_g));
            builder.setContentIntent(p);
            builder.setAutoCancel(true);
            builder.setDefaults(Notification.DEFAULT_ALL);
            nm.notify(0, builder.build());
            */

                }
            }, 5000);
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

                            btnFinalizarLista.setVisibility(View.VISIBLE);
                            btnFinalizarLista.setAnimation(alpha_in);
                            layout_principal.setBackgroundColor(Color.parseColor("#2d353c"));
                            layout_principal.setAnimation(alpha_out);
                        }
                    });


                    ImageButton add = view.findViewById(R.id.btnAdd);
                    add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            lista_compras.getListaProdutos().add(produto);
                            renderizaListaDeProdutos();

                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edtSearchProducts.getWindowToken(), 0);

                            btnFinalizarLista.setVisibility(View.VISIBLE);
                            btnFinalizarLista.setAnimation(alpha_in);
                            layout_principal.setBackgroundColor(Color.parseColor("#2d353c"));
                            layout_principal.setAnimation(alpha_out);
                        }
                    });

                }
            }
        }.execute();
    }

    private void renderizaListaDeProdutos() {
        if (lista_compras.getData() != null && !lista_compras.getData().trim().equalsIgnoreCase("")) {
            txtManyProducts.setText(lista_compras.getData().substring(0, 19) + " " + lista_compras.getListaProdutos().size() + "");
        } else {
            txtManyProducts.setText(" " + lista_compras.getListaProdutos().size() + " ");
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

    /*
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
        SimpleDateFormat formataData = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date data = new Date();
        String dataFormatada = formataData.format(data);

        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View mView = inflater.inflate(R.layout.layout_finalizar_lista, null);
        final AlertDialog dialogAlert = new AlertDialog.Builder(Alerta.this).create();
        final EditText edtNomeLista = mView.findViewById(R.id.userInputDialog);
        final Button btnCancelar = mView.findViewById(R.id.btnCancelar);
        final Button btnSalvar = mView.findViewById(R.id.btnSalvar);

        dialogAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAlert.getWindow().setDimAmount(0.8f);
        dialogAlert.getWindow().getAttributes().windowAnimations = R.style.AllDialogAnimation; //ANIMATION

        edtNomeLista.setText("lista " + dataFormatada);
        btnSalvar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lista_compras.setNome(edtNomeLista.getText().toString());
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
                            Toast.makeText(activity, "Lista Salva!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(activity, Minhas_Listas.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                }.execute();
                dialogAlert.dismiss();
            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAlert.dismiss();
            }
        });

        dialogAlert.setView(mView);
        dialogAlert.show();

    }
    */

    //on back
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawers();
            return;
        }
        renderizaListaDeProdutos();
        if (permiteVoltar) {
            super.onBackPressed();
        }
        else {
            if (usuario.getId_usuario() != 0) {

                btnFinalizarLista.setVisibility(View.GONE);
                btnFinalizarLista.setAnimation(alpha_out);

                //snackbar
                final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.main_layout);
                if (backAlreadyPressed) { finish(); System.exit(0); return; }
                backAlreadyPressed = true;
                Snackbar snackbar = Snackbar.make(coordinatorLayout, R.string.snackbar, Snackbar.LENGTH_LONG);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backAlreadyPressed = false;
                        btnFinalizarLista.setVisibility(View.VISIBLE);
                        btnFinalizarLista.setAnimation(alpha_in); }
                }, 3000);
                TextView txtSnackBar = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                txtSnackBar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                txtSnackBar.setGravity(Gravity.CENTER_HORIZONTAL);
                txtSnackBar.setTextColor(Color.parseColor("#ffffff"));
                snackbar.getView().setBackgroundResource(R.drawable.gradient_list);
                snackbar.show();
            } else {
                Intent main = new Intent(activity, Main.class);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(main);
                finish();
            }
        }
    }


}
