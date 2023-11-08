package cstjean.mobile.damier.classe;

/**
 * Cette classe de dames prend un pion et le transforme en dames.
 */
public class Dames extends Pion {
    /**
     * Variable de la couleur de la dame.
     */
    private final Couleur couleurDames;

    /**
     * Constructeur de dames.
     *
     * @param couleurPion Couleur du pion pour assigner à couleurDames.
     */
    public Dames(Couleur couleurPion) {
        super(couleurPion);
        this.couleurDames = couleurPion;
    }

    public Couleur getCouleurDames() {
        return couleurDames;
    }

    @Override
    public char getRepresentation() {
        if (getCouleurDames() == Couleur.Noir) {
            return 'D';
        } else {
            return 'd';
        }
    }
}
