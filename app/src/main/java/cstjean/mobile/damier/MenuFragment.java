package cstjean.mobile.damier;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;

/**
 * Cette methode est le fragment qui contient le menu pour la
 * saisie des noms des deux joueurs.
 */
public class MenuFragment extends Fragment {
    int compteurBoutonMenu = 0;
    String nomJoueur1 = null;
    String nomJoueur2 = null;
    TextInputEditText menuInput = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        // On va chercher tous les elements du menu.
        LinearLayout menuLayout = view.findViewById(R.id.menu_LinearLayout);
        Button menuButton = view.findViewById(R.id.menu_Bouton);
        TextView menuText = view.findViewById(R.id.menu_TextView);

        // Listener pour le bouton.
        menuButton.setOnClickListener(v -> {
            onButtonClickMenu(menuLayout, menuButton, menuText);
        });
        return view;
    }

    private void onButtonClickMenu(LinearLayout menuLayout, Button menuButton, TextView menuText) {
        switch (compteurBoutonMenu) {
            case 0 -> {
                menuText.setText("Entrez le nom du joueur 1");
                menuInput.setText("Entrez votre nom ici");
                menuLayout.addView(menuInput, 1);
                menuButton.setText("Valider joueur 1");
                compteurBoutonMenu++;
            }
            case 1 -> {
                nomJoueur1 = menuInput.getText().toString();
                menuText.setText("Entrez le nom du joueur 2");
                menuInput.setText("Entrez votre nom ici");
                menuButton.setText("Valider joueur 2");
                compteurBoutonMenu++;
            }
            default -> {
                nomJoueur2 = menuInput.getText().toString();
            }
        }
    }

}