package cstjean.mobile.damier;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withTagValue;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.CoreMatchers.is;

import android.os.RemoteException;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.uiautomator.UiDevice;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class DamierMenuInstrumentedTest extends RemoteException {

    /**
     * On va chercher l'activity qu'on veut faire les tests.
     */
    @Rule
    public ActivityScenarioRule<MenuActivity> rule = new ActivityScenarioRule<>(MenuActivity.class);

    /**
     * Ce test est pour verifier la saisie des noms dans le menu et si le changement d'orientation
     * affecte le TextInputEditText.
     */
    @Test
    public void testSaisieNomDesJoueursMenuActivity() throws RemoteException {

        // On place l'appareil en portrait
        UiDevice device = UiDevice.getInstance(getInstrumentation());
        device.setOrientationNatural();
        String tagMenuInput = "menuInput";
        String nomJoueur1 = "Olivier";

        // On clique sur le menu bouton pour generer les TextInputEditText et on verifie si le nom
        // entrer est le meme
        onView(withId(R.id.menu_Bouton)).perform(click());
        onView(withTagValue(is(tagMenuInput))).perform(typeText(nomJoueur1));
        withText(nomJoueur1).matches(onView(withTagValue(is(tagMenuInput))));

        // On tourne l'appareil vers la gauche et verifie si nomJoueur1 est toujours present.
        device.setOrientationLeft();
        withText(nomJoueur1).matches(onView(withTagValue(is(tagMenuInput))));
        pressBack();

        // On verifie si le nomJoueur2 est present dans le TextEditInputText affiche bien le
        // nomJoueur2.
        onView(withId(R.id.menu_Bouton)).perform(click());
        onView(withId(R.id.menu_Bouton)).perform(click());
        String nomJoueur2 = "Thomas";
        onView(withTagValue(is(tagMenuInput))).perform(typeText(nomJoueur2));
        withText(nomJoueur2).matches(onView(withTagValue(is(tagMenuInput))));

        // On remet l'appareil en portrait pour voir si nomJoueur2 est toujours present.
        device.setOrientationNatural();
        withText(nomJoueur2).matches(onView(withTagValue(is(tagMenuInput))));
    }
}