package robsonmachczew.howmuch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.miguelcatalan.materialsearchview.utils.AnimationUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import entidade.Produto;

public class BuyListActivity extends NavActivity {

    private TextView txtList;
    private EditText edtSearch;
    private LinearLayout layoutProdutos;
    private ArrayList<Produto> lista_compras;

    private MaterialSearchView searchView;
    private final Activity activity = this;
    private Animation alpha_in, alpha_out;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //called by nav
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_buy_list, contentFrameLayout);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);

        //basic config
        getSupportActionBar().setTitle(R.string.bar_buy_list);

        alpha_in = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        alpha_out = AnimationUtils.loadAnimation(this, R.anim.alpha_out);

        /*
        //searchview
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView();
        */


        txtList = findViewById(R.id.txtList);
        txtList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(BuyListActivity.this);
                //dialog.setTitle("Produtos Adicionados");

                TextView title = new TextView(BuyListActivity.this);
                //title.setText("Produtos Adicionados");
                title.setText("Menor preco: R$ 122,39");
                title.setBackgroundColor(ContextCompat.getColor(BuyListActivity.this, R.color.toolbar_status));
                title.setPadding(10, 10, 10, 10);
                title.setGravity(Gravity.CENTER);
                title.setTextColor(Color.WHITE);
                title.setTextSize(20);
                title.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.ic_maps);
                dialog.setCustomTitle(title);

                //call market data
                title.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(BuyListActivity.this, "Mercado Hu", Toast.LENGTH_SHORT).show();
                    }
                });

                //list
                dialog.setView(LayoutInflater.from(BuyListActivity.this).inflate(android.R.layout.simple_list_item_1,null));
                String[] someList = {"02 | Vinho Hu", "05 | Cerveja Hu", "02 | Batata Hu", "03 | Arroz Hu", "10 | Massa Hu"};
                dialog.setItems(someList, null);

                dialog.setCancelable(false);
                dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(activity, "Cancelado!", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(activity, "Lista Salva!", Toast.LENGTH_SHORT).show();
                    }
                });

                AlertDialog alert = dialog.create();
                alert.show();
            }
        });


        layoutProdutos = (LinearLayout) findViewById(R.id.layoutProdutos);
        lista_compras = new ArrayList<>();


        edtSearch = findViewById(R.id.edtSearch);
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

    /*
    //searchView
    public void searchView() {
        searchView.setHint("Consultar Produto..");
        searchView.setHintTextColor(R.color.hint_nav_login);
        //searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        searchView.setVoiceSearch(false);

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txtManyList.setText(query);
                txtManyList.startAnimation(alpha_in);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                Window window = activity.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(activity, R.color.searchview_status));

                fab.setVisibility(View.GONE);
                fab.startAnimation(alpha_out);


            }

            @Override
            public void onSearchViewClosed() {
                Window window = activity.getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(activity, R.color.toolbar_status));

                fab.setVisibility(View.VISIBLE);
                fab.startAnimation(alpha_in);

            }
        });

    }
    */


    @SuppressLint("StaticFieldLeak")
    private void realizaPesquisaProdutos(final String pesquisa) {
        new AsyncTask<String, Void, ArrayList<Produto>>() {
            @Override
            protected void onPreExecute() {
                layoutProdutos.removeAllViews();

                TextView txtCarregando = new TextView(BuyListActivity.this);
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

                    URL url = new URL("http://187.181.170.135:8080/Mercado/produto");
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
                    LinearLayout l = new LinearLayout(BuyListActivity.this);
                    l.setOrientation(LinearLayout.HORIZONTAL);

                    Button btnAdd = new Button(BuyListActivity.this);
                    btnAdd.setText("+ Add");
                    btnAdd.setMinimumHeight(0);
                    btnAdd.setHeight(80);
                    btnAdd.setGravity(Gravity.CENTER);
                    btnAdd.setTextColor(Color.parseColor("#ffffff"));
                    btnAdd.setBackgroundResource(R.drawable.buttons_login);

                    final EditText edtQtd = new EditText(BuyListActivity.this);
                    edtQtd.setText("1.0");
                    edtQtd.setTextColor(Color.parseColor("#a8acb1"));

                    final TextView txtDesc = new TextView(BuyListActivity.this);
                    txtDesc.setText(produto.getDescricao());
                    txtDesc.setTextColor(Color.parseColor("#ffffff"));
                    txtDesc.setPadding(5,0,0,0);

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
                            txtList.setText("Produtos Add: ("+lista_compras.size()+")");
                        }
                    });
                }
            }
        }.execute();
    }



    /*
    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        //search bar
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    */


    //onBack
    @Override
    public void onBackPressed() {
        Intent main = new Intent(BuyListActivity.this, MainActivity.class);
        startActivity(main);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}
