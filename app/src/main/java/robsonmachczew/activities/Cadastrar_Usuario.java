package robsonmachczew.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import entidade.Usuario;
import entidade.Utils;

public class Cadastrar_Usuario extends AppCompatActivity {

    private Button btnChoose;
    private ImageView imgUpload;
    private TextView txtImageDesc;

    private static final int SELECT_PICTURE = 1;
    private Uri selectedImage;

    private EditText inputName;
    private EditText edtEmail;
    private EditText edtPass;
    private EditText edtCPF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.bar);
        toolbar.setTitle(R.string.bar_register);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_bg));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //basic config
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.toolbar_status));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        imgUpload = findViewById(R.id.imgUpload);
        txtImageDesc = findViewById(R.id.txtImageDesc);

        btnChoose = findViewById(R.id.btnChoose);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, SELECT_PICTURE);
            }
        });

        inputName = findViewById(R.id.inputName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        edtCPF = findViewById(R.id.edtCPF);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImage = data.getData();
            txtImageDesc.setVisibility(View.GONE);
            imgUpload.setVisibility(View.VISIBLE);
            imgUpload.setImageURI(selectedImage);
        }

    }

    public void btnRegisterUser(View view) {
        final Usuario usuario = new Usuario();
        usuario.setNome(inputName.getText().toString());
        usuario.setEmail(edtEmail.getText().toString());
        usuario.setSenha(edtPass.getText().toString());
        usuario.setCpf(edtCPF.getText().toString());
        new AsyncTask<String, Void, Usuario>() {

            @Override
            protected void onPreExecute() {
                //progWait.setVisibility(View.VISIBLE);
                //txtWait.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected Usuario doInBackground(String... params) {
                Usuario u = new Usuario();
                try {
                    URL url = new URL(Utils.URL + "cadastrar_usuario");
                    HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                    urlCon.setRequestMethod("POST");
                    urlCon.setDoOutput(true);
                    urlCon.setDoInput(true);

                    ObjectOutputStream wr = new ObjectOutputStream(urlCon.getOutputStream());
                    wr.writeObject(usuario);
                    wr.close();
                    wr.flush();

                    ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream());
                    u = (Usuario) ois.readObject();
                    ois.close();

                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
                return u;
            }

            @Override
            protected void onPostExecute(Usuario u) {
                if (u.getId_usuario() != 0) {
                    Toast.makeText(Cadastrar_Usuario.this, "REGISTRADO", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }


    //usado para executar o voltar da action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent login = new Intent(Cadastrar_Usuario.this, Login.class);
        startActivity(login);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
        return super.onOptionsItemSelected(item);
    }

}
