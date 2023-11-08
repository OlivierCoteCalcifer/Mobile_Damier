package cstjean.mobile.damier.classe;

/**
 * Classe principale du pion. On détermine la couleur ainsi que sa position.
 */
public class Pion {
    /**
     * Couleur du pion, le blanc est le choix par défaut.
     */
    private final Couleur couleurPion;

    /**
     * Constructeur de base qui va instancier un pion blanc.
     */
    public Pion() {
        this.couleurPion = Couleur.Blanc;
    }

    /**
     * Constructeur de base qui va instancier un pion.
     *
     * @param couleurPion Cet argument est la couleur des pions du joueur.
     */
    public Pion(Couleur couleurPion) {
        this.couleurPion = couleurPion;
    }

    public Couleur getCouleurPion() {
        return couleurPion;
    }

    /**
     * Ce getter retourne un char qui correspond à la couleur du pion.
     *
     * @return La lettre qui represente le pion
     */
    public char getRepresentation() {
        if (getCouleurPion() == Couleur.Noir) {
            return 'P';
        } else {
            return 'p';
        }
    }

    /**
     * Cette enum est pour determiner la couleur du pion soit blanc ou noir.
     */
    public enum Couleur {
        /**
         * Enum pour le blanc.
         */
        Blanc,
        /**
         * Enum pour le noir.
         */
        Noir
    }
}