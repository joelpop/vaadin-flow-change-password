package org.vaadin.addons.joelpop.changepassword;

import org.vaadin.addons.joelpop.changepassword.ChangePasswordPanel.PasswordStrengthLevel;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ChangePasswordRule {
    private final String text;
    private final Function<String, Boolean> rule;

    public ChangePasswordRule(String text, Function<String, Boolean> rule) {
        this.text = text;
        this.rule = rule;
    }

    public String getText() {
        return text;
    }

    public boolean isSatisfiedBy(String password) {
        return rule.apply(password);
    }


    public static ChangePasswordRule startWithLetter() {
        return startWithLetter("Must start with a letter");
    }

    public static ChangePasswordRule startWithLetter(String text) {
        return new ChangePasswordRule(text,
                password -> password.matches("\\p{Alpha}.*"));
    }


    public static ChangePasswordRule hasUppercaseLetters(int count) {
        return hasUppercaseLetters(count, String.format("At least %d uppercase letter%s", count, count == 1 ? "" : "s"));
    }

    public static ChangePasswordRule hasUppercaseLetters(int count, String text) {
        return new ChangePasswordRule(text,
                password -> password.replaceAll("[^\\p{Upper}]", "").length() >= count);
    }


    public static ChangePasswordRule hasLowercaseLetters(int count) {
        return hasLowercaseLetters(count, String.format("At least %d lowercase letter%s", count, count == 1 ? "" : "s"));
    }

    public static ChangePasswordRule hasLowercaseLetters(int count, String text) {
        return new ChangePasswordRule(text,
                password -> password.replaceAll("[^\\p{Lower}]", "").length() >= count);
    }


    public static ChangePasswordRule hasDigits(int count) {
        return hasDigits(count, String.format("At least %d digit%s", count, count == 1 ? "" : "s"));
    }

    public static ChangePasswordRule hasDigits(int count, String text) {
        return new ChangePasswordRule(text,
                password -> password.replaceAll("[^\\p{Digit}]", "").length() >= count);
    }


    public static ChangePasswordRule hasSpecials(int count) {
        return hasSpecials(count, String.format("At least %d special character%s", count, count == 1 ? "" : "s"));
    }

    public static ChangePasswordRule hasSpecials(int count, String text) {
        return new ChangePasswordRule(text,
                password -> password.replaceAll("[\\p{Upper}\\p{Lower}\\p{Digit}]", "").length() >= count);
    }


    public static ChangePasswordRule hasCharacterGroups(int count) {
        return hasCharacterGroups(count,
                String.format("Characters from at least %d of the groups: uppercase, lowercase, digits, & specials", count));
    }

    public static ChangePasswordRule hasCharacterGroups(int count, String text) {
        return new ChangePasswordRule(text,
                password ->
                        (Pattern.compile("\\p{Upper}").matcher(password).find() ? 1 : 0) +
                        (Pattern.compile("\\p{Lower}").matcher(password).find() ? 1 : 0) +
                        (Pattern.compile("\\p{Digit}").matcher(password).find() ? 1 : 0) +
                        (Pattern.compile("[^\\p{Upper}\\p{Lower}\\p{Digit}]").matcher(password).find() ? 1 : 0)  >= count);
    }


    public static ChangePasswordRule different(Function<CharSequence, String> passwordEncoder,
                                               String currentEncodedPassword) {
        return different(passwordEncoder, currentEncodedPassword, "Different from current password");
    }

    public static ChangePasswordRule different(Function<CharSequence, String> passwordEncoder,
                                               String currentEncodedPassword, String text) {
        return new ChangePasswordRule(text,
                password -> !Objects.equals(passwordEncoder.apply(password), currentEncodedPassword));
    }


    public static ChangePasswordRule length(int minLength) {
        return length(minLength, String.format("At least %d characters long", minLength));
    }

    public static ChangePasswordRule length(int minLength, String text) {
        return new ChangePasswordRule(text,
                password -> password.length() >= minLength);
    }


    public static ChangePasswordRule length(int minLength, int maxLength) {
        return length(minLength, maxLength, String.format("Between %d and %d characters long", minLength, maxLength));
    }

    public static ChangePasswordRule length(int minLength, int maxLength, String text) {
        return new ChangePasswordRule(text,
                password -> {
                    var length = password.length();
                    return (length >= minLength) && (length <= maxLength);
                });
    }


    public static ChangePasswordRule notPreviousOf(Function<CharSequence, String> passwordEncoder,
                                                   String... previousEncodedPasswords) {
        return notPreviousOf(String.format("Not any of %d previous passwords", previousEncodedPasswords.length),
                passwordEncoder, previousEncodedPasswords);
    }

    public static ChangePasswordRule notPreviousOf(String text, Function<CharSequence, String> passwordEncoder,
                                                   String... previousEncodedPasswords) {
        var encodedPasswords = Set.of(previousEncodedPasswords);
        return new ChangePasswordRule(text,
                password -> !encodedPasswords.contains(passwordEncoder.apply(password)));
    }


    public static ChangePasswordRule strengthOf(PasswordStrengthLevel strengthLevel,
                                                ChangePasswordPanel.ChangePasswordI18n changePasswordI18n,
                                                Function<String, PasswordStrengthLevel> scorer) {
        return strengthOf(strengthLevel, scorer,
                String.format("Minimum strength of %s", changePasswordI18n.getPasswordStrengthLevelCaption(strengthLevel)));
    }

    public static ChangePasswordRule strengthOf(PasswordStrengthLevel strengthLevel,
                                                Function<String, PasswordStrengthLevel> scorer, String text) {
        return new ChangePasswordRule(text,
                password -> scorer.apply(password).compareTo(strengthLevel) >= 0);
    }

}
