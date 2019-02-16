package robsonmachczew.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
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
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import entidade.Item_NFe;
import entidade.Lista;
import entidade.Produto;
import entidade.ProdutoAbaixoMedia;
import entidade.Produto_Detalhado;
import entidade.Usuario;
import entidade.Utils;

public class Descontos extends Nav {

    boolean mostrando_pesquisados = false;

    private boolean permiteVoltar = false;
    private LinearLayout layout_produtos_desconto;
    private TextView tv_quant_prods_desconto;
    private EditText txt_pesquisa_produtos;
    private ArrayList<Lista> lista_de_listas;
    private ArrayList<ProdutoAbaixoMedia> lista_produtos_abaixo_media;
    private ArrayList<Item_NFe> lista_produtos_pesquisados;
    private Usuario usuario;

    private SimpleDateFormat sdf_bd = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    private SimpleDateFormat sdf_exibicao = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

    public Animation alpha_in, alpha_out;
    private ScrollView layout_top;

    private boolean backAlreadyPressed = false;

    Intent intent;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_descontos, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Descontos");

        //alpha effects
        alpha_in = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        alpha_out = AnimationUtils.loadAnimation(this, R.anim.alpha_out);


        pegaListasDeCompras();


        permiteVoltar = getIntent().getBooleanExtra("PERMITE_VOLTAR", false);

        layout_produtos_desconto = findViewById(R.id.layout_prods_desconto);
        tv_quant_prods_desconto = findViewById(R.id.tv_quant_prods_abaixo_media);
        txt_pesquisa_produtos = findViewById(R.id.editText);

        setEditTextProcuraProdutos();

        if (!Utils.estaConectado(this)) {
            Toast.makeText(this, "Sem conexão", Toast.LENGTH_LONG).show();
        } else {
            rvListStart();
        }

        setaOrdenacoesListas();

