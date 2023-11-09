package cstjean.mobile.damier;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Cette classe est l'activity du menu qui va appeler le fragment.
 */
public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }
}