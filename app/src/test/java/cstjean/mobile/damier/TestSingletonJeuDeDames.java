package cstjean.mobile.damier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import junit.framework.TestCase;

import cstjean.mobile.damier.classe.AffichageDamier;
import cstjean.mobile.damier.classe.Dames;
import cstjean.mobile.damier.classe.Pion;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Before;
import org.junit.Test;

/**
 * Cette classe de test effectue les actions du jeu de dames
 * pour verifier son bon fonctionnement.
 */
public class TestSingletonJeuDeDames extends TestCase {
    /**
     * Variable du jeu de dames pour les tests.
     */
    private cstjean.mobile.damier.classe.SingletonJeuDeDames jeu =
            cstjean.mobile.damier.classe.SingletonJeuDeDames.getInstance();

    /**
     * On initialise un jeu de dames et initialise le damier.
     */
    @Before
    public void setUp() {
        jeu.reset();
    }

    /**
     * Test pour la fonction reset.
     */
    @Test
    public void testInitialisationDamier() {
        assertEquals(40,
                jeu.getDamier().getNbPions());
    }

    /**
     * Verifie la prise d'un pion noir sur un blanc.
     */
    @Test
    public void testPriseParPionNoir(){
        jeu.bouger(34,30);
        jeu.bouger(20,25);
        jeu.bouger(31,26);
        jeu.prisePion(25,30,34);
        assertNotNull(jeu.getDamier().getPion(34));
        System.out.println(AffichageDamier.getAffichage(jeu.getDamier()));
    }
    /**
     * On vérifie si on peut ajouter un pion en commencant par le jeu de dames et remontant
     * jusqu'au damier.
     */
    @Test
    public void testAjoutPion() {
        jeu.reset();
        Pion pionBlanc = new Pion(Pion.Couleur.Blanc);
        jeu.getDamier().ajouterPion(25, pionBlanc);
        assertEquals(pionBlanc, jeu.getDamier().getListPion().get(25));
    }

    /**
     * Verifie si la methode empêche les mouvements invalides.
     */
    @Test
    public void testDeplacementInvalideCaseVide() {
        jeu.reset();
        jeu.vider();
        System.out.println(AffichageDamier.getAffichage(jeu.getDamier()));
        try {
            jeu.bouger(30, 35);
        } catch (IllegalArgumentException exMess) {
            assertEquals("Mouvement impossible, aucun pion.", exMess.getMessage());
        }
    }

