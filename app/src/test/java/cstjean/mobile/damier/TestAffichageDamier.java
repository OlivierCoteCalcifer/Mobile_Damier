package cstjean.mobile.damier;

import junit.framework.TestCase;

import cstjean.mobile.damier.classe.AffichageDamier;
import cstjean.mobile.damier.classe.Damier;
import cstjean.mobile.damier.classe.Pion;

/**
 * Ces tests voient si l'affichage du damier est conforme aux normes.
 */
public class TestAffichageDamier extends TestCase {
    /**
     * Variable de la classe du damier pour les tests.
     */
    private Damier damier1;
    /**
     * Cette variable contient le resultat d'affichage attendu avec une initialisation
     * du damier de base.
     */
    private String resultat1;
    /**
     * Cette variable contient le resultat d'affichage attendu avec une initialisation
     * du damier de base.
     */
    private String resultat2;
    /**
     * Cette variable contient le resultat d'affichage attendu avec une initialisation
     * du damier de base.
     */
    private String resultat3;
    /**
     * Variable d'un autre damier.
     */
    private Damier damier2;
    /**
     * Variable d'un pion blanc.
     */
    private Pion pionBlanc;

    /**
     * Cette methode instantie les variables nécessaires pour les tests d'affichage du damier.
     */
    public void setUp() {
        damier1 = new Damier();
        damier1.initialiser();
        resultat1 = """
                -P-P-P-P-P
                P-P-P-P-P-
                -P-P-P-P-P
                P-P-P-P-P-
                ----------
                ----------
                -p-p-p-p-p
                p-p-p-p-p-
                -p-p-p-p-p
                p-p-p-p-p-
                """;
        resultat2 = """
                ----------
                ----------
                -p--------
                ----------
                ----------
                ----------
                ----------
                ----------
                ----------
                ----------
                """;
        resultat3 = """
                ----------
                ----------
                -p--------
                ----------
                -------p--
                ----------
                ----------
                ----------
                ----------
                ----------
                """;
        damier2 = new Damier();
        pionBlanc = new Pion(Pion.Couleur.Blanc);
        damier2.ajouterPion(11, pionBlanc);
    }

    /**
     * Cette methode verifie l'affichage du damier avec le resultat avec un assertEquals.
     */
    public void testCreer() {
        assertEquals(resultat1, AffichageDamier.getAffichage(damier1));
        assertEquals(resultat2, AffichageDamier.getAffichage(damier2));
        damier2.ajouterPion(24, pionBlanc);
        assertEquals(resultat3, AffichageDamier.getAffichage(damier2));
    }
}
