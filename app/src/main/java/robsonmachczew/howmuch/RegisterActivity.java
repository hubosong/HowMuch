package robsonmachczew.howmuch;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText inputName;
    private TextInputLayout inputTextName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //toolbar
        Toolbar  toolbar = (Toolbar) findViewById(R.id.bar);
        toolbar.setTitle(R.string.bar_register);
        setSupportActionBar(toolbar);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.gradient_bg));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //basic config
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        getWindow().setStatusBarColor(this.getResources().getColor(R.color.toolbar_status));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


    }

    //usado para executar o voltar da action
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(login);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent login = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(login);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }

    public void btnRegisterUser(View view) {
        Toast.makeText(this, R.string.toast_successfull_register, Toast.LENGTH_SHORT).show();
    }

}
