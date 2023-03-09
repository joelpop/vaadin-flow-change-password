package org.vaadin.addons.joelpop.changepassword;

import com.vaadin.flow.component.AbstractField.ComponentValueChangeEvent;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A panel for changing, resetting, or establishing a password.
 *
 * <pre>
 * +-content(HorizontalLayout)---------------------------------------------------------------+
 * | +-credentialBlock(VerticalLayout)---+ +-helperBlock(VerticalLayout)-------------------+ |
 * | | User ID                           | | +-infoBlock(Div)----------------------------+ | |
 * | | +-useridTextField---------------+ | | |                                           | | |
 * | | |                               | | | |                                           | | |
 * | | +-------------------------------+ | | +-------------------------------------------+ | |
 *   | Current Password                  | | +-useridComplexityBlock(VerticalLayout)-----+ | |
 * | | +-currentPasswordField----------+ | | | User ID Rules                             | | |
 * | | |                               | | | | +-useridRuleBlock(VerticalLayout)-------+ | | |
 * | | +-------------------------------+ | | | | ( ) Must start with a letter          | | | |
 * | | New Password                      | | | | ( ) Between 10 and 20 characters long | | | |
 * | | +-desiredPasswordField----------+ | | | | ( ) Only letters, digits, and dots    | | | |
 * | | |                               | | | | +---------------------------------------+ | | |
 * | | +-------------------------------+ | | +-------------------------------------------+ | |
 * | | Confirm Password                  | | +-passwordComplexityBlock(VerticalLayout)---+ | |
 * | | +-confirmPasswordField----------+ | | | Password Complexity Rules                 | | |
 * | | |                               | | | | +-passwordRuleBlock(VerticalLayout)-----+ | | |
 * | | +-------------------------------+ | | | | ( ) Between 10 and 20 characters long | | | |
 * | +-----------------------------------+ | | | ( ) At least 1 lowercase letter       | | | |
 * |                                       | | | ( ) At least 1 uppercase letter       | | | |
 * |                                       | | | ( ) At least 1 digit                  | | | |
 * |                                       | | | ( ) At least 1 special character      | | | |
 * |                                       | | | ( ) Different from current password   | | | |
 * |                                       | | +---------------------------------------+ | | |
 * |                                       | +-------------------------------------------+ | |
 * |                                       | +-strengthMeter-----------------------------+ | |
 * |                                       | | Password Strength                         | | |
 * |                                       | | [#]  [ ]  [ ]  [ ]  [ ]  Very Weak        | | |
 * |                                       | | Could take 0 seconds to crack.            | | |
 * |                                       | +-------------------------------------------+ | |
 * |                                       +-----------------------------------------------+ |
 * +-----------------------------------------------------------------------------------------+
 * </pre>
 */
@CssImport("./change-password/styles/change-password.css")
public class ChangePasswordPanel extends Composite<HorizontalLayout> {
    public static final String HEADING_CLASS_NAME = "change-password-heading";

    private transient ChangePasswordType changePasswordType;
    private transient ChangePasswordI18n changePasswordI18n;

    private final TextField useridTextField;
    private final PasswordField currentPasswordField;
    private final PasswordField desiredPasswordField;
    private final PasswordField confirmPasswordField;
    private final VerticalLayout helpBlock;
    private final Div infoBlock;
    private final Span useridComplexityLabelSpan;
    private final VerticalLayout useridComplexityBlock;
    private final VerticalLayout useridRuleBlock;
    private final Span passwordComplexityLabelSpan;
    private final VerticalLayout passwordComplexityBlock;
    private final VerticalLayout passwordRuleBlock;
    private final StrengthMeter strengthMeter;

    private final transient Credentials credentials;
    private final Binder<Credentials> credentialsBinder;

    private final transient List<RuleItem> useridRuleItems;
    private final transient List<RuleItem> passwordRuleItems;
    private transient Function<String, PasswordStrength> scorer;

    public ChangePasswordPanel() {
        this(ChangePasswordType.CHANGE_KNOWN, new ChangePasswordI18n());
    }

    public ChangePasswordPanel(ChangePasswordType changePasswordType) {
        this(changePasswordType, new ChangePasswordI18n());
    }

    public ChangePasswordPanel(ChangePasswordI18n changePasswordI18n) {
        this(ChangePasswordType.CHANGE_KNOWN, changePasswordI18n);
    }


    public ChangePasswordPanel(ChangePasswordType changePasswordType, ChangePasswordI18n changePasswordI18n) {
        this.changePasswordI18n = changePasswordI18n;

        useridTextField = new TextField(changePasswordI18n.getUserIdLabel());
        useridTextField.setWidthFull();
        useridTextField.setValueChangeMode(ValueChangeMode.EAGER);
        useridTextField.addValueChangeListener(this::onUseridChange);

        currentPasswordField = new PasswordField(changePasswordI18n.getCurrentPasswordLabel());
        currentPasswordField.setWidthFull();
        currentPasswordField.setValueChangeMode(ValueChangeMode.EAGER);

        desiredPasswordField = new PasswordField(changePasswordI18n.getDesiredPasswordLabel());
        desiredPasswordField.setWidthFull();
        desiredPasswordField.setValueChangeMode(ValueChangeMode.EAGER);
        desiredPasswordField.addValueChangeListener(this::onDesiredPasswordChange);

        confirmPasswordField = new PasswordField(changePasswordI18n.getConfirmPasswordLabel());
        confirmPasswordField.setWidthFull();
        confirmPasswordField.setValueChangeMode(ValueChangeMode.EAGER);

        var credentialBlock = new VerticalLayout();
        credentialBlock.setWidth(15f, Unit.EM);
        credentialBlock.setPadding(false);
        credentialBlock.setSpacing(false);
        credentialBlock.add(useridTextField);
        credentialBlock.add(currentPasswordField);
        credentialBlock.add(desiredPasswordField);
        credentialBlock.add(confirmPasswordField);

        infoBlock = new Div();
        infoBlock.setVisible(false);

        useridComplexityLabelSpan = new Span(changePasswordI18n.getUseridComplexityLabel());
        useridComplexityLabelSpan.addClassNames(HEADING_CLASS_NAME);

        useridRuleBlock = new VerticalLayout();
        useridRuleBlock.setPadding(false);
        useridRuleBlock.setSpacing(false);

        useridComplexityBlock = new VerticalLayout();
        useridComplexityBlock.setVisible(false);
        useridComplexityBlock.setPadding(false);
        useridComplexityBlock.setSpacing(false);
        useridComplexityBlock.add(useridComplexityLabelSpan);
        useridComplexityBlock.add(useridRuleBlock);

        passwordComplexityLabelSpan = new Span(changePasswordI18n.getPasswordComplexityLabel());
        passwordComplexityLabelSpan.addClassNames(HEADING_CLASS_NAME);

        passwordRuleBlock = new VerticalLayout();
        passwordRuleBlock.setPadding(false);
        passwordRuleBlock.setSpacing(false);

        passwordComplexityBlock = new VerticalLayout();
        passwordComplexityBlock.setVisible(false);
        passwordComplexityBlock.setPadding(false);
        passwordComplexityBlock.setSpacing(false);
        passwordComplexityBlock.add(passwordComplexityLabelSpan);
        passwordComplexityBlock.add(passwordRuleBlock);

        strengthMeter = new StrengthMeter(changePasswordI18n);
        strengthMeter.setVisible(false);

        helpBlock = new VerticalLayout();
        helpBlock.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.LineHeight.SMALL);
        helpBlock.setWidth(20f, Unit.EM);
        helpBlock.setVisible(false);
        helpBlock.setPadding(false);
        helpBlock.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        helpBlock.add(infoBlock);
        helpBlock.add(useridComplexityBlock);
        helpBlock.add(passwordComplexityBlock);
        helpBlock.add(strengthMeter);

        var content = getContent();
        content.add(credentialBlock);
        content.add(helpBlock);

        credentials = new Credentials();
        useridRuleItems = new ArrayList<>();
        passwordRuleItems = new ArrayList<>();

        credentialsBinder = new Binder<>();

        credentialsBinder.forField(useridTextField)
                .asRequired(requiredValidator(useridTextField))
                .withValidator(this::useridRuleValidator)
                .withNullRepresentation("")
                .bind(Credentials::getUserId, Credentials::setUserId);

        credentialsBinder.forField(currentPasswordField)
                .asRequired(requiredValidator(currentPasswordField))
                .withNullRepresentation("")
                .bind(Credentials::getCurrent, Credentials::setCurrent);

        credentialsBinder.forField(desiredPasswordField)
                .asRequired(requiredValidator(desiredPasswordField))
                .withValidator(this::passwordRuleValidator)
                .withNullRepresentation("")
                .bind(Credentials::getDesired, Credentials::setDesired);

        credentialsBinder.forField(confirmPasswordField)
                .asRequired(requiredValidator(confirmPasswordField))
                .withValidator(this::passwordsMatchValidator)
                .withNullRepresentation("")
                .bind(Credentials::getConfirm, Credentials::setConfirm);

        credentialsBinder.setBean(credentials);

        setChangePasswordType(changePasswordType);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        if (useridTextField.isVisible() && !useridTextField.isReadOnly()) {
            useridTextField.focus();
        }
        else if (currentPasswordField.isVisible()) {
            currentPasswordField.focus();
        }
        else {
            desiredPasswordField.focus();
        }
    }

    private void updateHelpBlockVisibility() {
        helpBlock.setVisible(infoBlock.isVisible() || useridComplexityBlock.isVisible() || passwordComplexityBlock.isVisible() || strengthMeter.isVisible());
    }

    private Validator<String> requiredValidator(HasLabel hasLabel) {
        return (value, valueContext) -> {
            if (!valueContext.getComponent()
                    .map(Component::isVisible)
                    .orElse(true)) {
                return ValidationResult.ok();
            }
            if (valueContext.getHasValue()
                    .map(HasValue::isReadOnly)
                    .orElse(false)) {
                return ValidationResult.ok();
            }
            if (Optional.ofNullable(value)
                    .map(v -> !v.isBlank())
                    .orElse(false)) {
                return ValidationResult.ok();
            }
            return ValidationResult.error(String.format(changePasswordI18n.getRequiredFieldMessageFormat(), hasLabel.getLabel()));
        };
    }

    private ValidationResult useridRuleValidator(String userid, ValueContext valueContext) {
        if (useridRuleItems.stream()
                    .filter(ruleItem -> ruleItem.isSatisfiedBy(userid))
                    .count() == useridRuleItems.size()) {
            return ValidationResult.ok();
        }
        else {
            return ValidationResult.error(changePasswordI18n.getUseridInvalidMessage());
        }
    }

    private ValidationResult passwordRuleValidator(String desiredPassword, ValueContext valueContext) {
        if (passwordRuleItems.stream()
                    .filter(ruleItem -> ruleItem.isSatisfiedBy(desiredPassword))
                    .count() == passwordRuleItems.size()) {
            return ValidationResult.ok();
        }
        else {
            return ValidationResult.error(changePasswordI18n.getPasswordInvalidMessage());
        }
    }

    private ValidationResult passwordsMatchValidator(String confirmPassword, ValueContext valueContext) {
        return Objects.equals(confirmPassword, desiredPasswordField.getValue())
                ? ValidationResult.ok()
                : ValidationResult.error(changePasswordI18n.getMismatchedPasswordsMessage());
    }

    public ChangePasswordType getChangePasswordType() {
        return changePasswordType;
    }

    public void setChangePasswordType(ChangePasswordType changePasswordType) {
        this.changePasswordType = changePasswordType;

        useridTextField.setVisible((changePasswordType == ChangePasswordType.ESTABLISH_NEW) ||
                                   (credentials.getUserId() != null));
        useridTextField.setReadOnly(changePasswordType != ChangePasswordType.ESTABLISH_NEW);
        useridTextField.setRequiredIndicatorVisible(!useridTextField.isReadOnly());
        currentPasswordField.setVisible(changePasswordType == ChangePasswordType.CHANGE_KNOWN);
    }

    public ChangePasswordI18n getChangePasswordI18n() {
        return changePasswordI18n;
    }

    public void setChangePasswordI18n(ChangePasswordI18n changePasswordI18n) {
        this.changePasswordI18n = changePasswordI18n;

        useridTextField.setLabel(changePasswordI18n.getUserIdLabel());
        currentPasswordField.setLabel(changePasswordI18n.getCurrentPasswordLabel());
        desiredPasswordField.setLabel(changePasswordI18n.getDesiredPasswordLabel());
        confirmPasswordField.setLabel(changePasswordI18n.getConfirmPasswordLabel());
        useridComplexityLabelSpan.setText(changePasswordI18n.getUseridComplexityLabel());
        passwordComplexityLabelSpan.setText(changePasswordI18n.getPasswordComplexityLabel());
        strengthMeter.setChangePasswordI18n(changePasswordI18n);
    }

    public void reset() {
        credentials.setUserId(null);
        credentials.setCurrent(null);
        credentials.setDesired(null);
        credentials.setConfirm(null);
        credentialsBinder.refreshFields();

        setChangePasswordType(changePasswordType);
    }

    public String getUserid() {
        return credentials.getUserId();
    }

    public void setUserid(String userid) {
        credentials.setUserId(userid);
        credentialsBinder.refreshFields();

        setChangePasswordType(changePasswordType);
    }

    public String getCurrentPassword() {
        return credentials.getCurrent();
    }

    public String getDesiredPassword() {
        return credentials.getDesired();
    }

    public void setInfoText(String infoText) {
        setInfoText(Optional.ofNullable(infoText)
                .map(Text::new)
                .orElse(null));
    }

    public void setInfoText(Component infoTextComponent) {
        infoBlock.removeAll();

        var visible = (infoTextComponent != null);
        if (visible) {
            infoBlock.add(infoTextComponent);
        }

        infoBlock.setVisible(visible);
        updateHelpBlockVisibility();
    }

    public List<ChangePasswordRule> getUseridRules() {
        return useridRuleItems.stream()
                .map(RuleItem::getRule)
                .collect(Collectors.toList());
    }

    public void setUseridRules(ChangePasswordRule... rules) {
        useridRuleItems.clear();
        useridRuleBlock.removeAll();

        addUseridRule(rules);
    }

    public void addUseridRule(ChangePasswordRule... rules) {
        Arrays.stream(rules)
                .map(RuleItem::new)
                .forEach(ruleItem -> {
                    useridRuleItems.add(ruleItem);
                    useridRuleBlock.add(ruleItem);
                });

        useridComplexityBlock.setVisible(!useridRuleItems.isEmpty());
        updateHelpBlockVisibility();
    }

    public List<ChangePasswordRule> getPasswordRules() {
        return passwordRuleItems.stream()
                .map(RuleItem::getRule)
                .collect(Collectors.toList());
    }

    public void setPasswordRules(ChangePasswordRule... rules) {
        passwordRuleItems.clear();
        passwordRuleBlock.removeAll();

        addPasswordRule(rules);
    }

    public void addPasswordRule(ChangePasswordRule... rules) {
        Arrays.stream(rules)
                .map(RuleItem::new)
                .forEach(ruleItem -> {
                    passwordRuleItems.add(ruleItem);
                    passwordRuleBlock.add(ruleItem);
                });

        passwordComplexityBlock.setVisible(!passwordRuleItems.isEmpty());
        updateHelpBlockVisibility();
    }

    public void setScorer(Function<String, PasswordStrength> scorer) {
        this.scorer = scorer;

        strengthMeter.setVisible(scorer != null);
        updateHelpBlockVisibility();
    }

    private void onUseridChange(ComponentValueChangeEvent<TextField, String> event) {
        var userid = event.getValue();
        var useridIsEmpty = userid.isEmpty();

        if (useridIsEmpty) {
            useridRuleValidator(userid, null);
        }
    }

    private void onDesiredPasswordChange(ComponentValueChangeEvent<PasswordField, String> event) {
        var desiredPassword = event.getValue();
        var desiredPasswordIsEmpty = desiredPassword.isEmpty();

        if (desiredPasswordIsEmpty) {
            passwordRuleValidator(desiredPassword, null);
        }
        if (scorer != null) {
            strengthMeter.setPasswordStrength(desiredPasswordIsEmpty ? null : scorer.apply(desiredPassword));
        }
    }

    public boolean isValid() {
        return credentialsBinder.validate().isOk();
    }


    public enum ChangePasswordType {
        ESTABLISH_NEW,
        CHANGE_FORGOTTEN,
        CHANGE_KNOWN
    }

    public static class ChangePasswordI18n {
        private String userIdLabel;
        private String currentPasswordLabel;
        private String desiredPasswordLabel;
        private String confirmPasswordLabel;
        private String useridComplexityLabel;
        private String passwordComplexityLabel;
        private String passwordStrengthLabel;
        private String requiredFieldMessageFormat;
        private String useridInvalidMessage;
        private String passwordInvalidMessage;
        private String mismatchedPasswordsMessage;
        private final Map<PasswordStrengthLevel, String> passwordStrengthLevelCaptions;

        public ChangePasswordI18n() {
            userIdLabel = "User ID";
            currentPasswordLabel = "Current Password";
            desiredPasswordLabel = "New Password";
            confirmPasswordLabel = "Confirm Password";
            useridComplexityLabel = "User ID Rules";
            passwordComplexityLabel = "Password Complexity Rules";
            passwordStrengthLabel = "Password Strength";
            requiredFieldMessageFormat = "%s is required.";
            useridInvalidMessage = "Desired user ID must satisfy all User ID Rules.";
            passwordInvalidMessage = "Desired password must satisfy all Password Complexity Rules.";
            mismatchedPasswordsMessage = "Passwords don't match.";
            passwordStrengthLevelCaptions = new EnumMap<>(Map.ofEntries(
                    Map.entry(PasswordStrengthLevel.VERY_WEAK, "Very Weak"),
                    Map.entry(PasswordStrengthLevel.WEAK ,"Weak"),
                    Map.entry(PasswordStrengthLevel.MEDIOCRE ,"Mediocre"),
                    Map.entry(PasswordStrengthLevel.STRONG ,"Strong"),
                    Map.entry(PasswordStrengthLevel.VERY_STRONG ,"Very Strong")));
        }

        public String getUserIdLabel() {
            return userIdLabel;
        }

        public void setUserIdLabel(String userIdLabel) {
            this.userIdLabel = userIdLabel;
        }

        public String getCurrentPasswordLabel() {
            return currentPasswordLabel;
        }

        public void setCurrentPasswordLabel(String currentPasswordLabel) {
            this.currentPasswordLabel = currentPasswordLabel;
        }

        public String getDesiredPasswordLabel() {
            return desiredPasswordLabel;
        }

        public void setDesiredPasswordLabel(String desiredPasswordLabel) {
            this.desiredPasswordLabel = desiredPasswordLabel;
        }

        public String getConfirmPasswordLabel() {
            return confirmPasswordLabel;
        }

        public void setConfirmPasswordLabel(String confirmPasswordLabel) {
            this.confirmPasswordLabel = confirmPasswordLabel;
        }

        public String getUseridComplexityLabel() {
            return useridComplexityLabel;
        }

        public void setUseridComplexityLabel(String useridComplexityLabel) {
            this.useridComplexityLabel = useridComplexityLabel;
        }

        public String getPasswordComplexityLabel() {
            return passwordComplexityLabel;
        }

        public void setPasswordComplexityLabel(String complexityLabel) {
            this.passwordComplexityLabel = complexityLabel;
        }

        public String getPasswordStrengthLabel() {
            return passwordStrengthLabel;
        }

        public void setPasswordStrengthLabel(String passwordStrengthLabel) {
            this.passwordStrengthLabel = passwordStrengthLabel;
        }

        public String getRequiredFieldMessageFormat() {
            return requiredFieldMessageFormat;
        }

        public void setRequiredFieldMessageFormat(String requiredFieldMessageFormat) {
            this.requiredFieldMessageFormat = requiredFieldMessageFormat;
        }

        public String getUseridInvalidMessage() {
            return useridInvalidMessage;
        }

        public void setUseridInvalidMessage(String useridInvalidMessage) {
            this.useridInvalidMessage = useridInvalidMessage;
        }

        public String getPasswordInvalidMessage() {
            return passwordInvalidMessage;
        }

        public void setPasswordInvalidMessage(String passwordInvalidMessage) {
            this.passwordInvalidMessage = passwordInvalidMessage;
        }

        public String getMismatchedPasswordsMessage() {
            return mismatchedPasswordsMessage;
        }

        public void setMismatchedPasswordsMessage(String mismatchedPasswordsMessage) {
            this.mismatchedPasswordsMessage = mismatchedPasswordsMessage;
        }

        public String getPasswordStrengthLevelCaption(PasswordStrengthLevel passwordStrengthLevel) {
            return passwordStrengthLevelCaptions.get(passwordStrengthLevel);
        }

        public void setPasswordStrengthLevelCaption(PasswordStrengthLevel passwordStrengthLevel, String caption) {
            this.passwordStrengthLevelCaptions.put(passwordStrengthLevel, caption);
        }
    }


    private static class RuleItem extends Composite<HorizontalLayout> {
        public static final String ICON_SIZE = "1.875ex";

        private final transient ChangePasswordRule changePasswordRule;
        private final Div satisfiedDiv;

        public RuleItem(ChangePasswordRule changePasswordRule) {
            this.changePasswordRule = changePasswordRule;

            satisfiedDiv = new Div();
            satisfiedDiv.addClassNames(LumoUtility.Display.FLEX,
                    LumoUtility.Padding.Top.XSMALL, LumoUtility.AlignItems.START);
            satisfiedDiv.add(createEmptyIcon());

            var textDiv = new Div();
            textDiv.add(changePasswordRule.getText());

            var content = getContent();
            content.add(satisfiedDiv);
            content.add(textDiv);
        }

        public ChangePasswordRule getRule() {
            return changePasswordRule;
        }

        public boolean isSatisfiedBy(String password) {
            satisfiedDiv.removeAll();

            if (password.isEmpty()) {
                satisfiedDiv.add(createEmptyIcon());
                return false;
            }

            var satisfied = changePasswordRule.isSatisfiedBy(password);
            satisfiedDiv.add(satisfied ? createPassIcon() : createFailIcon());
            return satisfied;
        }

        private static Icon createEmptyIcon() {
            var icon = VaadinIcon.CIRCLE_THIN.create();
            icon.setColor("var(--lumo-contrast-50pct)");
            icon.setSize(ICON_SIZE);
            return icon;
        }

        private static Icon createFailIcon() {
            var icon = VaadinIcon.CLOSE_CIRCLE.create();
            icon.setColor("var(--lumo-error-color)");
            icon.setSize(ICON_SIZE);
            return icon;
        }

        private static Icon createPassIcon() {
            var icon = VaadinIcon.CHECK_CIRCLE_O.create();
            icon.setColor("var(--lumo-success-color)");
            icon.setSize(ICON_SIZE);
            return icon;
        }
    }


    private static class StrengthMeter extends Composite<VerticalLayout> {
        private transient ChangePasswordI18n changePasswordI18n;

        private final Span strengthLevelLabelSpan;
        private final List<Div> strengthLevelBoxes;
        private final Span strengthLevelCaptionSpan;
        private final Div feedbackDiv;

        private transient PasswordStrength passwordStrength;

        public StrengthMeter(ChangePasswordI18n changePasswordI18n) {
            this.changePasswordI18n = changePasswordI18n;

            strengthLevelLabelSpan = new Span(changePasswordI18n.getPasswordStrengthLabel());
            strengthLevelLabelSpan.addClassNames(HEADING_CLASS_NAME);

            strengthLevelBoxes = Arrays.stream(PasswordStrengthLevel.values())
                    .map(level -> new Div())
                    .collect(Collectors.toList());
            strengthLevelBoxes.forEach(div -> div.addClassNames("change-password-strength-level-box"));

            var strengthLevelBar = new Div();
            strengthLevelBar.addClassNames("change-password-strength-level-bar");
            strengthLevelBar.addClassNames(LumoUtility.Display.FLEX, LumoUtility.FlexDirection.ROW);
            strengthLevelBar.add(strengthLevelBoxes.toArray(Div[]::new));

            strengthLevelCaptionSpan = new Span();

            var strengthLevelBlock = new HorizontalLayout();
            strengthLevelBlock.add(strengthLevelBar);
            strengthLevelBlock.add(strengthLevelCaptionSpan);

            feedbackDiv = new Div();

            var content = getContent();
            content.setPadding(false);
            content.setSpacing(false);
            content.add(strengthLevelLabelSpan);
            content.add(strengthLevelBlock);
            content.add(feedbackDiv);
        }

        public void setChangePasswordI18n(ChangePasswordI18n changePasswordI18n) {
            this.changePasswordI18n = changePasswordI18n;

            strengthLevelLabelSpan.setText(changePasswordI18n.getPasswordStrengthLabel());
            setPasswordStrength(passwordStrength);
        }

        public PasswordStrength getPasswordStrength() {
            return passwordStrength;
        }

        public void setPasswordStrength(PasswordStrength passwordStrength) {
            this.passwordStrength = passwordStrength;

            int passwordStrengthLevelOrdinal;
            String passwordStrengthLevelColor;
            String caption;
            String feedback;

            if (passwordStrength != null) {
                var passwordStrengthLevel = passwordStrength.passwordStrengthLevel();
                passwordStrengthLevelOrdinal = passwordStrengthLevel.ordinal();
                passwordStrengthLevelColor = passwordStrengthLevel.getColor();
                caption = changePasswordI18n.getPasswordStrengthLevelCaption(passwordStrengthLevel);
                feedback = passwordStrength.feedback();
            }
            else {
                passwordStrengthLevelOrdinal = -1;
                passwordStrengthLevelColor = "transparent";
                caption = "";
                feedback = "";
            }

            for (PasswordStrengthLevel level : PasswordStrengthLevel.values()) {
                var div = strengthLevelBoxes.get(level.ordinal());
                var color = (level.ordinal() <= passwordStrengthLevelOrdinal) ? passwordStrengthLevelColor : "transparent";
                div.getStyle().set("--change-password-strength-level-box-color", color);
            }
            strengthLevelCaptionSpan.setText(caption);

            feedbackDiv.setText(feedback);
        }
    }


    private static class Credentials {
        private String userId;
        private String current;
        private String desired;
        private String confirm;

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getCurrent() {
            return current;
        }

        public void setCurrent(String current) {
            this.current = current;
        }

        public String getDesired() {
            return desired;
        }

        public void setDesired(String desired) {
            this.desired = desired;
        }

        public String getConfirm() {
            return confirm;
        }

        public void setConfirm(String confirm) {
            this.confirm = confirm;
        }
    }


    public enum PasswordStrengthLevel {
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


    public static class PasswordStrength {
        private final PasswordStrengthLevel passwordStrengthLevel;
        private final String feedback;

        public PasswordStrength(PasswordStrengthLevel passwordStrengthLevel,
                                String feedback) {
            this.passwordStrengthLevel = passwordStrengthLevel;
            this.feedback = feedback;
        }

        public PasswordStrengthLevel passwordStrengthLevel() {
            return passwordStrengthLevel;
        }

        public String feedback() {
            return feedback;
        }
    }
}
