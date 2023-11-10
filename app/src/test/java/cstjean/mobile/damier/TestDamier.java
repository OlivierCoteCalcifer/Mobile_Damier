package cstjean.mobile.damier;

import cstjean.mobile.damier.classe.Damier;
import cstjean.mobile.damier.classe.Pion;
import java.util.TreeMap;
import junit.framework.TestCase;

/**
 * Ceci est la classe TestDamier avec les tests nécessaires pour valider la classe Damier.
 */

public class TestDamier extends TestCase {
    /**
     * Variable pour un damier.
     */
    private Damier damier;
    /**
     * Variable pour un damier pour tester l'initialisation.
     */
    private Damier damier2;
    /**
     * Variable pour un damier pour tester l'initialisation.
     */
    private Damier damier3;
    /**
     * Variable pour un pion avec la couleur noir.
     */
    private Pion pionNoir;
    /**
     * Variable pour un pion avec la couleur blanc.
     */
    private Pion pionBlanc;
    /**
     * Variable TreeMap pour tester le constructeur.
     */
    private TreeMap<Integer, Pion> treeMap;

    /**
     * Cette fonction prepare les variables pour les tests.
     */
    public void setUp() {
        pionNoir = new Pion(Pion.Couleur.Noir);
        pionBlanc = new Pion(Pion.Couleur.Blanc);
        damier = new Damier();
        damier2 = new Damier();
        damier2.initialiser();
        treeMap = new TreeMap<>();
        damier3 = new Damier();
    }

    /**
     * Cette fonction creer et test la classe Damier en ajoutant des pions sur des emplacements
     * dans le damier et verifie par la suite sa position.
     */
    public void testCreer() {
        // Verifie la grosseur du damier.
        assertEquals(0, damier.getListPion().size());
        damier.ajouterPion(20, pionBlanc);
        damier.ajouterPion(3, pionNoir);
        // Verifie que la position 20 contient un blanc.
        assertEquals(Pion.Couleur.Blanc,
                damier.getListPion().get(20).getCouleurPion());
        assertEquals(Pion.Couleur.Noir,
                damier.getListPion().get(3).getCouleurPion());
        assertEquals(2, damier.getNbPions());
        treeMap.putAll(damier.getListPion());
        assertEquals(40, damier2.getNbPions());
        damier3 = new Damier(damier.getListPion());
        assertEquals(damier.getListPion(), damier3.getListPion());
    }
}