        //barcode search
        findViewById(R.id.btn_barcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(Descontos.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.EAN_13, IntentIntegrator.EAN_8);
                integrator.setPrompt("Alinhe o código\n\n\n\n");
                integrator.setCameraId(0);
                integrator.initiateScan();
                integrator.setBarcodeImageEnabled(true);
                integrator.setOrientationLocked(true);
                integrator.setBeepEnabled(true);
                code = 2;
            }
        });

        tentaEnviarNFesPendentes();
    }

    private void tentaEnviarNFesPendentes() {
        final Set<String> chavess = Utils.getNotasLocais(this);
        if (!chavess.isEmpty()) {
            System.out.println(">>> ENVIANDO CHAVES NÃO ENVIADAS: " + chavess);
            try {
                new AsyncTask<String, Void, JSONObject>() {
                    @Override
                    protected JSONObject doInBackground(String... params) {
                        JSONObject response_json = null;
                        try {
                            JSONObject send_json = new JSONObject();
                            for (String chave : chavess) {
                                send_json.put("chave", chave);
                            }

                            URL url = new URL(Utils.URL + "nfe_json");
                            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                            urlCon.setRequestProperty("Accept", "application/json");
                            urlCon.setRequestProperty("Content-type", "application/json");
                            urlCon.setRequestProperty("Function", "SEND_NOT_SAVED_NFES");
                            urlCon.setRequestProperty("id_usuario", String.valueOf(usuario.getId_usuario()));
                            urlCon.setRequestMethod("POST");
                            urlCon.setDoOutput(true);
                            urlCon.setDoInput(true);

                            OutputStream outputStream = urlCon.getOutputStream();
                            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));

                            writer.write(send_json.toString());
                            writer.flush();
                            writer.close();

                            if (urlCon.getResponseCode() == 201) { //201 = Created. Gravou algumas NFes e outras não. Precisamos pegar as que não gravou para mandar de novo mais tarde...
                                InputStream is = urlCon.getInputStream();
                                try {
                                    BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                                    StringBuilder sb = new StringBuilder();
                                    int cp;
                                    while ((cp = rd.read()) != -1) {
                                        sb.append((char) cp);
                                    }
                                    String s = sb.toString();
                                    response_json = new JSONObject(s);
                                } finally {
                                    is.close();
                                }
                            } else if (urlCon.getResponseCode() == 204) { //204 = No Content. Ou seja, se não tem conteúdo, é porque conseguiu gravar tudo. Senão, alguma chave não gravou...
                                response_json = new JSONObject();
                            }
                        } catch (Exception e) {
                            System.out.println(">>> Erro tentando enviar nfes não lidas_1: " + e.getMessage());
                            e.printStackTrace();
                        }
                        return response_json;
                    }

                    @Override
                    protected void onPostExecute(JSONObject response_json) {
                        if (response_json != null) {
                            Set<String> lista_nfes_nao_salvas = new HashSet<>();
                            Iterator<String> keys = response_json.keys();
                            try {
                                while (keys.hasNext()) {
                                    lista_nfes_nao_salvas.add((String) response_json.get(keys.next()));
                                }
                            } catch (Exception e) {
                                System.out.println(">>> Erro tentando transformar response_json em lista_nfes_nao_salvas...");
                            }
                            System.out.println(">>> KASDJKASJD:> " + lista_nfes_nao_salvas.toString());
                            Utils.salvaNotaLocalmente(Descontos.this, lista_nfes_nao_salvas);
                        } else {
                            System.out.println(">>> Erro enviando notas não salvas para o servidor (response json == null).");
                        }
                    }
                }.execute();
            } catch (Exception e) {
                System.out.println(">>> Erro tentando enviar nfes não lidas_2: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println(">>> SEM NOTAS NÃO LIDAS PARA ENVIAR...");
        }
    }

    //qrcode and barcode reader
    //copiado do nav para cá, para que nao haja conffito
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if qrcode reader
        if (code == 1) {
            System.out.println("LEITURA EM DESCONTOS...");
            IntentResult qrcode = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (qrcode != null) {
                if (qrcode.getContents() != null) {
                    String cut = qrcode.getContents();
                    int corte = cut.indexOf("=") + 1;
                    final String code = cut.substring(corte, corte + 44);
                    Intent readQRcode = new Intent(Descontos.this, VerNFe.class);
                    readQRcode.putExtra("code", code);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    startActivity(readQRcode);
                } else {
                    Toast.makeText(Descontos.this, R.string.toast_cancel_read_qrcode, Toast.LENGTH_SHORT).show();
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
        //if barcode reader
        else if (code == 2) {
            IntentResult barcode = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (barcode != null) {
                if (barcode.getContents() != null) {
                    String codigo_de_barras = barcode.getContents();
                    pegaProdutoPorCodigoDeBarras(codigo_de_barras);
                } else {
                    Toast.makeText(this, "Leitura do código de barras cancelado", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Erro ao ler código de barras do Produto", Toast.LENGTH_LONG).show();
            }
        }

    }


    /**
     * Atribui as ordenações das listas de produtos abaixo da média e pesquisados
     */
    private void setaOrdenacoesListas() {
        findViewById(R.id.btn_ordenar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PopupWindow popup = new PopupWindow(Descontos.this);
                View layout = getLayoutInflater().inflate(R.layout.popup_interface, null);
                popup.setContentView(layout);
                popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
                popup.setOutsideTouchable(true);
                popup.setFocusable(true);
                popup.showAsDropDown(v);

                popup.getContentView().findViewById(R.id.pop_menorPreco).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mostrando_pesquisados && lista_produtos_pesquisados != null) {
                            Collections.sort(lista_produtos_pesquisados, new Comparator<Item_NFe>() {
                                @Override
                                public int compare(Item_NFe o1, Item_NFe o2) {
                                    Float val1 = o1.getValor() / o1.getQuantidade();
                                    Float val2 = o2.getValor() / o2.getQuantidade();
                                    return val1.compareTo(val2);
                                }
                            });
                            renderizaProdutosDaPesquisa();
                        }
                        if (!mostrando_pesquisados && lista_produtos_abaixo_media != null) {
                            Collections.sort(lista_produtos_abaixo_media, new Comparator<ProdutoAbaixoMedia>() {
                                @Override
                                public int compare(ProdutoAbaixoMedia o1, ProdutoAbaixoMedia o2) {
                                    Float val1 = o1.getValor();
                                    Float val2 = o2.getValor();
                                    return val1.compareTo(val2);
                                }
                            });
                            renderizaProdutosComDesconto();
                        }
                        popup.dismiss();
                    }
                });
                popup.getContentView().findViewById(R.id.pop_maiorPreco).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mostrando_pesquisados && lista_produtos_pesquisados != null) {
                            Collections.sort(lista_produtos_pesquisados, new Comparator<Item_NFe>() {
                                @Override
                                public int compare(Item_NFe o1, Item_NFe o2) {
                                    Float val1 = o1.getValor() / o1.getQuantidade();
                                    Float val2 = o2.getValor() / o2.getQuantidade();
                                    return val2.compareTo(val1);
                                }
                            });
                            renderizaProdutosDaPesquisa();
                        }
                        if (!mostrando_pesquisados && lista_produtos_abaixo_media != null) {
                            Collections.sort(lista_produtos_abaixo_media, new Comparator<ProdutoAbaixoMedia>() {
                                @Override
                                public int compare(ProdutoAbaixoMedia o1, ProdutoAbaixoMedia o2) {
                                    Float val1 = o1.getValor();
                                    Float val2 = o2.getValor();
                                    return val2.compareTo(val1);
                                }
                            });
                            renderizaProdutosComDesconto();
                        }
                        popup.dismiss();
                    }
                });
                popup.getContentView().findViewById(R.id.pop_data).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mostrando_pesquisados && lista_produtos_pesquisados != null) {
                            Collections.sort(lista_produtos_pesquisados, new Comparator<Item_NFe>() {
                                @Override
                                public int compare(Item_NFe o1, Item_NFe o2) {
                                    return o2.getData().compareTo(o1.getData());
                                }
                            });
                            renderizaProdutosDaPesquisa();
                        }
                        if (!mostrando_pesquisados && lista_produtos_abaixo_media != null) {
                            Collections.sort(lista_produtos_abaixo_media, new Comparator<ProdutoAbaixoMedia>() {
                                @Override
                                public int compare(ProdutoAbaixoMedia o1, ProdutoAbaixoMedia o2) {
                                    return o2.getData().compareTo(o1.getData());
                                }
                            });
                            renderizaProdutosComDesconto();
                        }
                        popup.dismiss();
                    }
                });
                popup.getContentView().findViewById(R.id.pop_mercado).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mostrando_pesquisados && lista_produtos_pesquisados != null) {
                            Collections.sort(lista_produtos_pesquisados, new Comparator<Item_NFe>() {
                                @Override
                                public int compare(Item_NFe o1, Item_NFe o2) {
                                    return o1.getTransient_mercado().getNome().compareTo(o2.getTransient_mercado().getNome());
                                }
                            });
                            renderizaProdutosDaPesquisa();
                        }
                        if (!mostrando_pesquisados && lista_produtos_abaixo_media != null) {
                            Collections.sort(lista_produtos_abaixo_media, new Comparator<ProdutoAbaixoMedia>() {
                                @Override
                                public int compare(ProdutoAbaixoMedia o1, ProdutoAbaixoMedia o2) {
                                    return o1.getNome_mercado().compareTo(o2.getNome_mercado());
                                }
                            });
                            renderizaProdutosComDesconto();
                        }
                        popup.dismiss();
                    }
                });
                popup.getContentView().findViewById(R.id.pop_valor_desconto).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mostrando_pesquisados && lista_produtos_pesquisados != null) {
                            Toast.makeText(Descontos.this, "Indisponível Para Produtos Pesquisados", Toast.LENGTH_LONG).show();
                        }
                        if (!mostrando_pesquisados && lista_produtos_abaixo_media != null) {
                            Collections.sort(lista_produtos_abaixo_media, new Comparator<ProdutoAbaixoMedia>() {
                                @Override
                                public int compare(ProdutoAbaixoMedia o1, ProdutoAbaixoMedia o2) {
                                    Float val1 = o1.getValor_medio() - o1.getValor();
                                    Float val2 = o2.getValor_medio() - o2.getValor();
                                    return val2.compareTo(val1);
                                }
                            });
                            renderizaProdutosComDesconto();
                        }
                        popup.dismiss();
                    }
                });
                popup.getContentView().findViewById(R.id.pop_porcentagem_desconto).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mostrando_pesquisados && lista_produtos_pesquisados != null) {
                            Toast.makeText(Descontos.this, "Indisponível Para Produtos Pesquisados", Toast.LENGTH_LONG).show();
                        }
                        if (!mostrando_pesquisados && lista_produtos_abaixo_media != null) {
                            Collections.sort(lista_produtos_abaixo_media, new Comparator<ProdutoAbaixoMedia>() {
                                @Override
                                public int compare(ProdutoAbaixoMedia o1, ProdutoAbaixoMedia o2) {
                                    Float val1 = ((o1.getValor_medio() - o1.getValor()) * 100) / o1.getValor_medio();
                                    Float val2 = ((o2.getValor_medio() - o2.getValor()) * 100) / o2.getValor_medio();
                                    return val2.compareTo(val1);
                                }
                            });
                            renderizaProdutosComDesconto();
                        }
                        popup.dismiss();
                    }
                });
            }
        });
    }

    /**
     * Pega as Listas de compras do usuário logado
     */
    @SuppressLint("StaticFieldLeak")
    private void pegaListasDeCompras() {
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

    /**
     * Configura a procura no edittext de procura de produtos
     */
    @SuppressLint("StaticFieldLeak")
    private void setEditTextProcuraProdutos() {
        TextWatcher tw = new TextWatcher() {
            @SuppressLint("StaticFieldLeak")
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
                            lista_produtos_pesquisados = list;
                            renderizaProdutosDaPesquisa();
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
                lista_produtos_abaixo_media = list;
                renderizaProdutosComDesconto();
            }
        }.execute();
    }


    private void renderizaProdutosComDesconto() {
        if (lista_produtos_abaixo_media != null) {
            mostrando_pesquisados = false;
            try {
                tv_quant_prods_desconto.setText("Produtos Abaixo do Valor Médio (" + lista_produtos_abaixo_media.size() + "):");
                layout_produtos_desconto.removeAllViews();
                DecimalFormat df = new DecimalFormat("0.00");
                for (final ProdutoAbaixoMedia produto : lista_produtos_abaixo_media) {
                    if (produto.getDescricao_produto2() != null && !produto.getDescricao_produto2().equalsIgnoreCase("") && !produto.getDescricao_produto2().equalsIgnoreCase("NULL")) {
                        produto.setDescricao_produto(produto.getDescricao_produto2());
                    }
                    Date date = sdf_bd.parse(produto.getData());
                    String data_exibicao = sdf_exibicao.format(date);
                    View item; // Creating an instance for View Object
                    LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    item = inflater.inflate(R.layout.layout_products, null);
                    ((TextView) item.findViewById(R.id.txtNomeProduto)).setText(produto.getDescricao_produto());
                    ((TextView) item.findViewById(R.id.txtNomeMercado)).setText(produto.getNome_mercado());
                    ((TextView) item.findViewById(R.id.txtDataNFe)).setText(data_exibicao);
                    ((TextView) item.findViewById(R.id.txtMediumPrice)).setText("R$ " + produto.getValor_medio());
                    float porcentagem_desconto = ((produto.getValor_medio() - produto.getValor()) * 100) / produto.getValor_medio();
                    ((TextView) item.findViewById(R.id.txtOff)).setText("R$ " + df.format(produto.getValor_medio() - produto.getValor()).replace(",", ".") + " (" + df.format(porcentagem_desconto).replace(",", ".") + "%)");
                    ((TextView) item.findViewById(R.id.txtPrice)).setText("R$ " + df.format(produto.getValor()).replace(",", ".") + " " + produto.getUnidade_comercial());
                    item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog dialog_opcoes_produto = new Dialog(Descontos.this);
                            dialog_opcoes_produto.requestWindowFeature(Window.FEATURE_NO_TITLE); //no toolbar
                            dialog_opcoes_produto.setContentView(R.layout.dialog_opcoes_produto_abaixo_media);

                            //change alpha intensity
                            WindowManager.LayoutParams lp = dialog_opcoes_produto.getWindow().getAttributes();
                            lp.dimAmount = 0.8f;
                            dialog_opcoes_produto.getWindow().setAttributes(lp);
                            dialog_opcoes_produto.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);


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
                                    intent.putExtra("ID_PRODUTO", p.getId_produto());
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
            } catch (Exception e) {
                Toast.makeText(this, "Erro ao Exibir Produtos", Toast.LENGTH_LONG).show();
                System.out.println(">>> Erro: " + e.getMessage());
            }
        } else {
            Toast.makeText(Descontos.this, "Nenhum Item Encontrado", Toast.LENGTH_LONG).show();
        }
    }


    private void renderizaProdutosDaPesquisa() {
        if (lista_produtos_pesquisados != null) {
            mostrando_pesquisados = true;
            try {
                tv_quant_prods_desconto.setText("Produtos Encontrados (" + lista_produtos_pesquisados.size() + "):");
                layout_produtos_desconto.removeAllViews();
                DecimalFormat df = new DecimalFormat("0.00");
                for (final Item_NFe item : lista_produtos_pesquisados) {
                    if (item.getProduto().getDescricao2() != null && !item.getProduto().getDescricao2().equalsIgnoreCase("") && !item.getProduto().getDescricao2().equalsIgnoreCase("NULL")) {
                        item.getProduto().setDescricao(item.getProduto().getDescricao2());
                    }
                    item.setValor(item.getValor() / item.getQuantidade());
                    Date date = sdf_bd.parse(item.getData());
                    String data_exibicao = sdf_exibicao.format(date);
                    View view; // Creating an instance for View Object
                    LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = inflater.inflate(R.layout.layout_products, null);
                    ((TextView) view.findViewById(R.id.txtNomeProduto)).setText(item.getProduto().getDescricao());
                    ((TextView) view.findViewById(R.id.txtNomeMercado)).setText(item.getTransient_mercado().getNome());
                    ((TextView) view.findViewById(R.id.txtDataNFe)).setText(data_exibicao);
                    ((TextView) view.findViewById(R.id.txtMediumPrice)).setText("R$ -");
                    ((TextView) view.findViewById(R.id.txtOff)).setText("R$ -");
                    ((TextView) view.findViewById(R.id.txtPrice)).setText("R$ " + df.format(item.getValor()).replace(",", ".") + " " + item.getProduto().getUnidade_comercial());
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Dialog dialog_opcoes_produto = new Dialog(Descontos.this);

                            dialog_opcoes_produto.requestWindowFeature(Window.FEATURE_NO_TITLE); //no toolbar
                            dialog_opcoes_produto.setContentView(R.layout.dialog_opcoes_produto_abaixo_media);

                            //change alpha intensity
                            WindowManager.LayoutParams lp = dialog_opcoes_produto.getWindow().getAttributes();
                            lp.dimAmount = 0.8f;
                            dialog_opcoes_produto.getWindow().setAttributes(lp);
                            dialog_opcoes_produto.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);


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
                                    intent.putExtra("ID_PRODUTO", p.getId_produto());
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
            } catch (Exception e) {
                Toast.makeText(this, "Erro ao Exibir Produtos", Toast.LENGTH_LONG).show();
                System.out.println(">>> Erro: " + e.getMessage());
            }
        } else {
            Toast.makeText(Descontos.this, "Nenhum Item Encontrado", Toast.LENGTH_LONG).show();
        }
    }


    @SuppressLint("StaticFieldLeak")
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


    @SuppressLint("StaticFieldLeak")
    void pegaProdutoPorCodigoDeBarras(final String ean) {
        new AsyncTask<String, Void, Produto_Detalhado>() {

            @Override
            protected Produto_Detalhado doInBackground(String... params) {
                Produto_Detalhado produto_detalhado = null;
                try {
                    String urlParameters = "funcao=GET_PRODUTO_DETALHADO_BY_EAN&ean=" + ean;
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
                    produto_detalhado = (Produto_Detalhado) ois.readObject();
                    ois.close();

                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
                return produto_detalhado;
            }

            @Override
            protected void onPostExecute(Produto_Detalhado produto_detalhado) {
                if (produto_detalhado != null) {
                    Intent intent = new Intent(Descontos.this, VerProduto.class);
                    intent.putExtra("PRODUTO_DETALHADO", produto_detalhado);
                    startActivity(intent);
                } else {
                    Toast.makeText(Descontos.this, "Produto não Encontrado", Toast.LENGTH_LONG).show();
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


    //on back
    @Override
    public void onBackPressed() {
        if (permiteVoltar) {
            super.onBackPressed();
        } else {
            if (usuario.getId_usuario() != 0) {

                //snack
                final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.layout_principal);
                if (backAlreadyPressed) {
                    finish();
                    System.exit(0);
                    return;
                }
                backAlreadyPressed = true;
                Snackbar snackbar = Snackbar.make(coordinatorLayout, "Pressione novamente para SAIR.", Snackbar.LENGTH_LONG);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        backAlreadyPressed = false;
                    }
                }, 3000);
                TextView txtSnackBar = snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                    txtSnackBar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                else
                    txtSnackBar.setGravity(Gravity.CENTER_HORIZONTAL);
                txtSnackBar.setGravity(Gravity.CENTER_HORIZONTAL);
                txtSnackBar.setTextColor(Color.parseColor("#ffffff"));
                //snackbar.getView().setBackgroundColor(Color.parseColor("#242a31"));
                snackbar.getView().setBackgroundResource(R.drawable.gradient_list);
                snackbar.show();


            } else {
                Intent main = new Intent(Descontos.this, Main.class);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                startActivity(main);
                finish();
            }
        }
    }

}
