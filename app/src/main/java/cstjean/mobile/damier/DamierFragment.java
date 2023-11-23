package cstjean.mobile.damier;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import cstjean.mobile.damier.classe.Dames;
import cstjean.mobile.damier.classe.Pion;
import cstjean.mobile.damier.classe.SingletonJeuDeDames;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;

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
    private final SingletonJeuDeDames jeuDeDames = SingletonJeuDeDames.getInstance();
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
    private TextView messageBoard;
    /**
     * Cette variable est pour l'initialisation du board dans le onCreateView et
     * setupBoardWithImageChecker.
     */
    private boolean estPartieCommence = false;
    /**
     * Cette variable peutRetour est pour le retour sur un mouvement. Elle existe pour éviter
     * d'appuyer à répétitions.
     */
    private boolean peutRetour = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_damier, container, false);
        Bundle args = getArguments();

        // Récupère le nom des joueurs s'ils ne sont pas null.
        if (args != null) {
            nomJoueur1 = args.getString("Joueur1");
            nomJoueur2 = args.getString("Joueur2");
        } else {
            nomJoueur1 = "Joueur1";
            nomJoueur2 = "Joueur2";
        }

        // Création du damier.
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        resetUi();
        if (savedInstanceState != null) {
            int orientation = getResources().getConfiguration().orientation;
            LinearLayout layoutPrincipale = view.findViewById(R.id.layout_principale);
            updateTextHistorique(view);

            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                layoutPrincipale.setOrientation(LinearLayout.HORIZONTAL);
            } else {
                layoutPrincipale.setOrientation(LinearLayout.VERTICAL);
            }
            // Récupère les bools
            peutRetour = savedInstanceState.getBoolean("PeutRetour");
            estPartieCommence = savedInstanceState.getBoolean("EstPartieCommence");
            if (estPartieCommence) {
                resetUi();
            } else {
                for (int i = 1; i < 51; i++) {
                    String btnId = String.valueOf(i);
                    ImageButton button = view.findViewById(getResources().getIdentifier(btnId,
                            "id", requireContext().getPackageName()));
                    int drawableId = savedInstanceState.getInt("ImageButtonDrawable_" + i);
                    button.setImageResource(drawableId);
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Sauvegarde les variables lors de la rotation de l'appareil.
        outState.putString("TextViewContent", messageBoard.getText().toString());
        outState.putBoolean("EstPartieCommence", estPartieCommence);
        outState.putBoolean("PeutRetour", peutRetour);

        outState.putString("HistoriqueDamier", jeuDeDames.getHistoriqueDeplacementDamier().toString());

        for (int i = 1; i <= 50; i++) { // Assuming you have 50 buttons
            String btnId = String.valueOf(i);
            ImageButton button = requireView().findViewById(
                    getResources().getIdentifier(btnId,
                            "id", requireContext().getPackageName()));
        }
    }

    private void setupBoard(View view) {
        messageBoard = view.findViewById(R.id.damier_message);
        updateTextView();
        updateTextHistorique(view);
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
                    onClickButton(finalNumeroManoury, view);
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
            onClickRetourMouvement(view);
        });
        Button buttonReset = view.findViewById(R.id.damier_button_reset);
        buttonReset.setOnClickListener(v -> {
            onClickResetGame(view);
        });
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
            button.setTag("button" + i);
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
    private void onClickButton(int index, View view) {
        if (!jeuDeDames.estPartieTerminee()) {
            resetUi();
            List<Integer> mvtPossible;
            if (pionEnable && index != indexBase) {
                if (mvtPossiblePionBase != null && mvtPossiblePionBase.contains(index)) {
                    handleMouvementJoueur(index, mvtPossiblePionBase);
                    updateTextHistorique(view);
                    return;
                }
            }
            mvtPossible = jeuDeDames.mouvementsPossibles(index, false);
            updateTextHistorique(view);
            if (mvtPossible == null) {
                mvtPossible = new ArrayList<>();
            }
            char rep = verificationPionNull(index);
            resetUi();
            peutRetour = true;
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
                    return;
                }
            }
            updateTextHistorique(view);
        }
    }

    private void updateTextHistorique(View view) {
        TextView textHistorique = view.findViewById(R.id.damier_historique);

        if (jeuDeDames.getHistoriqueDeplacementDamier().size() > 0) {
            int indexHistorique = jeuDeDames.getHistoriqueDeplacementDamier().size() - 1;
            textHistorique.setText(jeuDeDames.getHistoriqueDeplacementDamier()
                    .get(indexHistorique));
        } else {
            textHistorique.setText("");
        }

    }

    private void handleMouvementJoueur(int index, List<Integer> mvtPossible) {
        if (mvtPossiblePionBase.isEmpty()) {
            pionEnable = true;
            ajoutUiOnClick(index, mvtPossible);
            indexBase = index;
            mvtPossiblePionBase = new ArrayList<>(mvtPossible);
        } else if (pionEnable && mvtPossiblePionBase.contains(index) && indexBase != null) {
            jeuDeDames.bouger(indexBase, index);
            mvtPossiblePionBase.clear();
            mvtPossible.clear();
            pionEnable = false;
            indexBase = null;
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
    private void ajoutUiOnClick(int index, List<Integer> mvtPossible) {
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
        if (jeuDeDames.estPartieTerminee()) {
            affichageMessageGagnantEtPerdant();
            jeuDeDames.vider();
            jeuDeDames.reset();
        }
    }

    private void affichageMessageGagnantEtPerdant() {
        if (jeuDeDames.getEstTourBlanc()) {
            updateTextView();
            Toast.makeText(getActivity(), nomJoueur1 + ", vous avez perdu...", Toast.LENGTH_SHORT).show();
        } else {
            updateTextView();
            Toast.makeText(getActivity(), nomJoueur2 + ", vous avez perdu...", Toast.LENGTH_SHORT).show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Context context = getContext();

                if (context != null) {
                    Intent intent = new Intent(getActivity(), MenuActivity.class);
                    context.startActivity(intent);

                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                }
            }
        }, 5000); // 2 secondes de retard
    }

    private void updateTextView() {
        String msg;
        if (!jeuDeDames.estPartieTerminee()) {
            if (jeuDeDames.getEstTourBlanc()) {
                msg = "Au tour de \n" + nomJoueur1;
            } else {
                msg = "Au tour de \n" + nomJoueur2;
            }
        } else {
            if (!jeuDeDames.getEstTourBlanc()) {
                msg = "Félicitations " + nomJoueur1 + "!!!";
            } else {
                msg = "Félicitations " + nomJoueur2 + "!!!";
            }
        }
        messageBoard.setText(msg);
    }

    private void onClickRetourMouvement(View view) {
        // Ligne 257 - 273 --> Endroit ou l'on reset le peutRetour lors d'un mouvement.
        if (peutRetour && jeuDeDames.getHistoriqueDeplacementDamier().size() > 0) {
            jeuDeDames.retourPartie();
            updateTextHistorique(view);
        } else {
            Toast.makeText(getContext(), "Retour impossible", Toast.LENGTH_SHORT).show();
        }
        resetUi();
    }

    private void onClickResetGame(View view) {
        jeuDeDames.vider();
        jeuDeDames.reset();
        TextView textHistorique = view.findViewById(R.id.damier_historique);
        textHistorique.setText("");
        resetUi();
        Toast.makeText(getContext(), "Partie recommencée", Toast.LENGTH_SHORT).show();
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