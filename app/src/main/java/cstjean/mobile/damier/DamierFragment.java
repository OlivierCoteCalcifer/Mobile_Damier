package cstjean.mobile.damier;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cstjean.mobile.damier.classe.Pion;
import cstjean.mobile.damier.classe.SingletonJeuDeDames;

/**
 * Cette classe est le fragment qui va contenir la logique du damier
 * ainsi que l'affichage nécessaire pour le jeu.
 */
public class DamierFragment extends Fragment {
    /**
     * Input du premier joueur du menu.
     */
    private String nomJoueur1;
    /**
     * Input du deuxième joueur du menu.
     */
    private String nomJoueur2;
    private SingletonJeuDeDames jeuDeDames = SingletonJeuDeDames.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_damier, container, false);
        Bundle args = getArguments();
        if (args != null) {
            nomJoueur1 = args.getString("Joueur1");
            nomJoueur2 = args.getString("Joueur2");
        }
        setupBoard(view);
        setupBoardWithImageChecker(view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            // Recreer le board selon les id de boutons
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Sauver le board et le message pour le tour du joueur.
    }

    private void setupBoard(View view) {
        TextView textDamier = view.findViewById(R.id.damier_message);
        textDamier.setText("Au tour de " + nomJoueur1);
        GridLayout damierBoard = view.findViewById(R.id.board_damier);
        damierBoard.setColumnCount(10);
        damierBoard.setRowCount(10);
        DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int buttonSize = screenWidth / 10; // Divide screen width by number of columns


        int numeroManoury = 1;
        for (int i = 0; i < 100; i++) {
            ImageButton button = new ImageButton(view.getContext());
            int row = i / 10;
            int col = i % 10;

            if ((row + col) % 2 != 0) {
                button.setId(numeroManoury);
                button.setBackgroundColor(view.getContext().getColor(R.color.boardBlackCase));
                numeroManoury++;
            } else {
                button.setBackgroundColor(view.getContext().getColor(R.color.boardWhiteCase));
                button.setEnabled(false);  // Consider if you want to disable the button
            }

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(row);
            params.columnSpec = GridLayout.spec(col);
            params.width = buttonSize;
            params.height = buttonSize;

            button.setLayoutParams(params);
            button.setScaleType(ImageView.ScaleType.CENTER_CROP);
            damierBoard.addView(button);
        }
    }

    private void setupBoardWithImageChecker(View view) {
        for (int i = 1; i <= 50; i++) {
            String id = String.valueOf(i);
            int resId = view.getResources().getIdentifier(id, "id", view.getContext().getPackageName());
            ImageButton button = view.findViewById(resId);
            if (i <= 20) {
                Pion pionNoir = new Pion(Pion.Couleur.Noir);
                jeuDeDames.getDamier().ajouterPion(i, pionNoir);
                button.setImageResource(R.drawable.pion_noir);
            }
            if (i >= 31) {
                Pion pionBlanc = new Pion(Pion.Couleur.Blanc);
                jeuDeDames.getDamier().ajouterPion(i, pionBlanc);
                button.setImageResource(R.drawable.pion_blanc);
            }
        }
    }

    /**
     * Cette methode va appeler les methodes nécessaire pour le fonctionnnement du jeu.
     *
     * @param view On prend la vue du damier.
     */
    private void onClickButton(View view) {

    }
}