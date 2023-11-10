package cstjean.mobile.damier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Cette methode est le fragment qui contient le menu pour la
 * saisie des noms des deux joueurs.
 */
public class MenuFragment extends Fragment {

    /**
     * Ce compteur est pour faciliter la transformation de la vue du menu.
     */
    private int compteurBoutonMenu = 0;

    /**
     * Le nom du premier joueur.
     */
    private String nomJoueur1 = null;

    /**
     * Le nom du deuxième joueur.
     */
    private String nomJoueur2 = null;

    /**
     * TextView pour le menu. Affiche les messages nécessaires au lancement de l'application.
     */
    private TextInputEditText menuInput = null;

    /**
     * Callback pour se promener entre les activities.
     */
    private Callbacks callbacks = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        int indexCourant = callbacks.getIndexCourant();
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
        menuInput.setHint("Entrez votre nom ici");
        switch (compteurBoutonMenu) {
            case 0 -> {
                menuText.setText(R.string.msg_validation_1);
                if (menuLayout.indexOfChild(menuInput) == -1) {
                    menuLayout.addView(menuInput, 1);
                }
                menuButton.setText(R.string.msg_btn_validation_1);
                compteurBoutonMenu++;
            }
            case 1 -> {
                if (Objects.requireNonNull(menuInput.getText()).toString().isEmpty() &&
                        nomJoueur1 == null) {
                    Toast.makeText(getContext(), "Entrez un nom de joueur.",
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                nomJoueur1 = menuInput.getText().toString();
                menuText.setText(R.string.msg_validation_2);
                menuInput.getText().clear();
                menuButton.setText(R.string.msg_btn_validation_2);
                compteurBoutonMenu++;
            }
            default -> {
                nomJoueur2 = menuInput.getText().toString();
                if (nomJoueur2.isEmpty()) {
                    Toast.makeText(getContext(), "Entrez un nom de joueur.",
                            Toast.LENGTH_SHORT).show();
                    break;
                }
                Intent intent = new Intent(getActivity(), DamierActivity.class);
                startActivity(intent);
            }
        }
    }

    /**
     * Getter pour les noms des joueurs.
     *
     * @return Les noms des joueurs en format de liste.
     */
    public List<String> getNomDesJoueurs() {
        List<String> liste = new ArrayList<>();
        liste.add(nomJoueur1);
        liste.add(nomJoueur2);
        return liste;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        callbacks = (Callbacks) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        callbacks = null;
    }

    /**
     * Interface de callback pour faciliter le chargement des activity par l'indexCourant.
     */
    interface Callbacks {
        /**
         * Cette méthode abstraite est pour renvoyer l'indexCourant.
         *
         * @return int indexCourant.
         */
        int getIndexCourant();
    }
}