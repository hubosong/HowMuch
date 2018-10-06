package robsonmachczew.howmuch;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import entidade.Usuario;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPass;
    private TextView txtResult, txtWait;
    private ProgressBar progWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.bar);
        toolbar.setTitle("Login");
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_bg));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //basic config
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.toolbar_status));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        progWait = findViewById(R.id.progWait);
        txtWait = findViewById(R.id.txtWait);

        edtEmail = findViewById(R.id.edtEmail);
        edtPass = findViewById(R.id.edtPass);
        //when clicked feito or enter on keyboard call btnlogin method
        edtPass.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            //validate login
                            btnLogin(edtPass);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });


        TextView txtForgotPass = findViewById(R.id.txtForgot);
        txtForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Ops! Talk with administrator.", Toast.LENGTH_SHORT).show();
            }
        });


    }

    //logar
    @SuppressLint("StaticFieldLeak")
    public void btnLogin(View view) {

        //clear focus --> created edittext Vazio
        EditText edt = findViewById(R.id.edtVazio);
        edtPass.clearFocus();
        edt.requestFocus();


        //hidden keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);


        final String email = edtEmail.getText().toString();
        final String senha = edtPass.getText().toString();
        //mínimos de dígitos em cada campo
        if (email.length() < 3 || senha.length() < 6) {
            Toast t = Toast.makeText(this, "Enter with your login and pass!", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.CENTER, 0, 50);
            t.show();
            return;
        }
        new AsyncTask<String, Void, Usuario>() {
            @Override
            protected void onPreExecute() {
                progWait.setVisibility(View.VISIBLE);
                txtWait.setVisibility(View.VISIBLE);
                super.onPreExecute();
            }

            @Override
            protected Usuario doInBackground(String... params) {
                Usuario usuario = null;
                try {
                    String urlParameters = "email=" + email + "&senha=" + senha;
                    byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);

                    URL url = new URL("http://187.181.170.135:8080/Mercado/login");
                    HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();
                    urlCon.setRequestMethod("POST");
                    urlCon.setDoOutput(true);
                    urlCon.setDoInput(true);

                    DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
                    wr.write(postData);
                    wr.close();
                    wr.flush();

                    ObjectInputStream ois = new ObjectInputStream(urlCon.getInputStream());
                    usuario = (Usuario) ois.readObject();
                    ois.close();

                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }

                return usuario;
            }

            @Override
            protected void onPostExecute(Usuario usuario) {
                progWait.setVisibility(View.GONE);
                txtWait.setVisibility(View.GONE);
                if(usuario == null){
                    Toast.makeText(LoginActivity.this, "Email or Password invalidates!", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent content = new Intent(LoginActivity.this, OffActivity.class);
                content.putExtra("usuario", usuario);
                startActivity(content);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }.execute();
    }

    public void btnRegister(View view) {
        Intent register = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(register);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }


    //usado para executar o voltar da action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent main = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(main);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        Intent main = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(main);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

}
