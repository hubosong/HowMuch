package robsonmachczew.howmuch;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

public class EditRegisterActivity extends NavActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //called by nav
        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.content_frame);
        getLayoutInflater().inflate(R.layout.activity_edit_register, contentFrameLayout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        //basic config
        getSupportActionBar().setTitle(R.string.bar_edit_register);

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
