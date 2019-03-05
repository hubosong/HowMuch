package robsonmachczew.activities;

import android.annotation.SuppressLint;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import entidade.Mercado;
import entidade.Usuario;
import entidade.Utils;

public class VerMercado extends Nav implements OnStreetViewPanoramaReadyCallback /*OnMapReadyCallback*/ {

    private Mercado mercado;

    private Usuario usuario;
    private TextView tv_descricao_local;
    private TextView txt_endereco;
    private TextView txt_telefone;
    private TextView txt_cnpj;
    private DecimalFormat decFormat = new DecimalFormat("'R$ ' #,##0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //basic config
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_ver_mercado, contentFrameLayout);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        getSupportActionBar().setTitle("Detalhes Do Mercado");

        /*
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        */
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
            System.out.println(mercado.getCnpj());
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


    /*
    @Override
    public void onMapReady(GoogleMap mMap) {
        //customizar style do maps
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapsstyle));

            if (!success) {
                Log.e("MapaActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapaActivity", "Can't find style. Error: ", e);
        }

        // Add a marker in Sydney and move the camera
        // achar lat lon --> http://www.mapcoordinates.net/pt
        LatLng santaMaria = new LatLng(-29.6813285, -53.8134987);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(santaMaria));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(santaMaria));
        mMap.animateCamera(CameraUpdateFactory.zoomTo( 15.5f ) );
        mMap.addMarker(new MarkerOptions()
                .position(santaMaria)
                .title("Marcado em Santa Maria")
                .snippet("texto teste")
                .alpha(0.7f)
                .flat(true) //girar marcador junto a tela
                .position(santaMaria)
                //.rotation(90)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
        // Changing marker icon
        //marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.my_marker_icon)));


    }
    */

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        streetViewPanorama.setPosition(new LatLng(-29.68493155793035, -53.79259005591795));
        streetViewPanorama.animateTo(new StreetViewPanoramaCamera.Builder().
                orientation( new StreetViewPanoramaOrientation(0, -90))
                .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                .build(), 2000);

    }
}
