package cstjean.mobile.damier;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Cette classe regroupe tout les tests dans un morceau.
 */
public class TestComplet extends TestCase {
    /**
     * Cette fonction regroupe les tests et retourne la suite de tests.
     *
     * @return suite Paramètre qui inclut les suites de Tests.
     */
    public static TestSuite suite() {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(TestPion.class);
        suite.addTestSuite(TestDamier.class);
        suite.addTestSuite(TestAffichageDamier.class);
        suite.addTestSuite(TestDames.class);
        suite.addTestSuite(TestSingletonJeuDeDames.class);
        return suite;
    }
}
