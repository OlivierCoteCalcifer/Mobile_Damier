package cstjean.mobile.damier;

import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import junit.framework.TestCase;

import cstjean.mobile.damier.classe.Dames;
import cstjean.mobile.damier.classe.Pion;
import cstjean.mobile.damier.classe.SingletonJeuDeDames;

/**
 * Cette classe de test effectue les actions du jeu de dames
 * pour verifier son bon fonctionnement.
 */
public class TestJeuDeDames extends TestCase {
    /**
     * Variable du jeu de dames pour les tests.
     */
    private SingletonJeuDeDames jeu = SingletonJeuDeDames.getInstance();

    /**
     * On initialise un jeu de dames et initialise le damier.
     */
    public void setUp() {
        jeu.reset();
    }

    /**
     * Test pour la fonction reset.
     */
    public void testInitialisationDamier() {
        jeu.reset();
        assertEquals(40, jeu.getDamier().getNbPions());
    }

    /**
     * On vérifie si on peut ajouter un pion en commencant par le jeu de dames et remontant
     * jusqu'au damier.
     */
    public void testAjoutPion() {
        Pion pionBlanc = new Pion(Pion.Couleur.Blanc);
        jeu.getDamier().ajouterPion(25, pionBlanc);
        assertEquals(pionBlanc, jeu.getDamier().getListPion().get(25));
    }

    /**
     * Verifie si la methode empêche les mouvements invalides.
     */
    public void testDeplacementInvalideCaseVide() {
        try {
            jeu.bouger(30, 35);
        } catch (IllegalArgumentException exMess) {
            assertEquals("Mouvement impossible, aucun pion.", exMess.getMessage());
        }
    }

    /**
     * Verifie si on ne peut pas prendre de pion à reculons.
     */
    public void testCaptureInvalidePasDePionAdverse() {
        try {
            jeu.bouger(35, 30);
            jeu.prisePion(20, 24, 29);
        } catch (IllegalArgumentException exMess) {
            assertEquals("Prise impossible, pion(s) null(s).", exMess.getMessage());
        }
    }

    /**
     * Verifie le tour du joueur.
     */
    public void testTourApresMouvementInvalide() {
        try {
            jeu.bouger(35, 36);
            fail("Un IllegalArgumentException devrait être lancée.");
        } catch (IllegalArgumentException exMess) {
            assertTrue(jeu.getEstTourBlanc());
        }
    }

    /**
     * Verifie l'invalidité d'une prise d'un pion de la même couleur.
     */
    public void testPrisePionMemeCouleur() {
        jeu.reset();
        try {
            jeu.prisePion(40, 34, 29);
            fail("Un IllegalArgumentException devrait être lancée.");
        } catch (IllegalArgumentException exMess) {
            assertEquals("Prise impossible, même couleur.", exMess.getMessage());
        }

    }

    /**
     * Verifie une prise de pion invalide.
     */
    public void testPrisePionInvalide() {
        jeu.reset();
        Pion pionNoir = new Pion(Pion.Couleur.Noir);
        jeu.getDamier().retirerPion(34);
        jeu.getDamier().retirerPion(33);
        jeu.getDamier().ajouterPion(34, pionNoir);
        try {
            jeu.prisePion(35, 34, 33);
            fail("Un IllegalArgumentException devrait être lancée.");
        } catch (IllegalArgumentException exMess) {
            assertEquals("Prise impossible.", exMess.getMessage());
        }
    }

    /**
     * Verifie si on sélectionne le bon pion selon le bon tour du joueur.
     */
    public void testEssayerDeDeplacerPionAdverse() {
        jeu.reset();
        try {
            jeu.bouger(20, 24);
            fail("Un IllegalArgumentException devrait être lancée.");
        } catch (IllegalArgumentException exMess) {
            assertEquals("Mouvement impossible", exMess.getMessage());
        }
    }

