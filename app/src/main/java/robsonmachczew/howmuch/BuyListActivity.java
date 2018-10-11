package robsonmachczew.howmuch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.design.widget.NavigationView;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import entidade.Produto;

public class BuyListActivity extends NavActivity {

    private TextView txtNomeLista;
    private LinearLayout layoutProdutos;
    private ImageView imgLogo;
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);

        //basic config
        getSupportActionBar().setTitle(R.string.bar_buy_list);

        alpha_in = AnimationUtils.loadAnimation(this, R.anim.alpha_in);
        alpha_out = AnimationUtils.loadAnimation(this, R.anim.alpha_out);

        //searchview
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView();


        txtNomeLista = findViewById(R.id.txtNomeLista);
        txtNomeLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "teste", Toast.LENGTH_SHORT).show();
            }
        });

        lista_compras = new ArrayList<>();
        layoutProdutos = (LinearLayout) findViewById(R.id.layoutProdutos);
        imgLogo = findViewById(R.id.imgLogo);

    }

    //searchView
    public void searchView() {
        searchView.setEllipsize(true);
        searchView.setAnimationDuration(700);
        searchView.setHint("Consultar Produto..");
        searchView.setHintTextColor(R.color.hint_nav_login);
        searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                txtNomeLista.setText(query);
                txtNomeLista.startAnimation(alpha_in);
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

                    imgLogo.setVisibility(View.GONE);

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
                            //txtNomeLista.setText(editNomeLista.getText() + " ("+lista_compras.size()+" Produtos)");
                        }
                    });
                }
            }
        }.execute();
    }



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


    //onBack
    @Override
    public void onBackPressed() {
        Intent main = new Intent(BuyListActivity.this, MainActivity.class);
        startActivity(main);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}
