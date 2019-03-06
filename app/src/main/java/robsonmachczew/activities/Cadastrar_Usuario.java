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
    private EditText edtCidade;
    private EditText edtUF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar);

        setResult(RESULT_CANCELED);

        //basic config
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.toolbar_status));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.bar);
        toolbar.setTitle(R.string.bar_register);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_bg));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
        edtCidade = findViewById(R.id.edtCidade);
        edtUF = findViewById(R.id.edtUF);
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
        usuario.setCidade(edtCidade.getText().toString());
        usuario.setUf(edtUF.toString());
        if (!usuario.getEmail().contains("@") || !usuario.getEmail().contains(".") || usuario.getEmail().length() < 5) {
            Toast.makeText(Cadastrar_Usuario.this, "Email inválido", Toast.LENGTH_LONG).show();
            return;
        }
        if (usuario.getNome().length() < 2 || usuario.getNome().matches(".*\\d+.*") || !usuario.getNome().contains(" ")) {
            Toast.makeText(Cadastrar_Usuario.this, "Nome inválido", Toast.LENGTH_LONG).show();
            return;
        }
        if (usuario.getSenha().length() < 6) {
            Toast.makeText(Cadastrar_Usuario.this, "Senha menor que 6 dígitos", Toast.LENGTH_LONG).show();
            return;
        }
        if(usuario.getCidade().equalsIgnoreCase("Santa Maria")){

        }
        if(usuario.getUf().length() != 2){
            Toast.makeText(Cadastrar_Usuario.this, "UF diferente de 2 dígitos", Toast.LENGTH_LONG).show();
            return;
        }
        usuario.setCpf(usuario.getCpf().replace(".", "").replace("-", "").replace(" ", "").trim());
        if(!isValidCPF(usuario.getCpf())){
            Toast.makeText(Cadastrar_Usuario.this, "CPF inválido", Toast.LENGTH_LONG).show();
            return;
        }


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
                    Toast.makeText(Cadastrar_Usuario.this, "Usuário Cadastrado", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    intent.putExtra("EMAIL", usuario.getEmail());
                    intent.putExtra("SENHA", usuario.getSenha());
                    setResult(RESULT_OK, intent);
                    onBackPressed();
                } else {
                    Toast.makeText(Cadastrar_Usuario.this, "Erro ao cadastrar Usuário", Toast.LENGTH_LONG).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private boolean isValidCPF(String cpf) {
        if ((cpf==null) || (cpf.length()!=11)) return false;
        final int[] pesoCPF = {11, 10, 9, 8, 7, 6, 5, 4, 3, 2};
        Integer digito1 = calcularDigito(cpf.substring(0,9), pesoCPF);
        Integer digito2 = calcularDigito(cpf.substring(0,9) + digito1, pesoCPF);
        return cpf.equals(cpf.substring(0,9) + digito1.toString() + digito2.toString());
    }

    private int calcularDigito(String str, int[] peso) {
        int soma = 0;
        for (int indice=str.length()-1, digito; indice >= 0; indice-- ) {
            digito = Integer.parseInt(str.substring(indice,indice+1));
            soma += digito*peso[peso.length-str.length()+indice];
        }
        soma = 11 - soma % 11;
        return soma > 9 ? 0 : soma;
    }

}
