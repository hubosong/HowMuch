package robsonmachczew.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import entidade.Lista;
import entidade.Usuario;
import entidade.Utils;

public class Minhas_Listas extends Nav {

    private boolean permiteVoltar;
    private Usuario usuario;
    private LinearLayout layout_listas_de_listas;
    private TextView tv_quant_listas;
    private SimpleDateFormat sdf_bd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private SimpleDateFormat sdf_exibicao = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_minhas_listas, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Minhas Listas");


        permiteVoltar = getIntent().getBooleanExtra("PERMITE_VOLTAR", false);

        layout_listas_de_listas = findViewById(R.id.layout_listas_de_listas);
        tv_quant_listas = findViewById(R.id.tv_quant_listas);

        if (!Utils.estaConectado(this)) {
            Toast.makeText(this, "Sem conexão", Toast.LENGTH_LONG).show();
        } else {
            pegaListaDeCompras();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void pegaListaDeCompras() {
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
                    rederizaListas(list);
                }
            }.execute();
        } else {
            Toast.makeText(this, "Você não está logado!", Toast.LENGTH_LONG).show();
        }
    }

    private void rederizaListas(ArrayList<Lista> list) {
        if (list != null) {
            try {
                final Context ctx = this;
                layout_listas_de_listas.removeAllViews();
                tv_quant_listas.setText("Listas Encontradas (" + list.size() + "):");
                for (final Lista lista : list) {
                    Date date = sdf_bd.parse(lista.getData());
                    lista.setData(sdf_exibicao.format(date));
                    View item; // Creating an instance for View Object
                    LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    item = inflater.inflate(R.layout.layout_lista_de_listas, null);
                    ((TextView) item.findViewById(R.id.txtNomeLista)).setText(lista.getNome());
                    ((TextView) item.findViewById(R.id.txtQtdItems)).setText(lista.getListaProdutos().size() + " Produtos");
                    ((TextView) item.findViewById(R.id.txtDataLista)).setText(lista.getData());
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog dialog_opcoes_lista = new Dialog(Minhas_Listas.this);

                            dialog_opcoes_lista.requestWindowFeature(Window.FEATURE_NO_TITLE); //no toolbar
                            dialog_opcoes_lista.setContentView(R.layout.dialog_opcoes_lista_de_listas);

                            //change alpha intensity
                            WindowManager.LayoutParams lp = dialog_opcoes_lista.getWindow().getAttributes();
                            lp.dimAmount=0.8f;
                            dialog_opcoes_lista.getWindow().setAttributes(lp);
                            dialog_opcoes_lista.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

                            ((Button) dialog_opcoes_lista.findViewById(R.id.bt_editar_lista)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(ctx, Criar_Lista_Compras.class);
                                    intent.putExtra("PERMITE_VOLTAR", true);
                                    intent.putExtra("LISTA", lista);
                                    startActivity(intent);
                                    dialog_opcoes_lista.cancel();
                                }
                            });
                            ((Button) dialog_opcoes_lista.findViewById(R.id.bt_comparar_precos_em_mercados)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(ctx, VerComparacaoLista.class);
                                    intent.putExtra("PERMITE_VOLTAR", true);
                                    intent.putExtra("LISTA", lista);
                                    startActivity(intent);
                                    dialog_opcoes_lista.cancel();
                                }
                            });
                            dialog_opcoes_lista.show();
                        }
                    });
                    layout_listas_de_listas.addView(item);
                }
            }catch (Exception e){
                Toast.makeText(this, "Erro ao Exibir Produtos", Toast.LENGTH_LONG).show();
                System.out.println(">>> Erro: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            Toast.makeText(Minhas_Listas.this, "Nenhuma Lista Encontrada", Toast.LENGTH_LONG).show();
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
                Intent main = new Intent(Minhas_Listas.this, Main.class);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(main);
                finish();
            }
        }
    }
}
