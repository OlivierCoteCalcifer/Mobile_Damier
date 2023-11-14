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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

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
    private boolean pionEnable = false;
    private SingletonJeuDeDames jeuDeDames = SingletonJeuDeDames.getInstance();
    private Integer indexBase;
    private List<Integer> mvtPossiblePionBase = new ArrayList<>();
    private TextView message_Board;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        message_Board = view.findViewById(R.id.damier_message);
        updateTextView();
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
                int finalNumeroManoury = numeroManoury;
                button.setOnClickListener(v -> {
                    onClickButton(finalNumeroManoury);
                });
                numeroManoury++;
            } else {
                button.setBackgroundColor(view.getContext().getColor(R.color.boardWhiteCase));
                button.setEnabled(false);
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
        Button buttonRetour = view.findViewById(R.id.damier_button_retour);
        buttonRetour.setOnClickListener(v -> {
            onClickRetourMouvement();
        });
    }

    private void onClickRetourMouvement() {
        Toast.makeText(getContext(), "Retour", Toast.LENGTH_SHORT).show();
    }

    private void setupBoardWithImageChecker(View view) {
        jeuDeDames.reset();
        for (int i = 1; i <= 50; i++) {
            String id = String.valueOf(i);
            int resId = view.getResources().getIdentifier(id, "id", view.getContext().getPackageName());
            ImageButton button = view.findViewById(resId);

            button.setBackgroundColor(view.getContext().getColor(R.color.boardBlackCase));

            // Faire un reset et passer par le jeu de dames avec Representation pour placer pion.
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
     */
    private void onClickButton(int index) {
        List<Integer> mvtPossible = new ArrayList<>();
        if (pionEnable && index != indexBase) {
            if (mvtPossiblePionBase.contains(index)) {
                handleMouvementJoueur(index, mvtPossiblePionBase);
            }
        }
        mvtPossible = jeuDeDames.mouvementsPossibles(index);
        char rep = verificationPionNull(index);
        resetUI();
        switch (rep) {
            case 'd', 'p' -> {
                if (jeuDeDames.getEstTourBlanc()) {
                    handleMouvementJoueur(index, mvtPossible);
                }
            }
            case 'D', 'P' -> {
                if (!jeuDeDames.getEstTourBlanc()) {
                    handleMouvementJoueur(index, mvtPossible);
                }
            }
            default -> {
                if (!pionEnable) {
                    Toast.makeText(getContext(), "Aucun pion sélectionné", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void handleMouvementJoueur(int index, List<Integer> mvtPossible) {
        if (mvtPossiblePionBase.isEmpty()) {
            pionEnable = true;
            ajoutUIOnClick(index, mvtPossible);
            indexBase = index;
            mvtPossiblePionBase = mvtPossible;
        } else if (pionEnable && mvtPossiblePionBase.contains(index)) {
            jeuDeDames.bouger(indexBase, index);
            mvtPossiblePionBase.clear();
            pionEnable = false;
            indexBase = null;
            resetUI();
        } else {
            indexBase = null;
            pionEnable = false;
            mvtPossiblePionBase.clear();
        }
    }




    /**
     * Ajoute des marqueurs visuels sur le pion sélectionné et les mouvements possible.
     *
     * @param index       Index du pion selectionné.
     * @param mvtPossible Liste de mouvements possibles.
     */
    private void ajoutUIOnClick(int index, List<Integer> mvtPossible) {
        String btnid = String.valueOf(index);
        int ressId = getResources().getIdentifier(btnid, "id", requireContext().getPackageName());
        ImageButton moveButton = getView().findViewById(ressId);
        moveButton.setBackgroundColor(moveButton.getContext().getColor(R.color.black));
        for (Integer element : mvtPossible) {
            String id = String.valueOf(element);
            int resId = getResources().getIdentifier(id, "id", requireContext().getPackageName());
            ImageButton mvtPossibleBoutons = getView().findViewById(resId);
            mvtPossibleBoutons.setImageResource(R.drawable.pion_possible);
        }
    }

    private void resetUI() {
        updateTextView();
        ImageButton moveButton = null;
        for (int i = 1; i < 51; i++) {
            char rep = verificationPionNull(i);
            String btnid = String.valueOf(i);
            int ressId = getResources().getIdentifier(
                    btnid, "id", requireContext().getPackageName());
            moveButton = getView().findViewById(ressId);
            moveButton.setImageResource(0);
            switch (rep) {
                case 'd' -> {
                    moveButton.setImageResource(R.drawable.dame_blanche);
                    moveButton.setBackgroundColor(
                            moveButton.getContext().getColor(R.color.boardBlackCase));
                }
                case 'D' -> {
                    moveButton.setImageResource(R.drawable.dame_noir);
                    moveButton.setBackgroundColor(
                            moveButton.getContext().getColor(R.color.boardBlackCase));
                }
                case 'P' -> {
                    moveButton.setImageResource(R.drawable.pion_noir);
                    moveButton.setBackgroundColor(
                            moveButton.getContext().getColor(R.color.boardBlackCase));
                }
                case 'p' -> {
                    moveButton.setImageResource(R.drawable.pion_blanc);
                    moveButton.setBackgroundColor(
                            moveButton.getContext().getColor(R.color.boardBlackCase));
                }
                default -> {
                    moveButton.setImageResource(0);
                    moveButton.setBackgroundColor(
                            moveButton.getContext().getColor(R.color.boardBlackCase));
                }
            }
        }
    }
    private void updateTextView() {
        String msg;
        if (jeuDeDames.getEstTourBlanc()) {
            msg = "Au tour de " + nomJoueur1;
        } else {
            msg = "Au tour de " + nomJoueur2;
        }
        message_Board.setText(msg);
    }
    private char verificationPionNull(int index) {
        try {
            Pion pion = jeuDeDames.getDamier().getPion(index);
            if (pion != null) {
                return pion.getRepresentation();
            }
        } catch (IllegalFormatException exMsg) {
            Toast.makeText(getContext(), "Bug representation", Toast.LENGTH_SHORT).show();
        }
        return ' ';
    }
}