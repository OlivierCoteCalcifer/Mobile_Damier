package cstjean.mobile.damier;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

import cstjean.mobile.damier.classe.AffichageDamier;
import cstjean.mobile.damier.classe.Damier;
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
    /**
     * Boolean pour lorsqu'un bouton est appuyé.
     */
    private boolean pionEnable = false;
    /**
     * Instance du Jeu de dames.
     */
    private SingletonJeuDeDames jeuDeDames = SingletonJeuDeDames.getInstance();
    /**
     * Lorsqu'on appuie sur un bouton on prend l'index pour le mettre en memoire pour faciliter
     * l'appel de prisePion.
     */
    private Integer indexBase;
    /**
     * Lorsqu'on appuie sur un bouton on mets en memoire les mouvements possible que lorsqu'on
     * appuie sur la destination on ne perd pas la liste de l'indexBase.
     */
    private List<Integer> mvtPossiblePionBase = new ArrayList<>();
    /**
     * TextView pour le message des tours des joueurs.
     */
    private TextView message_Board;
    private Damier damier;
    private boolean estPartieCommence = false;

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
        } else {
            nomJoueur1 = "Joueur1";
            nomJoueur2 = "Joueur2";
        }
        adjustLayoutForOrientation(view);
        setupBoard(view);
        setupBoardWithImageChecker(view);
        return view;
    }

    private void adjustLayoutForOrientation(View view) {
        int orientation = getResources().getConfiguration().orientation;
        LinearLayout layoutPrincipale = view.findViewById(R.id.layout_principale);

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutPrincipale.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            layoutPrincipale.setOrientation(LinearLayout.VERTICAL);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resetUi();
        if (savedInstanceState != null) {
            int orientation = getResources().getConfiguration().orientation;
            LinearLayout layoutPrincipale = view.findViewById(R.id.layout_principale);
            message_Board.setText(savedInstanceState.getString("TextViewContent"));

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                layoutPrincipale.setOrientation(LinearLayout.HORIZONTAL);
            } else {
                layoutPrincipale.setOrientation(LinearLayout.VERTICAL);
            }
            estPartieCommence = savedInstanceState.getBoolean("EstPartieCommence");
            if (estPartieCommence) {
                resetUi();
            } else {
                for (int i = 1; i <= 50; i++) {
                    String btnId = String.valueOf(i);
                    ImageButton button = view.findViewById(getResources().getIdentifier(btnId,
                            "id", getContext().getPackageName()));
                    int drawableId = savedInstanceState.getInt("ImageButtonDrawable_" + i);
                    button.setImageResource(drawableId);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("TextViewContent", message_Board.getText().toString());
        outState.putBoolean("EstPartieCommence", estPartieCommence);

        for (int i = 1; i <= 50; i++) { // Assuming you have 50 buttons
            String btnId = String.valueOf(i);
            ImageButton button = getView().findViewById(getResources().getIdentifier(btnId,
                    "id", requireContext().getPackageName()));

            // Save the resource ID of the drawable
            outState.putInt("ImageButtonDrawable_" + i, (Integer) button.getTag());
        }
    }

    private void setupBoard(View view) {
        message_Board = view.findViewById(R.id.damier_message);
        updateTextView();
        GridLayout damierBoard = view.findViewById(R.id.board_damier);

        int orientation = view.getContext().getResources().getConfiguration().orientation;
        boolean isPortrait = orientation == Configuration.ORIENTATION_PORTRAIT;

        int gridSize = 10;
        damierBoard.setColumnCount(gridSize);
        damierBoard.setRowCount(gridSize);

        DisplayMetrics displayMetrics = view.getContext().getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels - 75;

        // Calcule la grosseur du bouton selon l'orientation de l'ecran.
        int buttonSize = isPortrait ? screenWidth / gridSize : screenHeight / gridSize;
        int numeroManoury = 1;
        for (int i = 0; i < gridSize * gridSize; i++) {
            ImageButton button = new ImageButton(view.getContext());
            int row = i / gridSize;
            int col = i % gridSize;
            if ((row + col) % 2 != 0) {
                button.setId(numeroManoury);
                button.setTag(numeroManoury);
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
        // C'est juste pour cette setup le board pour mes tests avec les prises et tout. Feel free to change it.
        jeuDeDames.vider();
        jeuDeDames.reset();
        Log.d("DamierFragment", AffichageDamier.getAffichage(jeuDeDames.getDamier()));
        resetUi();
        Toast.makeText(getContext(), "Board Test reset", Toast.LENGTH_SHORT).show();
    }

    private void setupBoardWithImageChecker(View view) {
        if (!jeuDeDames.getEstPartieCommence()) {
            jeuDeDames.reset();
        }
        for (int i = 1; i <= 50; i++) {
            String id = String.valueOf(i);
            int resId = view.getResources().getIdentifier(id, "id",
                    view.getContext().getPackageName());
            ImageButton button = view.findViewById(resId);

            button.setBackgroundColor(view.getContext().getColor(R.color.boardBlackCase));

            char rep = verificationPionNull(i);
            switch (rep) {
                case 'd' -> {
                    button.setImageResource(R.drawable.dame_blanche);
                }
                case 'D' -> {
                    button.setImageResource(R.drawable.dame_noir);
                }
                case 'P' -> {
                    button.setImageResource(R.drawable.pion_noir);
                }
                case 'p' -> {
                    button.setImageResource(R.drawable.pion_blanc);
                }
                default -> {
                    button.setImageResource(0);
                }
            }
        }
    }

    /**
     * Cette methode va appeler les méthodes nécessaire pour le fonctionnnement du jeu.
     */
    private void onClickButton(int index) {
        resetUi();
        List<Integer> mvtPossible = new ArrayList<>();
        if (pionEnable && index != indexBase) {
            if (mvtPossiblePionBase != null && mvtPossiblePionBase.contains(index)) {
                handleMouvementJoueur(index, mvtPossiblePionBase);
                return;
            }
        }
        mvtPossible = jeuDeDames.mouvementsPossibles(index, false);
        if (mvtPossible == null) {
            mvtPossible = new ArrayList<>();
        }
        char rep = verificationPionNull(index);
        resetUi();
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
            }
        }
    }

    private void handleMouvementJoueur(int index, List<Integer> mvtPossible) {
        if (mvtPossiblePionBase.isEmpty()) {
            pionEnable = true;
            ajoutUIOnClick(index, mvtPossible);
            indexBase = index;
            mvtPossiblePionBase = new ArrayList<>(mvtPossible);
        } else if (pionEnable && mvtPossiblePionBase.contains(index)) {

            Log.d("DamierFragment", "Index de départ: " + indexBase);
            Log.d("DamierFragment", "Index d'arrivée: " + index);
            jeuDeDames.bouger(indexBase, index);
            mvtPossiblePionBase.clear();
            mvtPossible.clear();
            pionEnable = false;
            indexBase = null;
            Log.d("DamierFragment", "Index de départ: " + indexBase);
            Log.d("DamierFragment", "Index d'arrivée: " + index);
            resetUi();
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

    private void resetUi() {
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
                }
                case 'D' -> {
                    moveButton.setImageResource(R.drawable.dame_noir);
                }
                case 'P' -> {
                    moveButton.setImageResource(R.drawable.pion_noir);
                }
                case 'p' -> {
                    moveButton.setImageResource(R.drawable.pion_blanc);
                }
                default -> {
                    moveButton.setImageResource(0);
                }
            }
            moveButton.setBackgroundColor(
                    moveButton.getContext().getColor(R.color.boardBlackCase));
        }
        Log.d("DamierFragment", "" +
                "");
        Log.d("DamierFragment", "" +
                "");
        Log.d("DamierFragment", "" +
                "");
        Log.d("DamierFragment", AffichageDamier.getAffichage(jeuDeDames.getDamier()));
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