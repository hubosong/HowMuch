package robsonmachczew.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import entidade.Lista;
import entidade.Utils;

public class VerComparacaoLista extends Nav {

    private Lista lista;
    private LinearLayout layout_listas_comparadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_ver_comparacao_lista, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle(R.string.bar_qrcode);

        lista = (Lista) getIntent().getSerializableExtra("LISTA");
        layout_listas_comparadas = findViewById(R.id.layout_listas_comparadas);

        compararLista();
    }

    private void compararLista() {
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
                    wr.writeUTF("COMPARAR_LISTA_ID");
                    wr.writeLong(lista.getId_lista());
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
                renderizaComparacao(list);
            }
        }.execute();
    }


    private void renderizaComparacao(ArrayList<Lista> list) {
        if (list != null) {
            layout_listas_comparadas.removeAllViews();
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
            for(Lista l : list){
                View item; // Creating an instance for View Object
                LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                item = inflater.inflate(R.layout.layout_item_comparacao_listas, null);
                ((TextView) item.findViewById(R.id.txtNomeMercado)).setText(l.getMercado().getNome());
                ((TextView) item.findViewById(R.id.txtHowMany)).setText(l.getListaProdutos().size()+"");
                ((TextView) item.findViewById(R.id.txtPrice)).setText("R$ "+l.getValor_total());
                item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog_opcoes_lista = new Dialog(VerComparacaoLista.this);
                        dialog_opcoes_lista.setContentView(R.layout.dialog_opcoes_lista_comparada);
                        ((Button) dialog_opcoes_lista.findViewById(R.id.bt_precos_detalhados)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Toast.makeText(VerComparacaoLista.this, "Não Implementado", Toast.LENGTH_LONG).show();
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

}
