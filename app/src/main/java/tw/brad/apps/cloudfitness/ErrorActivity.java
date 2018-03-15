package tw.brad.apps.cloudfitness;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ErrorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide(); //隱藏標題
        setContentView(R.layout.activity_error);
    }
}
