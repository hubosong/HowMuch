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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import entidade.NFe;
import entidade.Usuario;
import entidade.Utils;

public class Minhas_NFes extends Nav {

    private boolean permiteVoltar;
    private Usuario usuario;
    private LinearLayout layout_lista_de_nfes;
    private TextView tv_quant_nfes;

    private SimpleDateFormat sdf_bd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private SimpleDateFormat sdf_exibicao = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_minhas_nfes, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(3).setChecked(true);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle(R.string.bar_my_nfe);

        permiteVoltar = getIntent().getBooleanExtra("PERMITE_VOLTAR", false);

        layout_lista_de_nfes = findViewById(R.id.layout_lista_de_nfes);
        tv_quant_nfes = findViewById(R.id.tv_minhas_nfes);

        carregaMinhasNFes();
    }


    @SuppressLint("StaticFieldLeak")
    private void carregaMinhasNFes() {
        if (usuario == null) {
            usuario = Utils.loadFromSharedPreferences(this);
        }

        if (usuario != null && usuario.getId_usuario() > 0) {
            new AsyncTask<String, Void, ArrayList<NFe>>() {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected ArrayList<NFe> doInBackground(String... params) {
                    ArrayList<NFe> list = null;
                    try {
                        String urlParameters = "funcao=GET_ALL_BY_ID_USUARIO&id_usuario=" + usuario.getId_usuario();
                        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                        URL url = new URL(Utils.URL + "nfe");
                        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                        urlCon.setRequestMethod("POST");
                        urlCon.setDoOutput(true);
                        urlCon.setDoInput(true);

                        DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
                        wr.write(postData);
                        wr.close();
                        wr.flush();

                        ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream());
                        list = (ArrayList<NFe>) ois.readObject();
                        ois.close();

                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                    return list;
                }

                @Override
                protected void onPostExecute(final ArrayList<NFe> list) {
                    renderizaNFes(list);
                }
            }.execute();
        } else {
            Toast.makeText(this, "Você não está logado!", Toast.LENGTH_LONG).show();
        }
    }

    private void renderizaNFes(ArrayList<NFe> list) {
        if (list != null) {
            try {
                layout_lista_de_nfes.removeAllViews();
                tv_quant_nfes.setText("Minhas Notas Fiscais (" + list.size() + "):");
                for (final NFe nfe : list) {
                    Date date = sdf_bd.parse(nfe.getData());
                    nfe.setData(sdf_exibicao.format(date));
                    View item; // Creating an instance for View Object
                    LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    item = inflater.inflate(R.layout.layout_my_buys_nfes, null);
                    ((TextView) item.findViewById(R.id.txtNomeLista)).setText(nfe.getMercado().getNome());
                    ((TextView) item.findViewById(R.id.txtHowMany)).setText(" " + nfe.getLista_items().size());
                    ((TextView) item.findViewById(R.id.txtUnitPrice)).setText(nfe.getData());
                    ((TextView) item.findViewById(R.id.txtPrice)).setText("R$ " + nfe.getValor());
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog dialog_opcoes_nfe = new Dialog(Minhas_NFes.this);

                            dialog_opcoes_nfe.requestWindowFeature(Window.FEATURE_NO_TITLE); //no toolbar
                            dialog_opcoes_nfe.setContentView(R.layout.dialog_opcoes_da_nfe);

                            //change alpha intensity
                            WindowManager.LayoutParams lp = dialog_opcoes_nfe.getWindow().getAttributes();
                            lp.dimAmount=0.8f;
                            dialog_opcoes_nfe.getWindow().setAttributes(lp);
                            dialog_opcoes_nfe.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);

                            dialog_opcoes_nfe.show();
                            ((Button) dialog_opcoes_nfe.findViewById(R.id.bt_ver_detalhes)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Minhas_NFes.this, VerNFe.class);
                                    intent.putExtra("NFE", nfe);
                                    startActivity(intent);
                                    dialog_opcoes_nfe.cancel();
                                }
                            });
                            ((Button) dialog_opcoes_nfe.findViewById(R.id.bt_transformar_em_lista)).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(Minhas_NFes.this, Criar_Lista_Compras.class);
                                    intent.putExtra("PERMITE_VOLTAR", true);
                                    intent.putExtra("NFE", nfe);
                                    startActivity(intent);
                                    dialog_opcoes_nfe.cancel();
                                }
                            });
                        }
                    });
                    layout_lista_de_nfes.addView(item);
                }
            } catch (Exception e) {
                Toast.makeText(this, "Erro ao Exibir Produtos", Toast.LENGTH_LONG).show();
                System.out.println(">>> Erro: " + e.getMessage());
            }
        } else {
            Toast.makeText(Minhas_NFes.this, "Nenhuma NFe Encontrada", Toast.LENGTH_LONG).show();
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
                Intent main = new Intent(Minhas_NFes.this, Main.class);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(main);
                finish();
            }
        }
    }
}
