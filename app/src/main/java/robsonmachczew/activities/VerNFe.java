package robsonmachczew.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONArray;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import adapter.ItemNFeAdapter;
import adapter.ProdutoAbaixoMediaAdapter;
import dao.NFe_DAO;
import entidade.Item_NFe;
import entidade.Mercado;
import entidade.NFe;
import entidade.Produto;
import entidade.ProdutoAbaixoMedia;
import entidade.Usuario;
import entidade.Utils;

public class VerNFe extends Nav {

    private ProgressBar progWait;
    private TextView txtQRCode, txtWait, txtMercado, txtData;
    private MaterialSearchView searchView;
    private RecyclerView recyclerView;
    private final Activity activity = this;
    public Animation alpha_in, alpha_out;
    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_read_qrcode, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle(R.string.bar_qrcode);

        alpha_in = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        alpha_out = AnimationUtils.loadAnimation(this, R.anim.alpha_out);


        NFe nfe = (NFe) getIntent().getSerializableExtra("NFE");


        progWait = findViewById(R.id.progWait);
        txtWait = findViewById(R.id.txtWait);
        txtQRCode = findViewById(R.id.txtQRCode);
        txtMercado = findViewById(R.id.txtDataLista);
        txtData = findViewById(R.id.txtQtdItems);

        usuario = Utils.loadFromSharedPreferences(this);

