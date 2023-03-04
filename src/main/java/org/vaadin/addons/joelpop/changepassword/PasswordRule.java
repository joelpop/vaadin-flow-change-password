package org.vaadin.addons.joelpop.changepassword;

import org.vaadin.addons.joelpop.changepassword.ChangePassword.PasswordStrengthLevel;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;

public class PasswordRule {
    private final String text;
    private final Function<String, Boolean> rule;

    public PasswordRule(String text, Function<String, Boolean> rule) {
        this.text = text;
        this.rule = rule;
    }

    public String getText() {
        return text;
    }

    public boolean isSatisfiedBy(String password) {
        return rule.apply(password);
    }

    public static PasswordRule hasUppercaseLetters(int count) {
        return new PasswordRule("At least %d uppercase letter%s".formatted(count, count == 1 ? "" : "s"),
                password -> password.replaceAll("[^\\p{Upper}]", "").length() >= count);
    }

    public static PasswordRule hasLowercaseLetters(int count) {
        return new PasswordRule("At least %d lowercase letter%s".formatted(count, count == 1 ? "" : "s"),
                password -> password.replaceAll("[^\\p{Lower}]", "").length() >= count);
    }

    public static PasswordRule hasDigits(int count) {
        return new PasswordRule("At least %d digit%s".formatted(count, count == 1 ? "" : "s"),
                password -> password.replaceAll("[^\\p{Digit}]", "").length() >= count);
    }

    public static PasswordRule hasSpecials(int count) {
        return new PasswordRule("At least %d special character%s".formatted(count, count == 1 ? "" : "s"),
                password -> password.replaceAll("[\\p{Upper}\\p{Lower}\\p{Digit}]", "").length() >= count);
    }

    public static PasswordRule hasCharacterGroups(int count) {
        return new PasswordRule("Characters from at least %d of the groups: uppercase, lowercase, digits, & specials".formatted(count),
                password ->
                        (Pattern.compile("\\p{Upper}").matcher(password).find() ? 1 : 0) +
                        (Pattern.compile("\\p{Lower}").matcher(password).find() ? 1 : 0) +
                        (Pattern.compile("\\p{Digit}").matcher(password).find() ? 1 : 0) +
                        (Pattern.compile("[^\\p{Upper}\\p{Lower}\\p{Digit}]").matcher(password).find() ? 1 : 0)  >= count);
    }

    public static PasswordRule different(Function<CharSequence, String> passwordEncoder,
                                         String currentEncodedPassword) {
        return new PasswordRule("Different from current password",
                password -> !Objects.equals(passwordEncoder.apply(password), currentEncodedPassword));
    }

    public static PasswordRule length(int minLength) {
        return new PasswordRule("At least %d characters long".formatted(minLength),
                password -> password.length() >= minLength);
    }

    public static PasswordRule length(int minLength, int maxLength) {
        return new PasswordRule("Between %d and %d characters long".formatted(minLength, maxLength),
                password -> {
                    var length = password.length();
                    return (length >= minLength) && (length <= maxLength);
                });
    }

    public static PasswordRule notPreviousOf(Function<CharSequence, String> passwordEncoder,
                                             String... previousEncodedPasswords) {
        var encodedPasswords = Set.of(previousEncodedPasswords);
        return new PasswordRule("Not any of %d previous passwords".formatted(encodedPasswords.size()),
                password -> !encodedPasswords.contains(passwordEncoder.apply(password)));
    }

    public static PasswordRule strengthOf(PasswordStrengthLevel strengthLevel, Function<String, PasswordStrengthLevel> scorer) {
        return new PasswordRule("Minimum strength of %s".formatted(strengthLevel.getCaption()),
                password -> scorer.apply(password).compareTo(strengthLevel) >= 0);
    }

}