    /**
     * Verifie les deplacements invalide.
     */
    public void testDeplacementInvalidePion() {
        jeu.reset();
        try {
            jeu.bouger(34, 24);
            fail("Un IllegalArgumentException devrait être lancée.");
        } catch (IllegalArgumentException exMess) {
            assertEquals("Mouvement impossible, mouvement diagonale seulement.", exMess.getMessage());
        }
        try {
            jeu.reset();
            jeu.bouger(40, 34);
            fail("Un IllegalArgumentException devrait être lancée.");
        } catch (IllegalArgumentException exMess) {
            assertEquals("Mouvement impossible, case déjà prise.", exMess.getMessage());
        }

        jeu.reset();
        jeu.getDamier().retirerPion(34);
        try {
            jeu.bouger(35, 34);
            fail("Un IllegalArgumentException devrait être lancée.");
        } catch (IllegalArgumentException exMess) {
            assertEquals("Mouvement impossible, mouvement diagonale seulement.", exMess.getMessage());
        }
    }

    /**
     * Verifie les deplacements d'une dame.
     */
    public void testDeplacementDame() {
        jeu.reset();
        Pion pionBlanc = new Dames(Pion.Couleur.Blanc);
        jeu.getDamier().ajouterPion(25, pionBlanc);
        jeu.bouger(25, 30);
        assertNull(jeu.getDamier().getListPion().get(25));
        assertTrue(jeu.getDamier().getListPion().get(30) instanceof Dames);
    }

    /**
     * Verifie les deplacements invalide de la dame.
     */
    public void testDeplacementDameInvalide() {

        Pion pionBlanc = new Dames(Pion.Couleur.Blanc);
        jeu.getDamier().ajouterPion(25, pionBlanc);
        try {
            jeu.bouger(25, 24);
            fail("Un IllegalArgumentException devrait être lancée.");
        } catch (IllegalArgumentException exMess) {
            assertEquals("Mouvement impossible", exMess.getMessage());
        }
        try {
            jeu.bouger(25, 20);
            fail("Un IllegalArgumentException devrait être lancée.");
        } catch (IllegalArgumentException exMess) {
            assertEquals("Mouvement impossible, case déjà prise.", exMess.getMessage());
        }
    }

    /**
     * Verifie les prises par la dame.
     */
    public void testCaptureAvecDame() {
        Dames dameBlanche = new Dames(Pion.Couleur.Blanc);
        jeu.getDamier().retirerPion(15);
        jeu.getDamier().ajouterPion(24, dameBlanche);
        jeu.prisePion(24, 20, 15);
        assertNull(jeu.getDamier().getListPion().get(25));
        assertNull(jeu.getDamier().getListPion().get(20));
        assertTrue(jeu.getDamier().getListPion().get(15) instanceof Dames);
    }

    /**
     * Verifie si la prise est invalide.
     */
    public void testCaptureAvecDameInvalide() {
        Dames dameBlanche = new Dames(Pion.Couleur.Blanc);
        jeu.getDamier().ajouterPion(25, dameBlanche);
    }

    /**
     * Verifie la transformation du pion en dames dans le jeu de dames.
     */
    public void testDeplacementPionEnDames() {
        Pion pionBlanc = new Pion(Pion.Couleur.Blanc);
        Pion pionNoir = new Pion(Pion.Couleur.Noir);
        jeu.getDamier().ajouterPion(41, pionNoir);
        jeu.getDamier().ajouterPion(6, pionBlanc);
        jeu.bouger(6, 1);
        jeu.bouger(41, 46);
        assertTrue(jeu.getDamier().getListPion().get(46) instanceof Dames);
        assertTrue(jeu.getDamier().getListPion().get(1) instanceof Dames);
    }

    /**
     * Verifie la reinitialisation du jeu de dames.
     */
    public void testReset() {
        jeu.reset();
        assertTrue(jeu.getEstTourBlanc());
        assertTrue(jeu.getListePionsPris().isEmpty());
    }

    /**
     * Verifie l'historique des mouvements.
     */
    public void testHistoriquePartieMouvement() {
        jeu.reset();
        jeu.bouger(34, 30);
        assertEquals("34-30", jeu.getHistoriqueDeplacementDamierPosition(0));
        jeu.bouger(19, 23);
        assertEquals("(19-23)", jeu.getHistoriqueDeplacementDamierPosition(1));

        jeu.reset();
        Pion dameNoir = new Dames(Pion.Couleur.Noir);
        jeu.getDamier().ajouterPion(27, dameNoir);
        jeu.bouger(33, 29);
        jeu.bouger(27, 22);
        assertEquals("(27-22)", jeu.getHistoriqueDeplacementDamierPosition(1));
    }

