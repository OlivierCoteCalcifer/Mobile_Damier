package cstjean.mobile.damier.classe;

import java.util.TreeMap;

/**
 * Cette methode affichage le damier en console et identifie les pions selon leurs couleurs.
 * P pour pion noir et p pour pion blanc.
 */
public class AffichageDamier {
    /**
     * Cette fonction prend en parametre un damier pour retourner un affichage.
     *
     * @param damier Doit avoir un damier pour l'affichage.
     * @return L'affichage en chaine de caracteres.
     */
    public static String getAffichage(Damier damier) {
        StringBuilder sb = new StringBuilder();
        TreeMap<Integer, Pion> mapDamier = damier.getListPion();
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 5; j++) {
                int cle = i * 5 + j + 1;
                Pion currentPion = mapDamier.get(cle);

                if (currentPion == null) {
                    sb.append("--");
                } else if (i % 2 == 0) {
                    sb.append("-").append(currentPion.getRepresentation());
                } else {
                    sb.append(currentPion.getRepresentation()).append("-");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}

