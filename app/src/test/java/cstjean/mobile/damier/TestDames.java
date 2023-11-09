package cstjean.mobile.damier;

import junit.framework.TestCase;

import cstjean.mobile.damier.classe.Dames;
import cstjean.mobile.damier.classe.Damier;
import cstjean.mobile.damier.classe.Pion;

/**
 * Cette classe de test execute pour valider la class de pour des dames.
 */
public class TestDames extends TestCase {
    /**
     * Cette variable est un pion noir.
     */
    private Pion pionTestNoir;
    /**
     * Cette variable est un pion blanc.
     */
    private Pion pionTestBlanc;

    /**
     * Cette methode instantie les variables nécessaires aux tests.
     */
    public void setUp() {
        pionTestNoir = new Pion(Pion.Couleur.Noir);
        pionTestBlanc = new Pion(Pion.Couleur.Blanc);
    }

    /**
     * Cette methode test la conversion des pions aux dames sur un damier.
     */
    public void testCreer() {
        Damier damier = new Damier();
        damier.initialiser();
        Dames dameTest = new Dames(pionTestNoir.getCouleurPion());
        assertEquals(Pion.Couleur.Noir, dameTest.getCouleurPion());
        assertEquals('D', dameTest.getRepresentation());
        Dames dameTest2 = new Dames(pionTestBlanc.getCouleurPion());
        assertEquals(Pion.Couleur.Blanc, dameTest2.getCouleurPion());
        assertEquals('d', dameTest2.getRepresentation());
        damier.ajouterDames(46, pionTestNoir, 41);
        assertEquals('D', damier.getListPion().get(46).getRepresentation());

        Damier damier2 = new Damier(damier.getListPion());
        assertEquals(40, damier2.getListPion().size());
    }
}
