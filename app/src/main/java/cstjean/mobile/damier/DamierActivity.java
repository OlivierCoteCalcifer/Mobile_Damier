package cstjean.mobile.damier;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

/**
 * Activity pour le damier.
 */
public class DamierActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_damier);

        String nomJoueur1 = getIntent().getStringExtra("nomJoueur1");
        String nomJoueur2 = getIntent().getStringExtra("nomJoueur2");

        // Create new fragment instance
        Fragment damierFragment = new DamierFragment();

        // Create a bundle to hold the arguments
        Bundle args = new Bundle();
        args.putString("Joueur1", nomJoueur1);
        args.putString("Joueur2", nomJoueur2);

        // Set the arguments on the fragment
        damierFragment.setArguments(args);

        // Now replace/add the fragment in your activity
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, damierFragment)
                .commit();

    }
}