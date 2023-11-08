package cstjean.mobile.damier.classe;

import java.util.TreeMap;

/**
 * Cette classe represente le damier ainsi qu'une liste avec chaque
 * pion et leurs emplacements.
 */
public class Damier {
    /**
     * Cette liste represente les pions sur le damier et leur position.
     */
    private final TreeMap<Integer, Pion> listPions;

    /**
     * Ce constructeur de base ne prend aucun argument et renvoie une liste avec des valeurs nulles.
     */
    public Damier() {
        this.listPions = new TreeMap<>();
    }

    /**
     * Ce constructeur prend la liste predefinie et l'attribut au damier. Si la liste
     * n'a pas un format de 50 emplacements une exception sera soulevée.
     *
     * @param listPions Cet argument est la liste de pion sur le damier.
     */

    public Damier(TreeMap<Integer, Pion> listPions) {
        this.listPions = new TreeMap<>(listPions);
    }

    /**
     * Cette fonction rajoute un pion sur le damier tout dependant de la couleur.
     * Donc, pour un blanc, on trouve le dernier index d'un pion blanc et on l'ajoute à la liste.
     * Pour le noir, on
     *
     * @param pionAjout Cet argument est le pion avec sa couleur.
     * @param position  Cet argument est la position qu'on veut attribuer au pion.
     */

    public void ajouterPion(int position, Pion pionAjout) {
        if (this.listPions.get(position) == null) {
            listPions.put(position, pionAjout);
        }
    }

    /**
     * Cette methode retire un pion du damier par la position.
     *
     * @param position Position a retirer
     */
    public void retirerPion(int position) {
        if (this.listPions.get(position) != null) {
            listPions.put(position, null);
        }
    }

    /**
     * Cette methode verifie la position du pion ensuite le transforme en dames si les conditions sont remplis.
     *
     * @param position       La position du pion.
     * @param pion           Le pion.
     * @param positionDepart Position de depart.
     */
    public void ajouterDames(int position, Pion pion, int positionDepart) {
        listPions.remove(positionDepart);
        listPions.put(position, new Dames(pion.getCouleurPion()));
    }

    /**
     * Getter de la liste de pions.
     *
     * @return La map de pions.
     */
    public TreeMap<Integer, Pion> getListPion() {
        return new TreeMap<>(listPions);
    }

    /**
     * Cette fonction calcule le nombre de pions qui sont présentement sur le damier.
     *
     * @return compteur Renvoie le nombre de pions sur le damier
     */
    public int getNbPions() {
        int compteur = 0;
        for (int i = 1; i <= 50; i++) {
            if (listPions.get(i) == null) {
                continue;
            }
            compteur++;
        }
        return compteur;
    }

    /**
     * Cette methode initialise le jeu de dames en placant les jetons blanc et noir.
     */
    public void initialiser() {
        for (int i = 1; i <= 51; i++) {
            if (i <= 20) {
                Pion pionNoir = new Pion(Pion.Couleur.Noir);
                this.listPions.put(i, pionNoir);
            } else if (i >= 31) {
                Pion pionBlanc = new Pion(Pion.Couleur.Blanc);
                this.listPions.put(i, pionBlanc);
            }
        }
    }
}
