package robsonmachczew.activities;

import android.annotation.SuppressLint;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import entidade.Mercado;
import entidade.Usuario;
import entidade.Utils;

public class VerMercado extends Nav implements OnStreetViewPanoramaReadyCallback {

    private Mercado mercado;

    private Usuario usuario;
    private TextView tv_descricao_local;
    private TextView txt_endereco;
    private TextView txt_telefone;
    private TextView txt_cnpj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_ver_mercado, contentFrameLayout);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Detalhes Do Mercado");

        StreetViewPanoramaFragment streetViewPanoramaFragment = (StreetViewPanoramaFragment) getFragmentManager().findFragmentById(R.id.map);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        tv_descricao_local = findViewById(R.id.tv_descricao_local);
        txt_endereco = findViewById(R.id.txt_endereco);
        txt_telefone = findViewById(R.id.txt_telefone);
        txt_cnpj = findViewById(R.id.txt_cnpj);

        usuario = Utils.loadFromSharedPreferences(this);

        mercado = (Mercado) getIntent().getSerializableExtra("MERCADO");
        if(mercado == null) {
            getDetalhesMercado(getIntent().getLongExtra("ID_MERCADO", 0));
        }else{
            renderizaMercado();
        }


    }


    @SuppressLint("SetTextI18n")
    private void renderizaMercado() {
        if (mercado != null) {
            tv_descricao_local.setText(mercado.getNome_fantasia());
            txt_endereco.setText(mercado.getEndereco());
            txt_telefone.setText(mercado.getTelefone());
            txt_cnpj.setText(mercado.getCnpj());

        }
    }

    @SuppressLint("StaticFieldLeak")
    private void getDetalhesMercado(final long id_mercado) {
        new AsyncTask<String, Void, Mercado>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Mercado doInBackground(String... params) {
                //mercado mercado = null;
                try {
                    String urlParameters = "funcao=GET_MERCADO&id_mercado="+id_mercado;
                    byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                    URL url = new URL(Utils.URL + "mercado");
                    HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                    urlCon.setRequestMethod("POST");
                    urlCon.setDoOutput(true);
                    urlCon.setDoInput(true);

                    DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
                    wr.write(postData);
                    wr.close();
                    wr.flush();

                    ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream());
                    mercado = (Mercado) ois.readObject();
                    ois.close();

                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
                return mercado;
            }

            @Override
            protected void onPostExecute(Mercado mercado) {
                mercado = mercado;
                if (mercado != null) {
                    renderizaMercado();
                }
            }
        }.execute();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        Double latitude = -29.68493155793035;
        Double longitude = -53.79259005591795;

        streetViewPanorama.setPosition(new LatLng(latitude, longitude));
        streetViewPanorama.animateTo(new StreetViewPanoramaCamera.Builder().
                orientation( new StreetViewPanoramaOrientation(0, -90))
                .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                .build(), 2000);

    }
}
