package robsonmachczew.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import entidade.Usuario;
import entidade.Utils;

public class Editar_Cadastros extends Nav {

    private Button btnChoose;
    private ImageView imgUpload;
    private static final int SELECT_PICTURE = 1;
    private Uri selectedImage;

    private Usuario usuario;

    private EditText inputName;
    private EditText edtEmail;
    private EditText edtPass;
    private EditText edtCPF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //called by nav
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_editar_cadastros, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //basic config
        getSupportActionBar().setTitle(R.string.bar_edit_register);

        imgUpload = findViewById(R.id.imgUpload);
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

        usuario = Utils.loadFromSharedPreferences(this);
        if(usuario != null) {
            inputName.setText(usuario.getNome());
            edtEmail.setText(usuario.getEmail());
            edtPass.setText(usuario.getSenha());
            edtCPF.setText(usuario.getCpf());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImage = data.getData();
            imgUpload.setImageURI(selectedImage);
        }

    }

    public void btnEditRegisterUser(View view) {
        final Usuario usuario = new Usuario();

    }

}
