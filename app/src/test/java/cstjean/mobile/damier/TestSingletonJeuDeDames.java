package cstjean.mobile.damier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import cstjean.mobile.damier.classe.Dames;
import cstjean.mobile.damier.classe.Pion;
import cstjean.mobile.damier.classe.SingletonJeuDeDames;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Cette classe de test effectue les actions du jeu de dames
 * pour verifier son bon fonctionnement.
 */
public class TestSingletonJeuDeDames {
    /**
     * Variable du jeu de dames pour les tests.
     */
    private final cstjean.mobile.damier.classe.SingletonJeuDeDames jeu =
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
     * Ce test verifie si les pions sur des emplacements précis du damier redonne le bon mouvement.
     */
    @Test
    public void testMouvementBorder() {
        jeu.reset();
        jeu.vider();
        jeu.getDamier().ajouterPion(50, new Pion(Pion.Couleur.Blanc));
        assertEquals("[44, 45]", jeu.mouvementsPossibles(50, false)
                .toString());
        jeu.getDamier().ajouterPion(1, new Pion(Pion.Couleur.Noir));
        assertEquals("[7, 6]", jeu.mouvementsPossibles(1, false)
                .toString());
        assertEquals("[]", jeu.mouvementsPossibles(2, false)
                .toString());
        List<Integer> mvt = new ArrayList<>();
        assertFalse(jeu.checkBoardPion(2, null, mvt));
        assertFalse(jeu.handlePionBlancMovesBorder(2, mvt));
        assertFalse(jeu.handlePionNoirMovesBorder(2, mvt));
    }

    /**
     * Verifie la prise d'un pion noir sur un blanc.
     */
    @Test
    public void testPriseParPionNoir() {
        jeu.reset();
        jeu.bouger(34, 30);
        jeu.bouger(20, 25);
        jeu.bouger(31, 26);
        jeu.prisePion(25, 30, 35);
        assertNotNull(jeu.getDamier().getPion(35));
        jeu.reset();
        jeu.vider();
        jeu.getDamier().ajouterPion(20, new Pion(Pion.Couleur.Noir));
        jeu.getDamier().ajouterPion(29, new Pion(Pion.Couleur.Blanc));
        jeu.bouger(29, 24);
        jeu.bouger(20, 24);
        assertNotNull(jeu.getDamier().getPion(29));
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
            Assert.fail("Un IllegalArgumentException devrait être lancée.");
        } catch (IllegalArgumentException exMess) {
            Assert.assertTrue(jeu.getEstTourBlanc());
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
            Assert.fail("Un IllegalArgumentException devrait être lancée.");
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
            Assert.fail("Un IllegalArgumentException devrait être lancée.");
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
            Assert.fail("Un IllegalArgumentException devrait être lancée.");
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
            jeu.bouger(34, 23);
            Assert.fail("Un IllegalArgumentException devrait être lancée.");
        } catch (IllegalArgumentException exMess) {
            assertEquals("Mouvement impossible, mouvement diagonale seulement.", exMess.getMessage());
        }