    /**
     * Verifie l'historique pour les prises.
     */
    public void testHistoriquePartiePrise() {
        jeu.reset();
        Pion pionNoir = new Pion(Pion.Couleur.Noir);
        jeu.getDamier().ajouterPion(30, pionNoir);
        jeu.getDamier().retirerPion(25);

        jeu.prisePion(34, 30, 25);
        assertEquals("34x25", jeu.getHistoriqueDeplacementDamierPosition(0));

        jeu.reset();
        Pion pionBlanc = new Pion(Pion.Couleur.Blanc);
        jeu.getDamier().ajouterPion(23, pionBlanc);
        jeu.bouger(35, 30);
        jeu.prisePion(19, 23, 28);
        assertEquals("(19x28)", jeu.getHistoriqueDeplacementDamierPosition(1));
    }

    /**
     * Verifie le callback des mouvements.
     */
    public void testRetourPartieMouvement() {
        jeu.reset();
        jeu.bouger(34, 30);
        assertEquals("34-30", jeu.getHistoriqueDeplacementDamierPosition(0));
        jeu.bouger(19, 23);
        assertEquals("(19-23)", jeu.getHistoriqueDeplacementDamierPosition(1));
        jeu.retourPartie();
        assertNull(jeu.getDamier().getListPion().get(23));
        assertEquals(1, jeu.getHistoriqueDeplacementDamier().size());
        jeu.retourPartie();
        assertNull(jeu.getDamier().getListPion().get(30));
        assertEquals(0, jeu.getHistoriqueDeplacementDamier().size());
    }

    /**
     * Verifie le callback des prises.
     */
    public void testRetourPartiePrise() {
        jeu.reset();
        Pion pionNoir = new Pion(Pion.Couleur.Noir);
        jeu.getDamier().ajouterPion(30, pionNoir);
        jeu.getDamier().retirerPion(25);

        jeu.prisePion(34, 30, 25);
        assertEquals("34x25", jeu.getHistoriqueDeplacementDamierPosition(0));
        jeu.retourPartie();
    }

    /**
     * Verifie si une position est valide.
     */
    public void testEstPositionValide() {
        assertTrue(jeu.estPositionValide(1));  // Supposons que 0 est une position valide
        assertFalse(jeu.estPositionValide(100));  // Supposons que 100 est une position invalide
    }

    /**
     * Verifie si une partie est terminée.
     */
    public void testEstPartietermine() {
        jeu.reset();
        assertFalse(jeu.estPartieTerminee());

        for (int i = 1; i <= 51; i++) {
            jeu.getDamier().retirerPion(i);
        }
        assertTrue(jeu.estPartieTerminee());
        jeu.getDamier().ajouterPion(15, new Pion(Pion.Couleur.Blanc));
        jeu.getDamier().ajouterPion(22, new Pion(Pion.Couleur.Blanc));
        assertTrue(jeu.estPartieTerminee());
        jeu.reset();
        for (int i = 1; i <= 50; i++) {
            jeu.getDamier().ajouterPion(i, new Pion(Pion.Couleur.Noir));
        }

        assertTrue(jeu.estPartieTerminee());
    }

