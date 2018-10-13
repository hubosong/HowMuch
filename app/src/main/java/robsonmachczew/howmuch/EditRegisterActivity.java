package robsonmachczew.howmuch;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EditRegisterActivity extends NavActivity {

    private Button btnChoose;
    private ImageView imgUpload;
    private static final int SELECT_PICTURE = 1;
    private Uri selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //called by nav
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_edit_register, contentFrameLayout);
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImage = data.getData();
            imgUpload.setImageURI(selectedImage);
        }

    }

    public void btnEditRegisterUser(View view) {
        Toast.makeText(this, R.string.toast_successfull_change, Toast.LENGTH_SHORT).show();
    }

    //onBack
    @Override
    public void onBackPressed() {
        Intent off = new Intent(EditRegisterActivity.this, OffActivity.class);
        startActivity(off);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