    /**
     * Verifie si on ne peut pas prendre de pion à reculons.
     */
    @Test
    public void testCaptureInvalidePasDePionAdverse() {
        jeu.vider();
        jeu.reset();
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
            assertEquals("Mouvement impossible, mouvement diagonale seulement.",
                    exMess.getMessage());
        }
    }

    /**
     * Verifie les deplacements d'une dame.
     */
    @Test
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
    @Test
    public void testCaptureAvecDame() {
        jeu.reset();
        Dames dameBlanche = new Dames(Pion.Couleur.Blanc);
        jeu.getDamier().retirerPion(15);
        jeu.getDamier().ajouterPion(24, dameBlanche);
        jeu.prisePion(24, 20, 15);
        assertNull(jeu.getDamier().getListPion().get(20));
        assertTrue(jeu.getDamier().getListPion().get(15) instanceof Dames);
    }

    /**
     * Verifie si la prise est invalide.
     */
    @Test
    public void testCaptureAvecDameInvalide() {
        Dames dameBlanche = new Dames(Pion.Couleur.Blanc);
        jeu.getDamier().ajouterPion(25, dameBlanche);
    }

    /**
     * Verifie la transformation du pion en dames dans le jeu de dames.
     */
    @Test
    public void testDeplacementPionEnDames() {
        jeu.reset();
        jeu.vider();
        Pion pionBlanc = new Pion(Pion.Couleur.Blanc);
        Pion pionNoir = new Pion(Pion.Couleur.Noir);
        jeu.getDamier().ajouterPion(41, pionNoir);
        jeu.getDamier().ajouterPion(6, pionBlanc);
        jeu.getDamier().retirerPion(1);
        jeu.getDamier().retirerPion(46);

        jeu.bouger(6, 1);
        jeu.bouger(41, 46);
        assertTrue(jeu.getDamier().getListPion().get(46) instanceof Dames);
        assertTrue(jeu.getDamier().getListPion().get(1) instanceof Dames);
    }

    /**
     * Verifie la reinitialisation du jeu de dames.
     */
    @Test
    public void testReset() {
        jeu.reset();
        assertTrue(jeu.getEstTourBlanc());
        assertTrue(jeu.getListePionsPris().isEmpty());
    }

    /**
     * Verifie l'historique des mouvements.
     */
    @Test
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
    @Test
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
    @Test
    public void testRetourPartieMouvement() {
        jeu.vider();
        jeu.reset();
        System.out.println(AffichageDamier.getAffichage(jeu.getDamier()));
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
    @Test
    public void testRetourPartiePrise() {
        jeu.reset();
        jeu.vider();
        Pion pionNoir = new Pion(Pion.Couleur.Noir);
        Pion pionBlanc = new Pion(Pion.Couleur.Blanc);
        jeu.getDamier().ajouterPion(30, pionNoir);
        jeu.getDamier().ajouterPion(34, pionBlanc);
        System.out.println(AffichageDamier.getAffichage(jeu.getDamier()));
        jeu.prisePion(34, 30, 25);
        assertEquals("34x25", jeu.getHistoriqueDeplacementDamierPosition(0));
        jeu.retourPartie();
    }

    /**
     * Verifie si une position est valide.
     */
    @Test
    public void testEstPositionValide() {
        assertTrue(jeu.estPositionValide(1));  // Supposons que 0 est une position valide
        assertFalse(jeu.estPositionValide(100));  // Supposons que 100 est une position invalide
    }

    /**
     * Verifie si une partie est terminée.
     */
    @Test
    public void testEstPartietermine() {
        jeu.vider();
        jeu.reset();
        assertFalse(jeu.estPartieTerminee());
        for (int i = 1; i <= 51; i++) {
            jeu.getDamier().retirerPion(i);
        }
/*
        assertTrue(jeu.estPartieTerminee());
        jeu.getDamier().ajouterPion(15, new Pion(Pion.Couleur.Blanc));
        jeu.getDamier().ajouterPion(22, new Pion(Pion.Couleur.Blanc));
        assertTrue(jeu.estPartieTerminee());
        jeu.reset();
        for (int i = 1; i <= 50; i++) {
            jeu.getDamier().ajouterPion(i, new Pion(Pion.Couleur.Noir));
        }

        System.out.println(AffichageDamier.getAffichage(jeu.getDamier()));
        System.out.println(jeu.estPartieTerminee());
*/
        assertTrue(jeu.estPartieTerminee());
    }

    /**
     * Verifie si une partie est terminée.
     */
    @Test
    public void testMouvementsPossiblesDames() {
        jeu.reset();
        Dames dameBlanc = new Dames(Pion.Couleur.Blanc);
        TreeMap<Integer, Pion> map = jeu.getPionRestants();
        for (Map.Entry<Integer, Pion> i : map.entrySet()) {
            Integer cle = i.getKey();
            jeu.getDamier().retirerPion(cle);
        }
        Pion pionNoir = new Pion(Pion.Couleur.Noir);
        assertEquals("[]",
                jeu.mouvementsPossibles(15,false).toString());
        jeu.getDamier().ajouterPion(28, dameBlanc);
        jeu.getDamier().ajouterPion(23, pionNoir);
        assertEquals("[33, 39, 44, 50, 32, 37, 41, 46, 22, 17, 11, 6, 23]",
                jeu.mouvementsPossibles(28,false).toString());

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
                jeu.mouvementsPossibles(28,false).toString());

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
                jeu.mouvementsPossibles(28,false).toString());

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
                jeu.mouvementsPossibles(28,false).toString());

        assertNotNull(jeu.getDamier().getListPion().get(39));

        assertEquals("[44, 50, 43, 48, 33, 28, 22, 17, 11, 6, 34, 30, 25]",
                jeu.mouvementsPossibles(39,false).toString());

        jeu.getDamier().retirerPion(39);
        jeu.getDamier().ajouterPion(12, dameBlanc);
        assertEquals("[18, 23, 29, 34, 40, 45, 17, 21, 26, 7, 1, 8, 3]",
                jeu.mouvementsPossibles(12,false).toString());

        jeu.bouger(12, 23);
        jeu.getDamier().ajouterPion(4, pionNoir);
        Pion pionBlanc = new Pion(Pion.Couleur.Blanc);
        jeu.getDamier().ajouterPion(43, pionBlanc);

        assertEquals("[38, 39]", jeu.mouvementsPossibles(43,false).toString());
        assertEquals("[10, 9]", jeu.mouvementsPossibles(4,false).toString());
    }

    /**
     * Vérifie si après une prise, on tombe sur la bonne case. Si les entrées sont mauvaises.
     * On retourne 0.
     */
    @Test
    public void testgetFinalPositionPrise() {
        jeu.reset();
        assertEquals(0, jeu.getFinalPositionPrise(22, 36));
    }

    /**
     * Verifier si les fonctions dans l'enum retourne la bonne valeur ainsi que le bon nom.
     */
    @Test
    public void testEnumDirection() {
        cstjean.mobile.damier.classe.SingletonJeuDeDames.Direction[] directionArray;
        directionArray = cstjean.mobile.damier.classe
                .SingletonJeuDeDames.Direction.getDirectionBasStartBlanc();
        assertEquals("[BAS_DROIT_START_BLANC, BAS_GAUCHE_START_BLANC]",
                Arrays.toString(directionArray));

        assertEquals("[6, 5]", cstjean.mobile.damier.classe
                .SingletonJeuDeDames.Direction.getAllValue(directionArray).toString());
        directionArray = cstjean.mobile.damier.classe.SingletonJeuDeDames
                .Direction.getDirectionBasStartNoir();
        assertEquals("[BAS_DROIT_START_NOIR, BAS_GAUCHE_START_NOIR]",
                Arrays.toString(directionArray));

        assertEquals("[5, 4]", cstjean.mobile.damier.classe
                .SingletonJeuDeDames.Direction.getAllValue(directionArray).toString());
        directionArray = cstjean.mobile.damier.classe
                .SingletonJeuDeDames.Direction.getDirectionHautStartNoir();
        assertEquals("[HAUT_GAUCHE_START_NOIR, HAUT_DROIT_START_NOIR]",
                Arrays.toString(directionArray));

        assertEquals("[-6, -5]", cstjean.mobile.damier.classe
                .SingletonJeuDeDames.Direction.getAllValue(directionArray).toString());
        directionArray = cstjean.mobile.damier.classe
                .SingletonJeuDeDames.Direction.getDirectionHautStartBlanc();
        assertEquals("[HAUT_GAUCHE_START_BLANC, HAUT_DROIT_START_BLANC]",
                Arrays.toString(directionArray));

        assertEquals("[-5, -4]", cstjean.mobile.damier.classe
                .SingletonJeuDeDames.Direction.getAllValue(directionArray).toString());
    }
}