        //recyclerview
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        ProdutoAbaixoMediaAdapter tmp_adapter = new ProdutoAbaixoMediaAdapter(this, new ArrayList<ProdutoAbaixoMedia>());
        recyclerView.setAdapter(tmp_adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (nfe != null) {
            preencherViewsProdutosNFe(nfe);
        } else {
            String code = getIntent().getStringExtra("code");
            //getNFeFromPHP(code);
            pegaNotaDoSite(code);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public void postHttpQRCode(final String code) {
        if ((code != null) && (code.length() == 44)) {
            new AsyncTask<String, Void, NFe>() {
                @Override
                protected void onPreExecute() {
                    progWait.setVisibility(View.VISIBLE);
                    txtWait.setVisibility(View.VISIBLE);
                    super.onPreExecute();
                }

                @Override
                protected NFe doInBackground(String... params) {
                    NFe nfe = null;
                    try {
                        String urlParameters = "chavenfe=" + code + "&idusuario=" + Utils.loadFromSharedPreferences(VerNFe.this).getId_usuario();
                        System.out.println("Enviando chave: " + code);
                        byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                        URL url = new URL(Utils.URL + "enviar_id_nfe");
                        HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                        urlCon.setRequestMethod("POST");
                        urlCon.setDoOutput(true); // Habilita o envio da chave por stream
                        urlCon.setDoInput(true); // Habilita o recebimento via stream

                        DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream()); // Stream que envia a chave para o servidor
                        wr.write(postData); // Envia a chave
                        wr.close();
                        wr.flush();

                        ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream()); // Stream que vai receber um objeto do tipo NFe
                        nfe = (NFe) ois.readObject();
                        ois.close();
                        if (nfe != null && nfe.getId_nfe() != 0) {
                            new NFe_DAO(VerNFe.this).insertFromServer(nfe);
                        }
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }
                    return nfe;
                }

                @Override
                protected void onPostExecute(NFe nfe) {
                    txtQRCode.setText(code);
                    preencherViewsProdutosNFe(nfe);
                }
            }.execute(code);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private void pegaNotaDoSite(final String code) {
        //Verifica se a nota já foi processada do site e colocada no BD
        NFe nota = new NFe_DAO(this).getByChave(code);
        if (nota != null) {
            //Caso a nota já esteja no BD...
            preencherViewsProdutosNFe(nota);
        } else {
            new AsyncTask<String, Integer, NFe>() {
                @Override
                protected void onPreExecute() {
                    progWait.setVisibility(View.VISIBLE);
                    txtWait.setVisibility(View.VISIBLE);
                    super.onPreExecute();
                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                    super.onProgressUpdate(values);
                    switch (values[0]) {
                        case 1:
                            txtWait.setText("Processando Dados Básicos..");
                            break;
                        case 2:
                            txtWait.setText("Processando Dados do Mercado..");
                            break;
                        case 3:
                            txtWait.setText("Processando Itens..");
                            break;
                        case 4:
                            txtWait.setText("Concluindo..");
                            break;
                    }
                }

                @Override
                protected NFe doInBackground(String... params) {
                    String url = "https://www.sefaz.rs.gov.br/NFE/NFE-COM.aspx";
                    String url2 = "https://www.sefaz.rs.gov.br/ASP/AAE_ROOT/NFE/SAT-WEB-NFE-COM_2.asp";
                    NFe nfe = null;
                    try {
                        publishProgress(1);
                        Connection.Response res = Jsoup.connect(url).method(Connection.Method.POST).execute();
                        publishProgress(2);
                        Document doc = Jsoup.connect(url2)
                                .data("HML", "false")
                                .data("chaveNFe", code)
                                .data("Action", "Avan%E7ar")
                                .cookies(res.cookies())
                                .method(Connection.Method.POST)
                                .get();

                        //Pega os dados da Nota Fiscal Eletronica
                        Element divNFe = doc.getElementById("NFe");
                        nfe = getNFe(divNFe);
                        if(divNFe != null) {
                            nfe.setChave(code);
                            if (usuario.getId_usuario() != 0) {
                                nfe.setId_usuario(usuario.getId_usuario());
                            }

                            //Pega os dados do Mercado
                            Element divEminente = doc.getElementById("Emitente");
                            Mercado mercado = getMercado(divEminente);
                            nfe.setMercado(mercado);

                            //Pega os items e produtos da Nota Fiscal Eletrônica e os cadastra no BD
                            publishProgress(3);
                            Element divProd = doc.getElementById("Prod");
                            ArrayList<Item_NFe> lista_itens = getItemsNFe(divProd);
                            nfe.setLista_items(lista_itens);


                            //Se o app conseguiu pegar a nota no site, vamos envia-la para o servidor.
                            System.out.println("Enviando nfe: " + nfe);
                            publishProgress(4);
                            URL url3 = new URL(Utils.URL + "enviar_nfe");
                            HttpURLConnection urlCon = (HttpURLConnection) url3.openConnection();
                            urlCon.setRequestMethod("POST");
                            urlCon.setDoOutput(true); // Habilita o envio da chave por stream
                            urlCon.setDoInput(true); // Habilita o recebimento via stream

                            OutputStream os = urlCon.getOutputStream();
                            ObjectOutputStream objectOutputStream = new ObjectOutputStream(os);
                            objectOutputStream.writeObject(nfe);
                            objectOutputStream.close();
                            objectOutputStream.flush();

                            ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream()); // Stream que vai receber um objeto do tipo NFe
                            nfe = (NFe) ois.readObject();
                            ois.close();
                            if (nfe.getId_nfe() != 0) {
                                new NFe_DAO(VerNFe.this).insertFromServer(nfe);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return nfe;
                }

                @Override
                protected void onPostExecute(NFe nfe) {
                    txtWait.setText("Carregando..");
                    final Intent intent = new Intent(activity, VerNFe.class);
                    if (nfe != null && nfe.getLista_items() != null && nfe.getLista_items().size() > 0) {
                        System.out.println("NFe Recebida do servidor: " + nfe);
                        preencherViewsProdutosNFe(nfe);
                    } else {
                        //Se o app NÃO conseguiu pegar a nota no site, vamos apenas enviar o código e deixar que o servidor pegue-a no site.
                        postHttpQRCode(code);
                    }
                }
            }.execute(code);
        }
    }

    public void preencherViewsProdutosNFe(NFe nfe) {
        progWait.setVisibility(View.GONE);
        txtWait.setVisibility(View.GONE);
        if (nfe == null) {
            Toast.makeText(this, "NFe não encontrada", Toast.LENGTH_LONG).show();
            return;
        }
        txtMercado.setText(nfe.getMercado().getNome());
        txtData.setText(nfe.getData());
        txtQRCode.setText(nfe.getChave());

        TextView txtMarket = findViewById(R.id.txtDataLista);
        TextView txtDate = findViewById(R.id.txtQtdItems);

        Toast.makeText(VerNFe.this, nfe.getLista_items().size() + " itens encontrados", Toast.LENGTH_LONG).show();

        ItemNFeAdapter adapter = new ItemNFeAdapter(this, nfe.getLista_items());
        recyclerView.setAdapter(adapter);

        txtMarket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(VerNFe.this, R.string.toast_error, Toast.LENGTH_SHORT).show();
            }
        });

        //hide floating button when scroll
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                    fab.startAnimation(alpha_out);
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                    fab.startAnimation(alpha_in);
                }
            }
        });
    }


    // NÃO APAGAR ESSE MÉTODO
    @SuppressLint("StaticFieldLeak")
    private void sendChaveToPHP(final String chave) {
        new AsyncTask<String, Void, JSONArray>() {
            @Override
            protected void onPreExecute() {

            }

            @Override
            protected JSONArray doInBackground(String... params) {
                try {
                    URL url = new URL("http://192.168.0.99/mercado/dao/nfe/select_nfe_completa_by_chave.php");
                    HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                    urlCon.setRequestMethod("POST");
                    urlCon.setDoOutput(true); // to be able to write.
                    urlCon.setDoInput(true); // to be able to read.
                    OutputStream out = urlCon.getOutputStream();

                    out.write(("&" + URLEncoder.encode("chave", "UTF-8") + "=" + URLEncoder.encode(chave, "UTF-8")
                            + "&" + URLEncoder.encode("id_usuario", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(usuario.getId_usuario()), "UTF-8")).getBytes());
                    out.close();
                    out.flush();

                    InputStream ois = urlCon.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(ois));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    //NO ANDROID USAR JSONARRAY:
                    JSONArray array = new JSONArray(result.toString());
                    ois.close();
                    return array;
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(JSONArray array) {
                try {
                    NFe nfe = new NFe();

                    //System.out.println("NAMES >> " + array.getJSONObject(0).names());
                    //nfe.setId_nfe();
                    ArrayList<NFe> lista = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONArray j = new JSONArray(array.get(i));
                        System.out.println(array.get(i).getClass());
                    }
                } catch (Exception e) {
                    System.out.println(">>> ERRO: " + e.getMessage());
                }
            }
        }.execute(chave);
    }


    // Métodos que buscam a NFe no site do sefaz. A ideia é distribuir esse processamento
    // (de buscar a nota) para desafogar o servidor.
    // DEIXAR PRIVADOS

    private static ArrayList<Item_NFe> getItemsNFe(Element div) {
        Elements tabs_basicas = div.getElementsByClass("toggle box");
        Elements tabs_detalhadas = div.getElementsByClass("toggable box");
        ArrayList<Item_NFe> lista = new ArrayList<>();
        if (tabs_basicas.size() == tabs_detalhadas.size()) {
            for (int i = 0; i < tabs_basicas.size(); i++) {
                Item_NFe item = new Item_NFe();
                Produto produto = new Produto();

                Elements tds_basicas = tabs_basicas.get(i).getElementsByTag("td");
                Elements tds_detalhes = tabs_detalhadas.get(i).getElementsByTag("td");

                for (Element td : tds_basicas) {
                    if (td.toString().contains("<td class=\"fixo-prod-serv-descricao\">")) {
                        produto.setDescricao(getSpanContent(td.toString()));
                    }
                    if (td.toString().contains("<td class=\"fixo-prod-serv-uc\">")) {
                        produto.setUnidade_comercial(getSpanContent(td.toString()));
                    }
                    if (td.toString().contains("<td class=\"fixo-prod-serv-qtd\">")) {
                        item.setQuantidade(Float.valueOf(getSpanContent(td.toString()).trim().replace(",", ".")));
                    }
                    if (td.toString().contains("<td class=\"fixo-prod-serv-vb\">")) {
                        item.setValor(Float.valueOf(getSpanContent(td.toString()).trim().replace(",", ".")));
                    }
                }
                for (Element td : tds_detalhes) {
                    if (td.toString().contains("<label>Código EAN Comercial</label>")) {
                        String s = getSpanContent(td.toString().trim());
                        if (s.length() > 10 && s.length() < 16) {
                            produto.setCod_EAN_comercial(Long.valueOf(s));
                        }
                    }
                    if (td.toString().contains("<label>Código NCM</label>")) {
                        String s = getSpanContent(td.toString().trim());
                        if (!s.equalsIgnoreCase("")) {
                            produto.setCodigo_ncm(Long.valueOf(getSpanContent(td.toString()).trim()));
                        }
                    }
                    if (td.toString().contains("<label>Código do Produto</label>")) {
                        String s = getSpanContent(td.toString().trim());
                        if (s.length() > 0 && s.length() < 15) {
                            produto.setCodigo_do_produto(Long.valueOf(getSpanContent(td.toString()).trim()));
                        }
                    }
                }
                if (produto.getCod_EAN_comercial() != 0) {
                    try {
                        //Tenta pegar mais inf. do produto
                        Document doc = Jsoup.connect("https://cosmos.bluesoft.com.br/produtos/" + produto.getCod_EAN_comercial()).get();
                        String h1 = doc.getElementsByTag("h1").toString().replace("<h1 class=\"page-header\"> ", "");
                        int ini_img = h1.indexOf("<img");
                        h1 = h1.substring(0, ini_img - 1).trim();
                        produto.setDescricao2(h1);
                    } catch (Exception e) {
                        System.out.println(">>> ERRO tentando pegar infos do produto (EAN: +" + produto.getCod_EAN_comercial() + "): " + e.getMessage());
                    }
                }
                item.setProduto(produto);
                lista.add(item);
            }
        }
        return lista;
    }

    private static NFe getNFe(Element div) {
        if (div == null) {
            return null;
        }
        NFe nfe = new NFe();
        Elements els = div.getElementsByTag("td");
        for (Element e : els) {
            if (e.toString().contains("<label>Modelo</label>")) {
                nfe.setModelo(getSpanContent(e.toString()));
            }
            if (e.toString().contains("<label>Série</label>")) {
                nfe.setSerie(getSpanContent(e.toString()));
            }
            if (e.toString().contains("<label>Número</label>")) {
                nfe.setNumero(getSpanContent(e.toString()));
            }
            if (e.toString().contains("<label>Data de Emissão</label>")) {
                String[] datas = getSpanContent(e.toString()).split(" ");
                String[] dt = datas[0].split("/");
                nfe.setData(dt[2] + "-" + dt[1] + "-" + dt[0] + " " + datas[1].substring(0, 8));
            }
            if (e.toString().contains("<label>Valor&nbsp;Total&nbsp;da&nbsp;Nota&nbsp;Fiscal&nbsp;&nbsp;</label>")) {
                nfe.setValor(Float.valueOf(getSpanContent(e.toString()).replace(",", ".").trim()));
            }
            if (e.toString().contains("<label>Forma de Pagamento</label>")) {
                nfe.setForma_pagamento(getSpanContent(e.toString()));
            }
        }
        return nfe;
    }

    private static Mercado getMercado(Element div) {
        Mercado m = new Mercado();
        Elements els = div.getElementsByTag("td");
        for (Element e : els) {
            if (e.toString().contains("<label>Nome / Razão Social</label>")) {
                m.setNome(getSpanContent(e.toString()));
            }
            if (e.toString().contains("<label>Nome Fantasia</label>")) {
                m.setNome_fantasia(getSpanContent(e.toString()));
            }
            if (e.toString().contains("<label>CNPJ</label>")) {
                m.setCnpj(getSpanContent(e.toString()));
            }
            if (e.toString().contains("<label>Endereço</label>")) {
                m.setEndereco(getSpanContent(e.toString()));
            }
            if (e.toString().contains("<label>Bairro / Distrito</label>")) {
                m.setBairro(getSpanContent(e.toString()));
            }
            if (e.toString().contains("<label>CEP</label>")) {
                m.setCep(getSpanContent(e.toString()));
            }
            if (e.toString().contains("<label>Município</label>")) {
                m.setMunicipio(getSpanContent(e.toString()));
            }
            if (e.toString().contains("<label>Telefone</label>")) {
                m.setTelefone(getSpanContent(e.toString()));
            }
            if (e.toString().contains("<label>UF</label>")) {
                m.setUf(getSpanContent(e.toString()));
            }
            if (e.toString().contains("<label>País</label>")) {
                m.setPais(getSpanContent(e.toString()));
            }
            if (e.toString().contains("<label>Inscrição Estadual</label>")) {
                m.setInscricao_estadual(getSpanContent(e.toString()));
            }
            if (e.toString().contains("<label>Código de Regime Tributário</label>")) {
                m.setCod_regime_tributario(getSpanContent(e.toString()));
            }
        }
        return m;
    }

    private static String getSpanContent(String span) {
        int ini = span.indexOf("<span>") + 6;
        int fim = span.indexOf("</span>");
        String s = span.substring(ini, fim).replace("&nbsp;", " ").replace("&amp;", "&").replace("  ", " ").trim();
        return s;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}