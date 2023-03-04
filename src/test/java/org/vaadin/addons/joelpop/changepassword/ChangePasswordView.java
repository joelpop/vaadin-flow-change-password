package org.vaadin.addons.joelpop.changepassword;

import com.nulabinc.zxcvbn.Zxcvbn;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.router.Route;

import java.util.function.Function;

@Route("")
public class ChangePasswordView extends Composite<Div> {

    private final transient Zxcvbn zxcvbn;
    private final Function<CharSequence, String> passwordEncoder;

    public ChangePasswordView() {
        zxcvbn = new Zxcvbn();
        passwordEncoder = CharSequence::toString;

        var button = new Button("Change Password", this::onChangePasswordClick);

        getContent().add(button);
    }

    private void onChangePasswordClick(ClickEvent<Button> event) {
        var changePasswordDialog = new ChangePasswordDialog();
        changePasswordDialog.setInfoText(new Html(
                "<span>\n" +
                      "    Instructions\n" +
                      "    <ol>\n" +
                      "        <li>Enter the current password.\n" +
                      "        <li>Provide a new password that satisfies the following complexity rules.\n" +
                      "        <li>Verify its strength with the meter.\n" +
                      "        <li>Repeat the new password to confirm.\n" +
                      "    </ol>\n" +
                      "</span>"));
        changePasswordDialog.addRule(PasswordRule.length(8));
        changePasswordDialog.addRule(PasswordRule.length(10, 64));
        changePasswordDialog.addRule(PasswordRule.hasUppercaseLetters(1));
        changePasswordDialog.addRule(PasswordRule.hasLowercaseLetters(1));
        changePasswordDialog.addRule(PasswordRule.hasDigits(1));
        changePasswordDialog.addRule(PasswordRule.hasSpecials(1));
        changePasswordDialog.addRule(PasswordRule.hasCharacterGroups(3));
        changePasswordDialog.addRule(PasswordRule.different(passwordEncoder,
                passwordEncoder.apply("user")));
        changePasswordDialog.addRule(PasswordRule.notPreviousOf(passwordEncoder,
                passwordEncoder.apply("first"),
                passwordEncoder.apply("second"),
                passwordEncoder.apply("third")));
        changePasswordDialog.addRule(PasswordRule.strengthOf(ChangePassword.PasswordStrengthLevel.STRONG, this::onRuling));
        changePasswordDialog.setScorer(this::onScoring);
        changePasswordDialog.addOkListener(e -> Notification.show(String.format("New password is \"%s\"", e.getDesiredPassword())));
        changePasswordDialog.open();
    }

    private ChangePassword.PasswordStrengthLevel onRuling(String desiredPassword) {
        var strength = zxcvbn.measure(desiredPassword);
        return guessesToPasswordStrengthLevel(strength.getGuesses());
    }

    private ChangePassword.PasswordStrength onScoring(String desiredPassword) {
        var strength = zxcvbn.measure(desiredPassword);
        return new ChangePassword.PasswordStrength(guessesToPasswordStrengthLevel(strength.getGuesses()),
                String.format("Could take %s to crack. %s",
                        strength.getCrackTimesDisplay().getOfflineSlowHashing1e4perSecond(),
                        strength.getFeedback().getWarning()));
    }

    private static ChangePassword.PasswordStrengthLevel guessesToPasswordStrengthLevel(double guesses) {
        final double DELTA = 5;
        var guessesDelta = guesses - DELTA;

        if (guessesDelta < 1e3) {
            return ChangePassword.PasswordStrengthLevel.VERY_WEAK;
        }
        else if (guessesDelta < 1e6) {
            return ChangePassword.PasswordStrengthLevel.WEAK;
        }
        else if (guessesDelta < 1e9) {
            return ChangePassword.PasswordStrengthLevel.MEDIOCRE;
        }
        else if (guessesDelta < 1e12) {
            return ChangePassword.PasswordStrengthLevel.STRONG;
        }
        else {
            return ChangePassword.PasswordStrengthLevel.VERY_STRONG;
        }
    }
}
