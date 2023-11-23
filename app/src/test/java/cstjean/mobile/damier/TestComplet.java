package cstjean.mobile.damier;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Cette classe regroupe tout les tests dans un morceau.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    TestPion.class,
    TestDamier.class,
    TestAffichageDamier.class,
    TestDames.class,
    TestSingletonJeuDeDames.class
})
public class TestComplet {
    // This class remains empty, it is used only as a holder for the above annotations
}
