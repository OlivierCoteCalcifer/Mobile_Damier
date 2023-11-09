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
        menuInput = new TextInputEditText(getContext());
        int widthInDp = 200; // You can adjust this value as needed
        float scale = getResources().getDisplayMetrics().density;
        int widthInPx = (int) (widthInDp * scale + 0.5f);

        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                widthInPx,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        menuInput.setLayoutParams(layoutParams);

        // Listener pour le bouton.
        menuButton.setOnClickListener(v -> {
            onButtonClickMenu(menuLayout, menuButton, menuText);
        });
        return view;
    }

    private void onButtonClickMenu(LinearLayout menuLayout, Button menuButton, TextView menuText) {
        switch (compteurBoutonMenu) {
            case 0:
                menuText.setText("Entrez le nom du joueur 1");
                menuInput.setHint("Entrez votre nom ici");
                if(menuLayout.indexOfChild(menuInput) == -1) {
                    menuLayout.addView(menuInput, 1);
                }
                menuButton.setText("Valider joueur 1");
                compteurBoutonMenu++;
                break;
            case 1:
                nomJoueur1 = menuInput.getText().toString();
                menuText.setText("Entrez le nom du joueur 2");
                menuInput.setText(""); // Clear the text instead of setting hint
                menuInput.setHint("Entrez votre nom ici");
                menuButton.setText("Valider joueur 2");
                compteurBoutonMenu++;
                break;
            default:
                nomJoueur2 = menuInput.getText().toString();
                // Perform any action after getting nomJoueur2, like navigating to another Fragment or Activity
                break;
        }
    }

}