    /**
     * Verifie si une partie est terminée.
     */
    public void testMouvementsPossiblesDames() {
        jeu.reset();
        jeu.reset();
        Dames dameBlanc = new Dames(Pion.Couleur.Blanc);
        TreeMap<Integer, Pion> map = jeu.getPionRestants();
        for (Map.Entry<Integer, Pion> i : map.entrySet()) {
            Integer cle = i.getKey();
            jeu.getDamier().retirerPion(cle);
        }
        Pion pionNoir = new Pion(Pion.Couleur.Noir);
        assertEquals("[]",
                jeu.mouvementsPossibles(15).toString());
        jeu.getDamier().ajouterPion(28, dameBlanc);
        jeu.getDamier().ajouterPion(23, pionNoir);
        assertEquals("[33, 39, 44, 50, 32, 37, 41, 46, 22, 17, 11, 6, 23]",
                jeu.mouvementsPossibles(28).toString());

        assertNotNull(jeu.getDamier().getListPion().get(19));

        jeu.reset();
        map = jeu.getPionRestants();
        for (Map.Entry<Integer, Pion> i : map.entrySet()) {
            Integer cle = i.getKey();
            jeu.getDamier().retirerPion(cle);
        }
        jeu.getDamier().retirerPion(19);
        jeu.getDamier().ajouterPion(28, dameBlanc);
        jeu.getDamier().ajouterPion(22, pionNoir);
        assertEquals("[33, 39, 44, 50, 32, 37, 41, 46, 22]",
                jeu.mouvementsPossibles(28).toString());

        assertNotNull(jeu.getDamier().getListPion().get(17));

        jeu.reset();
        map = jeu.getPionRestants();
        for (Map.Entry<Integer, Pion> i : map.entrySet()) {
            Integer cle = i.getKey();
            jeu.getDamier().retirerPion(cle);
        }
        jeu.getDamier().retirerPion(22);
        jeu.getDamier().ajouterPion(28, dameBlanc);
        jeu.getDamier().ajouterPion(32, pionNoir);
        assertEquals("[33, 39, 44, 50, 32]",
                jeu.mouvementsPossibles(28).toString());

        jeu.getDamier().retirerPion(23);
        assertNotNull(jeu.getDamier().getListPion().get(37));

        jeu.reset();
        map = jeu.getPionRestants();
        for (Map.Entry<Integer, Pion> i : map.entrySet()) {
            Integer cle = i.getKey();
            jeu.getDamier().retirerPion(cle);
        }
        jeu.getDamier().ajouterPion(28, dameBlanc);
        jeu.getDamier().ajouterPion(33, pionNoir);
        assertEquals("[33]",
                jeu.mouvementsPossibles(28).toString());

        assertNotNull(jeu.getDamier().getListPion().get(39));

        assertEquals("[44, 50, 43, 48, 33, 28, 22, 17, 11, 6, 34, 30, 25]",
                jeu.mouvementsPossibles(39).toString());

        jeu.getDamier().retirerPion(39);
        jeu.getDamier().ajouterPion(12, dameBlanc);
        assertEquals("[18, 23, 29, 34, 40, 45, 17, 21, 26, 7, 1, 8, 3]",
                jeu.mouvementsPossibles(12).toString());

        jeu.bouger(12, 23);
        jeu.getDamier().ajouterPion(4, pionNoir);
        Pion pionBlanc = new Pion(Pion.Couleur.Blanc);
        jeu.getDamier().ajouterPion(43, pionBlanc);

        assertEquals("[38, 39]", jeu.mouvementsPossibles(43).toString());
        assertEquals("[10, 9]", jeu.mouvementsPossibles(4).toString());
    }

    /**
     * Vérifie si après une prise, on tombe sur la bonne case. Si les entrées sont mauvaises.
     * On retourne 0.
     */
    public void testgetFinalPositionPrise() {
        jeu.reset();
        assertEquals(0, jeu.getFinalPositionPrise(22, 36));
    }

    /**
     * Verifier si les fonctions dans l'enum retourne la bonne valeur ainsi que le bon nom.
     */
    public void testEnumDirection() {
        SingletonJeuDeDames.Direction[] directionArray;
        directionArray = SingletonJeuDeDames.Direction.getDirectionBasStartBlanc();
        assertEquals("[BAS_DROIT_START_BLANC, BAS_GAUCHE_START_BLANC]", Arrays.toString(directionArray));

        assertEquals("[6, 5]", SingletonJeuDeDames.Direction.getAllValue(directionArray).toString());
        directionArray = SingletonJeuDeDames.Direction.getDirectionBasStartNoir();
        assertEquals("[BAS_DROIT_START_NOIR, BAS_GAUCHE_START_NOIR]", Arrays.toString(directionArray));

        assertEquals("[5, 4]", SingletonJeuDeDames.Direction.getAllValue(directionArray).toString());
        directionArray = SingletonJeuDeDames.Direction.getDirectionHautStartNoir();
        assertEquals("[HAUT_GAUCHE_START_NOIR, HAUT_DROIT_START_NOIR]", Arrays.toString(directionArray));

        assertEquals("[-6, -5]", SingletonJeuDeDames.Direction.getAllValue(directionArray).toString());
        directionArray = SingletonJeuDeDames.Direction.getDirectionHautStartBlanc();
        assertEquals("[HAUT_GAUCHE_START_BLANC, HAUT_DROIT_START_BLANC]", Arrays.toString(directionArray));

        assertEquals("[-5, -4]", SingletonJeuDeDames.Direction.getAllValue(directionArray).toString());
    }
}
