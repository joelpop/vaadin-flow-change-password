package org.vaadin.addons.joelpop.changepassword;

import org.vaadin.addons.joelpop.changepassword.ChangePassword.PasswordStrengthLevel;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A description of a rule and its associated test.
 * <p>
 * Used to test user IDs and passwords for classes implementing {@link ChangePassword}.
 * The description is used to display next to an indicator
 * showing if the supplied text satisfies the rule test.
 * </p>
 *
 */
public class ChangePasswordRule {
    private final String description;
    private final Function<String, Boolean> test;

    /**
     * Create a rule.
     * <p>
     *     This is how you create a custom rule. See examples in the predefined factory rules.
     * </p>
     * @param description the displayed description of the rule
     * @param test the rule function applied to the text being tested
     */
    public ChangePasswordRule(String description, Function<String, Boolean> test) {
        this.description = description;
        this.test = test;
    }

    /**
     * Return the displayed description of the rule.
     *
     * @return the displayed description of the rule
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the result of applying the rule function to the text being tested.
     *
     * @param text the text being tested
     * @return the result of applying the rule function to the text being tested
     */
    public boolean isSatisfiedBy(String text) {
        return test.apply(text);
    }


    /**
     * A rule with a US English description
     * requiring text to be at least {@code minLength} characters long.
     *
     * @param minLength the minimum number of characters for the text
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule length(int minLength) {
        return length(minLength, String.format("At least %d characters long", minLength));
    }

    /**
     * A rule requiring text to be at least {@code minLength} characters long.
     *
     * @param minLength the minimum number of characters for the text
     * @param description the displayed description of the rule
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule length(int minLength, String description) {
        return new ChangePasswordRule(description,
                text -> text.length() >= minLength);
    }


    /**
     * A rule with a US English description
     * requiring text to be at least {@code minLength} but no more than
     * {@code maxLength} characters long.
     *
     * @param minLength the minimum number of characters for the text
     * @param maxLength the maximum number of characters for the text
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule length(int minLength, int maxLength) {
        return length(minLength, maxLength, String.format("Between %d and %d characters long", minLength, maxLength));
    }

    /**
     * A rule requiring text to be at least {@code minLength} but no more than
     * {@code maxLength} characters long.
     *
     * @param minLength the minimum number of characters for the text
     * @param maxLength the maximum number of characters for the text
     * @param description the displayed description of the rule
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule length(int minLength, int maxLength, String description) {
        return new ChangePasswordRule(description,
                text -> {
                    var length = text.length();
                    return (length >= minLength) && (length <= maxLength);
                });
    }


    /**
     * A rule with a US English description
     * requiring text to start with a letter.
     *
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule startWithLetter() {
        return startWithLetter("Must start with a letter");
    }

    /**
     * A rule requiring text to start with a letter.
     *
     * @param description the displayed description of the rule
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule startWithLetter(String description) {
        return new ChangePasswordRule(description,
                text -> text.matches("\\p{Alpha}.*"));
    }


    /**
     * A rule with a US English description
     * requiring text to contain at least {@code count} uppercase letters.
     *
     * @param count the minimum number of uppercase letters required in the text
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule hasUppercaseLetters(int count) {
        return hasUppercaseLetters(count, String.format("At least %d uppercase letter%s", count, count == 1 ? "" : "s"));
    }

    /**
     * A rule requiring text to contain at least {@code count} uppercase letters.
     *
     * @param count the minimum number of uppercase letters required in the text
     * @param description the displayed description of the rule
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule hasUppercaseLetters(int count, String description) {
        return new ChangePasswordRule(description,
                text -> text.replaceAll("[^\\p{Upper}]", "").length() >= count);
    }


    /**
     * A rule with a US English description
     * requiring text to contain at least {@code count} lowercase letters.
     *
     * @param count the minimum number of lowercase letters required in the text
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule hasLowercaseLetters(int count) {
        return hasLowercaseLetters(count, String.format("At least %d lowercase letter%s", count, count == 1 ? "" : "s"));
    }

    /**
     * A rule requiring text to contain at least {@code count} lowercase letters.
     *
     * @param count the minimum number of lowercase letters required in the text
     * @param description the displayed description of the rule
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule hasLowercaseLetters(int count, String description) {
        return new ChangePasswordRule(description,
                text -> text.replaceAll("[^\\p{Lower}]", "").length() >= count);
    }


    /**
     * A rule with a US English description
     * requiring text to contain at least {@code count} digits.
     *
     * @param count the minimum number of digits required in the text
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule hasDigits(int count) {
        return hasDigits(count, String.format("At least %d digit%s", count, count == 1 ? "" : "s"));
    }

    /**
     * A rule requiring text to contain at least {@code count} digits.
     *
     * @param count the minimum number of digits required in the text
     * @param description the displayed description of the rule
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule hasDigits(int count, String description) {
        return new ChangePasswordRule(description,
                text -> text.replaceAll("[^\\p{Digit}]", "").length() >= count);
    }


    /**
     * A rule with a US English description
     * requiring text to contain at least {@code count} special characters.
     *
     * @param count the minimum number of special characters required in the text
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule hasSpecials(int count) {
        return hasSpecials(count, String.format("At least %d special character%s", count, count == 1 ? "" : "s"));
    }

    /**
     * A rule requiring text to contain at least {@code count} special characters.
     *
     * @param count the minimum number of special characters required in the text
     * @param description the displayed description of the rule
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule hasSpecials(int count, String description) {
        return new ChangePasswordRule(description,
                text -> text.replaceAll("[\\p{Upper}\\p{Lower}\\p{Digit}]", "").length() >= count);
    }


    /**
     * A rule with a US English description
     * requiring text to contain at least {@code count} of the specified characters.
     *
     * @param count the minimum number of the specified characters required in the text
     * @param specifieds the specified characters
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule hasSpecifieds(int count, CharSequence specifieds) {
        return hasSpecifieds(count, specifieds, String.format("At least %d character%s from: %s", count, count == 1 ? "" : "s", specifieds));
    }

    /**
     * A rule requiring text to contain at least {@code count} of the specified characters.
     *
     * @param count the minimum number of the specified characters required in the text
     * @param specifieds the specified characters
     * @param description the displayed description of the rule
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule hasSpecifieds(int count, CharSequence specifieds, String description) {
        var specifiedSet = specifieds.chars().boxed()
                .map(i -> (char) i.intValue())
                .collect(Collectors.toSet());
        return new ChangePasswordRule(description,
                text -> text.chars().boxed()
                        .map(i -> (char) i.intValue())
                        .filter(specifiedSet::contains)
                        .count() >= count);
    }


    /**
     * A rule with a US English description
     * requiring text to contain characters from at least {@code count} of the character groups
     * uppercase, lowercase, digits, and specials.
     *
     * @param count the minimum number of character groups contained in the text
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule hasCharacterGroups(int count) {
        return hasCharacterGroups(count,
                String.format("Characters from at least %d of the groups: uppercase, lowercase, digits, & specials", count));
    }

    /**
     * A rule requiring text to contain characters from at least {@code count} of the character groups
     * uppercase, lowercase, digits, and specials.
     *
     * @param count the minimum number of character groups contained in the text
     * @param description the displayed description of the rule
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule hasCharacterGroups(int count, String description) {
        return new ChangePasswordRule(description,
                text ->
                        (Pattern.compile("\\p{Upper}").matcher(text).find() ? 1 : 0) +
                        (Pattern.compile("\\p{Lower}").matcher(text).find() ? 1 : 0) +
                        (Pattern.compile("\\p{Digit}").matcher(text).find() ? 1 : 0) +
                        (Pattern.compile("[^\\p{Upper}\\p{Lower}\\p{Digit}]").matcher(text).find() ? 1 : 0)  >= count);
    }


    /**
     * A rule with a US English description
     * requiring text, when encoded, to be different from the supplied encoded text.
     *
     * @param encoder the encoder to transform text into encoded form
     * @param encodedText already encoded text the text must not match
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule different(Function<CharSequence, String> encoder,
                                               String encodedText) {
        return different(encoder, encodedText, "Different from current password");
    }

    /**
     * A rule requiring text, when encoded, to be different from the supplied encoded text.
     *
     * @param encoder the encoder to transform text into encoded form
     * @param encodedText already encoded text the text must not match
     * @param description the displayed description of the rule
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule different(Function<CharSequence, String> encoder,
                                               String encodedText, String description) {
        return new ChangePasswordRule(description,
                text -> !Objects.equals(encoder.apply(text), encodedText));
    }


    /**
     * A rule with a US English description
     * requiring text, when encoded, to be different from the supplied encoded texts.
     *
     * @param encoder the encoder to transform text into encoded form
     * @param previousEncodedTexts already encoded texts the text must not match
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule notPreviousOf(Function<CharSequence, String> encoder,
                                                   String... previousEncodedTexts) {
        return notPreviousOf(String.format("Not any of %d previous passwords", previousEncodedTexts.length),
                encoder, previousEncodedTexts);
    }

    /**
     * A rule requiring text, when encoded, to be different from the supplied encoded texts.
     * <p>
     *     Note, the description parameter is first for this method due to the String vararg parameter.
     * </p>
     *
     * @param description the displayed description of the rule
     * @param encoder the encoder to transform text into encoded form
     * @param previousEncodedTexts already encoded texts the text must not match
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule notPreviousOf(String description, Function<CharSequence, String> encoder,
                                                   String... previousEncodedTexts) {
        var encodedPasswords = Set.of(previousEncodedTexts);
        return new ChangePasswordRule(description,
                text -> !encodedPasswords.contains(encoder.apply(text)));
    }


    /**
     * A rule with the {@link ChangePasswordPanel.ChangePasswordI18n}{@code ::getPasswordStrengthLevelCaption} description
     * requiring text to be at least {@code strengthLevel} when scored by the supplied {@code scorer}.
     *
     * @param strengthLevel the minimum strength level score for text
     * @param changePasswordI18n the strength level descriptions
     * @param scorer the password strength level scorer
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule strengthOf(PasswordStrengthLevel strengthLevel,
                                                ChangePasswordPanel.ChangePasswordI18n changePasswordI18n,
                                                Function<String, PasswordStrengthLevel> scorer) {
        return strengthOf(strengthLevel, scorer,
                String.format("Minimum strength of %s", changePasswordI18n.getPasswordStrengthLevelCaption(strengthLevel)));
    }

    /**
     * A rule requiring text to be at least {@code strengthLevel} when scored by the supplied {@code scorer}.
     *
     * @param strengthLevel the minimum strength level score for text
     * @param scorer the password strength level scorer
     * @param description the displayed description of the rule
     * @return {@code true} if the text satisfies the rule, {@code false} otherwise
     */
    public static ChangePasswordRule strengthOf(PasswordStrengthLevel strengthLevel,
                                                Function<String, PasswordStrengthLevel> scorer, String description) {
        return new ChangePasswordRule(description,
                text -> scorer.apply(text).compareTo(strengthLevel) >= 0);
    }

}
