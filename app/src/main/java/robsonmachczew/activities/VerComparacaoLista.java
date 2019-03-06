package robsonmachczew.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import entidade.Lista;
import entidade.Produto;
import entidade.Utils;

public class VerComparacaoLista extends Nav {

    private Lista lista;
    private LinearLayout layout_listas_comparadas;
    private TextView txt_nome_da_lista_de_compras;
    private TextView txt_mercados_simulados;

    private SimpleDateFormat sdf_bd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private SimpleDateFormat sdf_exibicao = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_ver_comparacao_lista, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Comparativo");

        lista = (Lista) getIntent().getSerializableExtra("LISTA");
        layout_listas_comparadas = findViewById(R.id.layout_listas_comparadas);
        txt_nome_da_lista_de_compras = findViewById(R.id.txt_nome_da_lista_de_compras);
        txt_mercados_simulados = findViewById(R.id.txt_mercados_simulados);

        ArrayList<Long> ids_produtos = new ArrayList<>();
        for (Produto p : lista.getListaProdutos()) {
            ids_produtos.add(p.getId_produto());
        }
        compararLista(ids_produtos);
    }

    @SuppressLint("StaticFieldLeak")
    private void compararLista(final ArrayList<Long> ids_produtos) {
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
                    wr.writeUTF("COMPARAR_LISTA_IDS_PRODUTOS");
                    wr.writeObject(ids_produtos);
                    wr.writeLong(lista.getId_usuario());
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
                try {
                    for (Lista ll : list) {
                        ll.setNome(lista.getNome());
                        ll.setValor_total(0.0f);
                        for (Produto p : ll.getListaProdutos()) {
                            System.out.println(">>>P: " + p.getTransient_valor() + "  " + p.getTransient_quantidade());
                            p.setTransient_valor(p.getTransient_valor() / p.getTransient_quantidade());
                            ll.setValor_total(ll.getValor_total() + p.getTransient_valor());
                            Date date = sdf_bd.parse(p.getTransient_data());
                            p.setTransient_data(sdf_exibicao.format(date));
                        }
                    }
                } catch (Exception e) {

                }
                renderizaComparacao(list);
            }
        }.execute();
    }

    private void renderizaComparacao(ArrayList<Lista> list) {
        if (list != null) {
            layout_listas_comparadas.removeAllViews();
            txt_mercados_simulados.setText("" + list.size() + "");
            txt_nome_da_lista_de_compras.setText(lista.getNome());
            Collections.sort(list, new Comparator<Lista>() {
                @Override
                public int compare(Lista o1, Lista o2) {
                    Integer x1 = o1.getListaProdutos().size();
                    Integer x2 = o2.getListaProdutos().size();
                    int sComp = x2.compareTo(x1);
                    if (sComp != 0) {
                        return sComp;
                    }
                    Float xx1 = o1.getValor_total();
                    Float xx2 = o2.getValor_total();
                    return xx1.compareTo(xx2);
                }
            });
            DecimalFormat df = new DecimalFormat("0.00");
            for (final Lista l : list) {
                View item; // Creating an instance for View Object
                LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                item = inflater.inflate(R.layout.layout_item_comparacao_listas, null);
                ((TextView) item.findViewById(R.id.txtNomeMercado)).setText(l.getMercado().getNome());
                ((TextView) item.findViewById(R.id.txtHowMany)).setText(l.getListaProdutos().size() + " / " + lista.getListaProdutos().size());
                ((TextView) item.findViewById(R.id.txtPrice)).setText("R$ " + df.format(l.getValor_total()).replace(",", "."));
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog_opcoes_lista = new Dialog(VerComparacaoLista.this);

                        dialog_opcoes_lista.requestWindowFeature(Window.FEATURE_NO_TITLE); //no toolbar
                        dialog_opcoes_lista.setContentView(R.layout.dialog_opcoes_lista_comparada);

                        dialog_opcoes_lista.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog_opcoes_lista.getWindow().setDimAmount(0.8f);

                        ((Button) dialog_opcoes_lista.findViewById(R.id.bt_precos_detalhados)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(VerComparacaoLista.this, VerSimulacaoListaEmMercado.class);
                                intent.putExtra("LISTA", l);
                                startActivity(intent);
                                dialog_opcoes_lista.cancel();
                            }
                        });
                        ((Button) dialog_opcoes_lista.findViewById(R.id.bt_detalhes_mercado)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(VerComparacaoLista.this, VerMercado.class);
                                intent.putExtra("MERCADO", l.getMercado());
                                startActivity(intent);
                                dialog_opcoes_lista.cancel();
                            }
                        });

                        dialog_opcoes_lista.show();
                    }
                });
                layout_listas_comparadas.addView(item);
            }
        }
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