        jeu.reset();
        jeu.getDamier().retirerPion(34);
        try {
            jeu.bouger(35, 34);
            Assert.fail("Un IllegalArgumentException devrait être lancée.");
        } catch (IllegalArgumentException exMess) {
            assertEquals("Mouvement impossible, mouvement diagonale seulement.",
                    exMess.getMessage());
        }
    }

    /**
     * Verifie les deplacements invalide de la dame.
     */
    @Test
    public void testDeplacementDameInvalide() {
        jeu.vider();
        jeu.reset();
        Pion pionBlanc = new Dames(Pion.Couleur.Blanc);
        jeu.getDamier().ajouterPion(25, pionBlanc);
        try {
            jeu.bouger(25, 24);
            Assert.fail("Un IllegalArgumentException devrait être lancée.");
        } catch (IllegalArgumentException exMess) {
            assertEquals("Mouvement impossible", exMess.getMessage());
        }
    }

    /**
     * Verifie les prises par la dame.
     */
    @Test
    public void testCaptureAvecDame() {
        jeu.reset();
        jeu.vider();
        Dames dameBlanche = new Dames(Pion.Couleur.Blanc);
        jeu.getDamier().ajouterPion(20, new Pion(Pion.Couleur.Noir));
        jeu.getDamier().ajouterPion(24, dameBlanche);
        jeu.prisePion(24, 20, 15);
        Assert.assertNull(jeu.getDamier().getListPion().get(20));
        Assert.assertTrue(jeu.getDamier().getListPion().get(15) instanceof Dames);
        jeu.reset();
        jeu.vider();
        jeu.getDamier().ajouterPion(50, new Dames(Pion.Couleur.Blanc));
        jeu.getDamier().ajouterPion(44, new Pion(Pion.Couleur.Noir));
        assertEquals("[]", jeu.mouvementsPossibles(50, false)
                .toString());
        assertNotNull(jeu.getDamier().getPion(39));
        jeu.reset();
        jeu.vider();
        jeu.getDamier().ajouterPion(50, new Dames(Pion.Couleur.Blanc));
        jeu.getDamier().ajouterPion(39, new Pion(Pion.Couleur.Noir));
        assertEquals("[44]", jeu.mouvementsPossibles(50, false)
                .toString());

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
        Assert.assertTrue(jeu.getDamier().getListPion().get(46) instanceof Dames);
        Assert.assertTrue(jeu.getDamier().getListPion().get(1) instanceof Dames);
    }

    /**
     * Verifie la reinitialisation du jeu de dames.
     */
    @Test
    public void testReset() {
        jeu.reset();
        Assert.assertTrue(jeu.getEstTourBlanc());
        Assert.assertTrue(jeu.getListePionsPris().isEmpty());
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
        jeu.bouger(34, 30);
        assertEquals("34-30", jeu.getHistoriqueDeplacementDamierPosition(0));
        jeu.bouger(19, 23);
        assertEquals("(19-23)", jeu.getHistoriqueDeplacementDamierPosition(1));
        jeu.retourPartie();
        Assert.assertNull(jeu.getDamier().getListPion().get(23));
        assertEquals(1, jeu.getHistoriqueDeplacementDamier().size());
        jeu.retourPartie();
        Assert.assertNull(jeu.getDamier().getListPion().get(30));
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
        jeu.prisePion(34, 30, 25);
        assertEquals("34x25", jeu.getHistoriqueDeplacementDamierPosition(0));
        jeu.retourPartie();
    }

    /**
     * Verifie si une position est valide.
     */
    @Test
    public void testEstPositionValide() {
        Assert.assertTrue(jeu.estPositionValide(1));  // Supposons que 0 est une position valide
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
        Assert.assertTrue(jeu.estPartieTerminee());
    }

    /**
     * Verifie si une partie est terminée.
     */
    @Test
    public void testMouvementsPossiblesDames() {
        jeu.reset();
        jeu.vider();
        Pion pionBlanc = new Dames(Pion.Couleur.Blanc);
        jeu.getDamier().ajouterPion(25, pionBlanc);
        jeu.bouger(25, 30);
        Assert.assertNull(jeu.getDamier().getListPion().get(25));
        Assert.assertTrue(jeu.getDamier().getListPion().get(30) instanceof Dames);
        jeu.reset();
        jeu.vider();
        jeu.getDamier().ajouterPion(44, new Dames(Pion.Couleur.Blanc));
        assertEquals("[50, 49, 39, 33, 28, 22, 17, 11, 6, 40, 35]",
                jeu.mouvementsPossibles(44, false).toString());
        // Verifie si on envoie le bon enum pour une dame sur le cote gauche.
        jeu.reset();
        jeu.vider();
        jeu.getDamier().ajouterPion(16, new Dames(Pion.Couleur.Blanc));
        assertEquals("[21, 27, 32, 38, 43, 49, 11, 7, 2]",
                jeu.mouvementsPossibles(16, true).toString());

        jeu.reset();
        Dames dameBlanc = new Dames(Pion.Couleur.Blanc);
        TreeMap<Integer, Pion> map = jeu.getPionRestants();
        for (Map.Entry<Integer, Pion> i : map.entrySet()) {
            Integer cle = i.getKey();
            jeu.getDamier().retirerPion(cle);
        }
        Pion pionNoir = new Pion(Pion.Couleur.Noir);
        assertEquals("[]",
                jeu.mouvementsPossibles(15, false).toString());
        jeu.getDamier().ajouterPion(28, dameBlanc);
        jeu.getDamier().ajouterPion(23, pionNoir);
        assertEquals("[33, 39, 44, 50, 32, 37, 41, 46, 22, 17, 11, 6]",
                jeu.mouvementsPossibles(28, false).toString());

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
        assertEquals("[33, 39, 44, 50, 32, 37, 41, 46]",
                jeu.mouvementsPossibles(28, false).toString());

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
        assertEquals("[33, 39, 44, 50]",
                jeu.mouvementsPossibles(28, false).toString());

        jeu.getDamier().retirerPion(23);
        assertNotNull(jeu.getDamier().getListPion().get(37));

        jeu.reset();
        jeu.vider();
        jeu.getDamier().ajouterPion(28, dameBlanc);
        jeu.getDamier().ajouterPion(33, pionNoir);
        assertEquals("[]",
                jeu.mouvementsPossibles(28, false).toString());

        assertNotNull(jeu.getDamier().getListPion().get(39));

        assertEquals("[44, 50, 43, 48, 33, 28, 22, 17, 11, 6, 34, 30, 25]",
                jeu.mouvementsPossibles(39, false).toString());

        jeu.getDamier().retirerPion(39);
        jeu.getDamier().ajouterPion(12, dameBlanc);
        assertEquals("[18, 23, 29, 34, 40, 45, 17, 21, 26, 7, 1, 8, 3]",
                jeu.mouvementsPossibles(12, false).toString());

        jeu.bouger(12, 23);
        jeu.reset();
        jeu.vider();
        jeu.getDamier().ajouterPion(4, pionNoir);
        pionBlanc = new Pion(Pion.Couleur.Blanc);
        jeu.getDamier().ajouterPion(43, pionBlanc);

        assertEquals("[38, 39]", jeu
                .mouvementsPossibles(43, true).toString());

        jeu.reset();
        jeu.vider();
        jeu.getDamier().ajouterPion(50, dameBlanc);
        assertEquals("[44, 39, 33, 28, 22, 17, 11, 6, 45]",
                jeu.mouvementsPossibles(50, true).toString());
        jeu.getDamier().ajouterPion(5, dameBlanc);
        assertEquals("[10, 14, 19, 23, 28, 32, 37, 41, 46]",
                jeu.mouvementsPossibles(5, true).toString());
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

        assertEquals("[6, 5]", SingletonJeuDeDames.Direction.getAllValue(directionArray).toString());
        directionArray = cstjean.mobile.damier.classe.SingletonJeuDeDames
                .Direction.getDirectionBasStartNoir();
        assertEquals("[BAS_DROIT_START_NOIR, BAS_GAUCHE_START_NOIR]",
                Arrays.toString(directionArray));

        assertEquals("[5, 4]", SingletonJeuDeDames.Direction.getAllValue(directionArray).toString());
        directionArray = cstjean.mobile.damier.classe
                .SingletonJeuDeDames.Direction.getDirectionHautStartNoir();
        assertEquals("[HAUT_GAUCHE_START_NOIR, HAUT_DROIT_START_NOIR]",
                Arrays.toString(directionArray));

        assertEquals("[-6, -5]", SingletonJeuDeDames.Direction.getAllValue(directionArray).toString());
        directionArray = cstjean.mobile.damier.classe
                .SingletonJeuDeDames.Direction.getDirectionHautStartBlanc();
        assertEquals("[HAUT_GAUCHE_START_BLANC, HAUT_DROIT_START_BLANC]",
                Arrays.toString(directionArray));

        assertEquals("[-5, -4]", SingletonJeuDeDames.Direction.getAllValue(directionArray).toString());

        directionArray = cstjean.mobile.damier.classe
                .SingletonJeuDeDames.Direction.getDirectionDameStartNoirVerDroite();
        assertEquals("[5, -5]", SingletonJeuDeDames.Direction.getAllValue(directionArray).toString());
        assertEquals("[BAS_DROIT_START_NOIR, HAUT_DROIT_START_NOIR]",
                Arrays.toString(directionArray));

        directionArray = cstjean.mobile.damier.classe
                .SingletonJeuDeDames.Direction.getDirectionDameStartBlancVersDroite();
        assertEquals("[5, -5]", SingletonJeuDeDames.Direction.getAllValue(directionArray).toString());
        assertEquals("[BAS_GAUCHE_START_BLANC, HAUT_GAUCHE_START_BLANC]",
                Arrays.toString(directionArray));

        directionArray = cstjean.mobile.damier.classe
                .SingletonJeuDeDames.Direction.getDirectionStartBlancFifthCase();
        assertEquals("[5]", SingletonJeuDeDames.Direction.getAllValue(directionArray).toString());
        assertEquals("[BAS_GAUCHE_START_BLANC]",
                Arrays.toString(directionArray));
    }

    /**
     * Ce test verifie la transformation du pion en dames par une prise.
     */
    @Test
    public void testPriseTranformationDame() {
        // Prise de pion noir par blanc
        jeu.reset();
        jeu.vider();
        jeu.getDamier().ajouterPion(10, new Pion(Pion.Couleur.Noir));
        jeu.getDamier().ajouterPion(14, new Pion(Pion.Couleur.Blanc));
        jeu.prisePion(14, 10, 5);
        Assert.assertTrue(jeu.getDamier().getListPion().get(5) instanceof Dames);

        // Prise de pion blanc par noir
        jeu.reset();
        jeu.vider();
        jeu.getDamier().ajouterPion(15, new Pion(Pion.Couleur.Blanc));
        jeu.bouger(15, 10);
        jeu.getDamier().ajouterPion(40, new Pion(Pion.Couleur.Noir));
        jeu.getDamier().ajouterPion(44, new Pion(Pion.Couleur.Blanc));
        jeu.prisePion(40, 44, 49);
        Assert.assertTrue(jeu.getDamier().getListPion().get(49) instanceof Dames);

    }

    /**
     * Ce test verifie si le retour de partie converti la dame en pion.
     */
    @Test
    public void testRetourPartiePromotion() {
        jeu.reset();
        jeu.vider();
        jeu.getDamier().ajouterPion(42, new Pion(Pion.Couleur.Noir));
        jeu.getDamier().ajouterPion(6, new Pion(Pion.Couleur.Blanc));
        jeu.bouger(6, 1);
        jeu.bouger(42, 47);
        jeu.bouger(1, 6);
        jeu.retourPartie();
        jeu.retourPartie();
        assertEquals('P', jeu.getDamier().getPion(42).getRepresentation());
    }

    /**
     * Ce test verifie les mouvements par les pions ou dames sur les cotes.
     */
    @Test
    public void testBougerBorder() {
        jeu.reset();
        jeu.vider();
        // Verification pion Noir sur border gauche et droit
        jeu.getDamier().ajouterPion(6, new Pion(Pion.Couleur.Noir));
        assertEquals("[11]", jeu.mouvementsPossibles(6, true).toString());
        jeu.getDamier().ajouterPion(16, new Pion(Pion.Couleur.Noir));
        assertEquals("[21]", jeu.mouvementsPossibles(16, true).toString());
        jeu.getDamier().ajouterPion(26, new Pion(Pion.Couleur.Noir));
        assertEquals("[31]", jeu.mouvementsPossibles(26, true).toString());
        jeu.getDamier().ajouterPion(36, new Pion(Pion.Couleur.Noir));
        assertEquals("[41]", jeu.mouvementsPossibles(36, true).toString());

        jeu.getDamier().ajouterPion(5, new Pion(Pion.Couleur.Noir));
        assertEquals("[10]", jeu.mouvementsPossibles(5, true).toString());
        jeu.getDamier().ajouterPion(15, new Pion(Pion.Couleur.Noir));
        assertEquals("[20]", jeu.mouvementsPossibles(15, true).toString());
        jeu.getDamier().ajouterPion(25, new Pion(Pion.Couleur.Noir));
        assertEquals("[30]", jeu.mouvementsPossibles(25, true).toString());
        jeu.getDamier().ajouterPion(35, new Pion(Pion.Couleur.Noir));
        assertEquals("[40]", jeu.mouvementsPossibles(35, true).toString());
        jeu.getDamier().ajouterPion(45, new Pion(Pion.Couleur.Noir));
        assertEquals("[50]", jeu.mouvementsPossibles(45, true).toString());

        // Verification pion Blanc sur border gauche et droit
        jeu.vider();
        jeu.getDamier().ajouterPion(6, new Pion(Pion.Couleur.Blanc));
        assertEquals("[1]", jeu.mouvementsPossibles(6, true).toString());
        jeu.getDamier().ajouterPion(16, new Pion(Pion.Couleur.Blanc));
        assertEquals("[11]", jeu.mouvementsPossibles(16, true).toString());
        jeu.getDamier().ajouterPion(26, new Pion(Pion.Couleur.Blanc));
        assertEquals("[21]", jeu.mouvementsPossibles(26, true).toString());
        jeu.getDamier().ajouterPion(36, new Pion(Pion.Couleur.Blanc));
        assertEquals("[31]", jeu.mouvementsPossibles(36, true).toString());
        jeu.getDamier().ajouterPion(46, new Pion(Pion.Couleur.Blanc));
        assertEquals("[41]", jeu.mouvementsPossibles(46, true).toString());

        jeu.getDamier().ajouterPion(15, new Pion(Pion.Couleur.Blanc));
        assertEquals("[10]", jeu.mouvementsPossibles(15, true).toString());
        jeu.getDamier().ajouterPion(25, new Pion(Pion.Couleur.Blanc));
        assertEquals("[20]", jeu.mouvementsPossibles(25, true).toString());
        jeu.getDamier().ajouterPion(35, new Pion(Pion.Couleur.Blanc));
        assertEquals("[30]", jeu.mouvementsPossibles(35, true).toString());
        jeu.getDamier().ajouterPion(45, new Pion(Pion.Couleur.Blanc));
        assertEquals("[40]", jeu.mouvementsPossibles(45, true).toString());
    }
}
