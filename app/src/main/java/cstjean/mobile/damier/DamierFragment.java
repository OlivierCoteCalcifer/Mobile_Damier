package cstjean.mobile.damier;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    private boolean pionEnable;
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
    }

    private void setupBoardWithImageChecker(View view) {
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
    private void onClickButton(int index, View view) {
        List<Integer> mvtPossible = jeuDeDames.mouvementsPossibles(index);

        resetUI();

        if (mvtPossible.isEmpty()) {
            Toast.makeText(getContext(), "Aucun mouvement possible Position" + index, Toast.LENGTH_SHORT).show();
        } else {
            ajoutUIOnClick(index, view, mvtPossible);
            Toast.makeText(getContext(), "Mouvement possible " + mvtPossible + index, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Ajoute des marqueurs visuels sur le pion sélectionné et les mouvements possible.
     *
     * @param index       Index du pion selectionné.
     * @param view        Vue.
     * @param mvtPossible Liste de mouvements possibles.
     */
    private void ajoutUIOnClick(int index, View view, List<Integer> mvtPossible) {
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
        ImageButton moveButton = null;
        for (int i = 1; i < 51; i++) {
            Object rep = jeuDeDames.getDamier().getPion(i).getRepresentation();
            String btnid = String.valueOf(i);
            int ressId = getResources().getIdentifier(btnid, "id", requireContext().getPackageName());
            moveButton = getView().findViewById(ressId);
            moveButton.setImageResource(0);
            switch (rep.toString()) {
                case "d" -> moveButton.setImageResource(R.drawable.dame_blanche);
                case "D" -> moveButton.setImageResource(R.drawable.dame_noir);
                case "P" -> moveButton.setImageResource(R.drawable.pion_noir);
                case "p" -> moveButton.setImageResource(R.drawable.pion_blanc);
                default -> moveButton.setImageResource(0);
            }
        }
    }
}