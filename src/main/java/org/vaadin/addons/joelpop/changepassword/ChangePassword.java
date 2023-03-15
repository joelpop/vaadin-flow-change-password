package org.vaadin.addons.joelpop.changepassword;

import com.vaadin.flow.component.Component;

import java.util.List;
import java.util.function.Function;

/**
 * An API for changing, resetting, or establishing a password.
 *
 * <p>
 *     This API is used by both {@link ChangePasswordPanel} and {@link ChangePasswordDialog}.
 * </p>
 */
public interface ChangePassword {

    /**
     * Return the change password type of the component.
     * <p>
     *     The type controls which input fields are visible
     *     and if the user ID field (if visible) is read-only or not.
     * </p>
     *
     * @return the change password type of the component
     */
    ChangePasswordType getChangePasswordType();

    /**
     * Set the change password type of the component.
     * <p>
     *     The type controls which input fields are visible
     *     and if the user ID field (if visible) is read-only or not.
     * </p>
     *
     * @param changePasswordType the change password type to set for the component
     */
    void setChangePasswordType(ChangePasswordType changePasswordType);

    /**
     * Clears all the input field values.
     */
    void reset();

    /**
     * Return the value of the user ID field.
     *
     * @return the value of the user ID field
     */
    String getUserid();

    /**
     * Set the value of the user ID field.
     *
     * @param userid the user ID value
     */
    void setUserid(String userid);

    /**
     * Return the value of the current password field.
     *
     * @return the value of the current password field
     */
    String getCurrentPassword();

    /**
     * Return the value of the desired password field.
     *
     * @return the value of the desired password field
     */
    String getDesiredPassword();

    /**
     * Set the text to display in the information block.
     *
     * @param infoText the text to display in the information block
     */
    void setInfoText(String infoText);

    /**
     * Set the component to display in the information block.
     *
     * @param infoTextComponent the component to display in the information block
     */
    void setInfoText(Component infoTextComponent);

    /**
     * Return the user ID rules.
     *
     * @return the user ID rules
     */
    List<ChangePasswordRule> getUseridRules();

    /**
     * Set the user ID rules. Removes any rules already set.
     *
     * @param rules the user ID rules to set
     */
    void setUseridRules(ChangePasswordRule... rules);

    /**
     * Add to the user ID rules.
     *
     * @param rules the user ID rules to add
     */
    void addUseridRules(ChangePasswordRule... rules);

    /**
     * Return the password rules.
     *
     * @return the password rules
     */
    List<ChangePasswordRule> getPasswordRules();

    /**
     * Set the password rules. Removes any rules already set.
     *
     * @param rules the password rules to set
     */
    void setPasswordRules(ChangePasswordRule... rules);

    /**
     * Add to the password rules.
     *
     * @param rules the password rules to add
     */
    void addPasswordRules(ChangePasswordRule... rules);

    /**
     * Set the password strength scorer to use to display the level and feedback message of the meter.
     *
     * @param scorer the password strength scorer
     */
    void setScorer(Function<String, PasswordStrength> scorer);

    /**
     * Return the validation of the component.
     *
     * @return {@code true} if all fields are valid, {@code false} otherwise
     */
    boolean isValid();


    /**
     * The type of the password change to make.
     * <p>
     *     The type controls which input fields are visible
     *     and if the user ID field (if visible) is read-only or not.
     * </p>
     */
    enum ChangePasswordType {
        /**
         * Use when the current password is not known.
         * <p>
         * Displays the desired and confirm password fields.
         * </p>
         */
        CHANGE_FORGOTTEN,

        /**
         * Use when the current password is known.
         * <p>
         * Displays the known, desired, and confirm password fields.
         * </p>
         */
        CHANGE_KNOWN,

        /**
         * Use when there is no user ID nor current password.
         * <p>
         * Displays the userid, desired, and confirm password fields.
         * </p>
         */
        ESTABLISH_NEW
    }


    /**
     * The strength level of the password.
     */
    enum PasswordStrengthLevel {
        VERY_WEAK("#FF1F1f"),
        WEAK("#FFBF00"),
        MEDIOCRE("#EFEF00"),
        STRONG("#1FFF1f"),
        VERY_STRONG("#00BF00");

        private final String color;

        PasswordStrengthLevel(String color) {
            this.color = color;
        }

        public String getColor() {
            return this.color;
        }
    }


    /**
     * The strength of the password expressed as a level and a feedback message.
     */
    class PasswordStrength {
        private final PasswordStrengthLevel passwordStrengthLevel;
        private final String feedback;

        public PasswordStrength(PasswordStrengthLevel passwordStrengthLevel,
                                String feedback) {
            this.passwordStrengthLevel = passwordStrengthLevel;
            this.feedback = feedback;
        }

        public PasswordStrengthLevel getPasswordStrengthLevel() {
            return passwordStrengthLevel;
        }

        public String getFeedback() {
            return feedback;
        }
    }

}
