package cstjean.mobile.damier;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/**
 * Cette classe est l'activity du menu qui va appeler le fragment.
 */
public class MenuActivity extends AppCompatActivity implements MenuFragment.Callbacks {
    /**
     * String pour l'indexCourant.
     */
    private static final String EXTRA_INDEXCOURANT = "indexcourant";
    /**
     * Integer pour l'indexCourant.
     */
    private int indexCourant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        indexCourant = getIntent().getIntExtra(EXTRA_INDEXCOURANT, 0);
    }

    @Override
    public int getIndexCourant() {
        return indexCourant;
    }
}