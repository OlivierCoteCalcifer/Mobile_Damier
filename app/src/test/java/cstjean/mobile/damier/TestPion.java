package cstjean.mobile.damier;

import junit.framework.TestCase;

import cstjean.mobile.damier.classe.Pion;

/**
 * Cette classe test la creation des pions des joueurs.
 */

public class TestPion extends TestCase {
    /**
     * Cette fonction creer des variables de pions pour ensuite verifier leurs couleurs.
     */
    public void testCreer() {
        Pion pion1 = new Pion(Pion.Couleur.Blanc);
        assertEquals(Pion.Couleur.Blanc, pion1.getCouleurPion());

        Pion pion2 = new Pion(Pion.Couleur.Noir);
        assertEquals(Pion.Couleur.Noir, pion2.getCouleurPion());

        Pion pion3 = new Pion();
        assertEquals(Pion.Couleur.Blanc, pion3.getCouleurPion());
    }

}
