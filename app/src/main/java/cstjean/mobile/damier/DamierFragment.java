package cstjean.mobile.damier;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import androidx.fragment.app.Fragment;

/**
 * Cette classe est le fragment qui va contenir la logique du damier
 * ainsi que l'affichage nécessaire pour le jeu.
 */
public class DamierFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_damier, container, false);
        setupBoard(view);
        return view;
    }

    private void setupBoard(View view) {
        GridLayout damierBoard = view.findViewById(R.id.board_damier);
        int numeroManoury = 1;
        for (int i = 1; i <= 100; i++) {
            Button button = new Button(view.getContext());
            if (i % 2 == 0) {
                button.setId(numeroManoury);
                button.setBackgroundColor(Color.BLACK);
                numeroManoury++;
            } else {
                button.setBackgroundColor(Color.WHITE);
            }
            damierBoard.addView(button);
        }
    }
}