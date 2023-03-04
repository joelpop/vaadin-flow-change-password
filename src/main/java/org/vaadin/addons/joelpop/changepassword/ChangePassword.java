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
import com.vaadin.flow.data.binder.ValueContext;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.*;
import java.util.function.Function;

/**
 *
 * <pre>
 * +-content(HorizontalLayout)---------------------------------------------------------------+
 * | +-credentialBlock(VerticalLayout)---+ +-helperBlock(VerticalLayout)-------------------+ |
 * | | User ID                           | | +-infoBlock(Div)----------------------------+ | |
 * | | +-useridTextField---------------+ | | |                                           | | |
 * | | |                               | | | |                                           | | |
 * | | +-------------------------------+ | | +-------------------------------------------+ | |
 *   | Current Password                  | | +-complexityBlock(VerticalLayout)-----------+ | |
 * | | +-currentPasswordField----------+ | | | Complexity Rules                          | | |
 * | | |                               | | | | +-ruleBlock(VerticalLayout)-------------+ | | |
 * | | +-------------------------------+ | | | | ( ) Between 10 and 20 characters long | | | |
 * | | New Password                      | | | | ( ) At least 1 lowercase letter       | | | |
 * | | +-desiredPasswordField----------+ | | | | ( ) At least 1 uppercase letter       | | | |
 * | | |                               | | | | | ( ) At least 1 digit                  | | | |
 * | | +-------------------------------+ | | | | ( ) At least 1 special character      | | | |
 * | | Confirm Password                  | | | | ( ) Different from current password   | | | |
 * | | +-confirmPasswordField----------+ | | | +---------------------------------------+ | | |
 * | | |                               | | | +-------------------------------------------+ | |
 * | | +-------------------------------+ | | +-strengthMeter-----------------------------+ | |
 * | +-----------------------------------+ | | [#]  [ ]  [ ]  [ ]  [ ]  Very Weak        | | |
 * |                                       | | Could take 0 seconds to crack.            | | |
 * |                                       | +-------------------------------------------+ | |
 * |                                       +-----------------------------------------------+ |
 * +-----------------------------------------------------------------------------------------+
 * </pre>
 */
@CssImport("./change-password/styles/change-password.css")
public class ChangePassword extends Composite<HorizontalLayout> {

    private final TextField useridTextField;
    private final PasswordField currentPasswordField;
    private final PasswordField desiredPasswordField;
    private final PasswordField confirmPasswordField;
    private final VerticalLayout helpBlock;
    private final Div infoBlock;
    private final VerticalLayout complexityBlock;
    private final VerticalLayout ruleBlock;
    private final StrengthMeter strengthMeter;

    private final transient PasswordTrio passwordTrio;
    private final Binder<PasswordTrio> passwordBinder;

    private final transient List<RuleItem> ruleItems;
    private transient Function<String, PasswordStrength> scorer;

    public ChangePassword() {
        useridTextField = new TextField("User ID");
        useridTextField.setWidthFull();
        useridTextField.setVisible(false);
        useridTextField.setReadOnly(true);

        currentPasswordField = new PasswordField("Current Password");
        currentPasswordField.setWidthFull();
        currentPasswordField.setValueChangeMode(ValueChangeMode.EAGER);

        desiredPasswordField = new PasswordField("New Password");
        desiredPasswordField.setWidthFull();
        desiredPasswordField.setValueChangeMode(ValueChangeMode.EAGER);
        desiredPasswordField.addValueChangeListener(this::onDesiredPasswordChange);

        confirmPasswordField = new PasswordField("Confirm Password");
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

        ruleBlock = new VerticalLayout();
        ruleBlock.setPadding(false);
        ruleBlock.setSpacing(false);

        complexityBlock = new VerticalLayout();
        complexityBlock.setVisible(false);
        complexityBlock.setPadding(false);
        complexityBlock.setSpacing(false);
        complexityBlock.add(new Span("Complexity Rules"));
        complexityBlock.add(ruleBlock);

        strengthMeter = new StrengthMeter();
        strengthMeter.setVisible(false);

        helpBlock = new VerticalLayout();
        helpBlock.addClassNames(LumoUtility.FontSize.SMALL, LumoUtility.LineHeight.SMALL);
        helpBlock.setWidth(20f, Unit.EM);
        helpBlock.setVisible(false);
        helpBlock.setPadding(false);
        helpBlock.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        helpBlock.add(infoBlock);
        helpBlock.add(complexityBlock);
        helpBlock.add(strengthMeter);

        var content = getContent();
        content.add(credentialBlock);
        content.add(helpBlock);

        passwordTrio = new PasswordTrio();

        passwordBinder = new Binder<>();

        passwordBinder.forField(currentPasswordField)
                .asRequired(fieldIsRequired(currentPasswordField))
                .withNullRepresentation("")
                .bind(PasswordTrio::getCurrent, PasswordTrio::setCurrent);

        passwordBinder.forField(desiredPasswordField)
                .asRequired(fieldIsRequired(desiredPasswordField))
                .withValidator(this::passwordRuleValidator)
                .withNullRepresentation("")
                .bind(PasswordTrio::getDesired, PasswordTrio::setDesired);

        passwordBinder.forField(confirmPasswordField)
                .asRequired(fieldIsRequired(confirmPasswordField))
                .withValidator(this::passwordsMatchValidator)
                .withNullRepresentation("")
                .bind(PasswordTrio::getConfirm, PasswordTrio::setConfirm);

        passwordBinder.setBean(passwordTrio);

        ruleItems = new ArrayList<>();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        if (!useridTextField.isReadOnly()) {
            useridTextField.focus();
        }
        else if (!currentPasswordField.isReadOnly()) {
            currentPasswordField.focus();
        }
        else {
            desiredPasswordField.focus();
        }
    }

    private void updateHelpBlockVisibility() {
        helpBlock.setVisible(infoBlock.isVisible() || complexityBlock.isVisible() || strengthMeter.isVisible());
    }

    private String fieldIsRequired(HasLabel hasLabel) {
        return "%s is required.".formatted(hasLabel.getLabel());
    }

    private ValidationResult passwordRuleValidator(String desiredPassword, ValueContext valueContext) {
        if (ruleItems.stream()
                .filter(ruleItem -> ruleItem.isSatisfiedBy(desiredPassword))
                .count() == ruleItems.size()) {
            return ValidationResult.ok();
        }
        else {
            return ValidationResult.error("Desired password must satisfy all rules.");
        }
    }

    private ValidationResult passwordsMatchValidator(String confirmPassword, ValueContext valueContext) {
        return Objects.equals(confirmPassword, desiredPasswordField.getValue())
                ? ValidationResult.ok()
                : ValidationResult.error("Passwords don't match.");
    }

    public String getUseridLabel() {
        return useridTextField.getLabel();
    }

    public void setUseridLabel(String useridLabel) {
        useridTextField.setLabel(useridLabel);
    }

    public String getCurrentPasswordLabel() {
        return currentPasswordField.getLabel();
    }

    public void setCurrentPasswordLabel(String currentPasswordLabel) {
        currentPasswordField.setLabel(currentPasswordLabel);
    }

    public String getNewPasswordLabel() {
        return desiredPasswordField.getLabel();
    }

    public void setNewPasswordLabel(String desiredPasswordLabel) {
        desiredPasswordField.setLabel(desiredPasswordLabel);
    }

    public String getConfirmPasswordLabel() {
        return confirmPasswordField.getLabel();
    }

    public void setConfirmPasswordLabel(String confirmPasswordLabel) {
        confirmPasswordField.setLabel(confirmPasswordLabel);
    }

    public String getUserid() {
        return useridTextField.getValue();
    }

    public void setUserid(String userid) {
        useridTextField.setValue(userid);
        useridTextField.setVisible(userid != null);
    }

    public String getCurrentPassword() {
        return passwordTrio.getCurrent();
    }

    public void setCurrentPassword(String password) {
        passwordTrio.setCurrent(password);
        passwordTrio.setDesired(null);
        passwordTrio.setConfirm(null);
        passwordBinder.refreshFields();
        currentPasswordField.setReadOnly(password != null);
    }

    public String getDesiredPassword() {
        return passwordTrio.getDesired();
    }

    public void setInfoText(String infoText) {
        setInfoText(Optional.ofNullable(infoText)
                .map(Text::new)
                .orElse(null));
    }

    public void setInfoText(Component infoTextComponent) {
        infoBlock.removeAll();

        var visible = infoTextComponent != null;
        if (visible) {
            infoBlock.add(infoTextComponent);
        }

        infoBlock.setVisible(visible);
        updateHelpBlockVisibility();
    }

    public List<PasswordRule> getRules() {
        return ruleItems.stream()
                .map(RuleItem::getRule)
                .toList();
    }

    public void setRules(PasswordRule... rules) {
        ruleItems.clear();
        ruleBlock.removeAll();

        addRule(rules);
    }

    public void addRule(PasswordRule... rules) {
        Arrays.stream(rules)
                .map(RuleItem::new)
                .forEach(ruleItem -> {
                    ruleItems.add(ruleItem);
                    ruleBlock.add(ruleItem);
                });

        complexityBlock.setVisible(!ruleItems.isEmpty());
        updateHelpBlockVisibility();
    }

    public void setScorer(Function<String, PasswordStrength> scorer) {
        this.scorer = scorer;

        strengthMeter.setVisible(scorer != null);
        updateHelpBlockVisibility();
    }

    private void onDesiredPasswordChange(ComponentValueChangeEvent<PasswordField, String> event) {
        var desiredPassword = event.getValue();
        var desiredPasswordEmpty = desiredPassword.isEmpty();

        if (desiredPasswordEmpty) {
            passwordRuleValidator(desiredPassword, null);
        }
        if (scorer != null) {
            strengthMeter.setPasswordStrength(desiredPasswordEmpty ? null : scorer.apply(desiredPassword));
        }
    }

    public boolean isValid() {
        return passwordBinder.validate().isOk();
    }


    private static class RuleItem extends Composite<HorizontalLayout> {
        public static final String ICON_SIZE = "1.875ex";

        private final transient PasswordRule passwordRule;
        private final Div satisfiedDiv;

        public RuleItem(PasswordRule passwordRule) {
            this.passwordRule = passwordRule;

            satisfiedDiv = new Div();
            satisfiedDiv.addClassNames(LumoUtility.Display.FLEX,
                    LumoUtility.Padding.Top.XSMALL, LumoUtility.AlignItems.START);
            satisfiedDiv.add(createEmptyIcon());

            var textDiv = new Div();
            textDiv.add(passwordRule.getText());

            var content = getContent();
            content.add(satisfiedDiv);
            content.add(textDiv);
        }

        public PasswordRule getRule() {
            return passwordRule;
        }

        public boolean isSatisfiedBy(String password) {
            satisfiedDiv.removeAll();

            if (password.isEmpty()) {
                satisfiedDiv.add(createEmptyIcon());
                return false;
            }

            var satisfied = passwordRule.isSatisfiedBy(password);
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
        private final List<Div> strengthLevelBoxes;
        private final Span strengthLevelCaptionSpan;
        private final Div feedbackDiv;

        private transient PasswordStrength passwordStrength;

        public StrengthMeter() {
            strengthLevelBoxes = Arrays.stream(PasswordStrengthLevel.values())
                    .map(level -> new Div())
                    .toList();
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
            content.add(new Text("Password Strength"));
            content.add(strengthLevelBlock);
            content.add(feedbackDiv);
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
                caption = passwordStrengthLevel.getCaption();
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


    private static class PasswordTrio {
        private String current;
        private String desired;
        private String confirm;

        public String getCurrent() {
            return this.current;
        }

        public String getDesired() {
            return this.desired;
        }

        public String getConfirm() {
            return this.confirm;
        }

        public void setCurrent(String current) {
            this.current = current;
        }

        public void setDesired(String desired) {
            this.desired = desired;
        }

        public void setConfirm(String confirm) {
            this.confirm = confirm;
        }
    }

    public enum PasswordStrengthLevel {
        VERY_WEAK("Very Weak", "#FF1F1f"),
        WEAK("Weak", "#FFBF00"),
        MEDIOCRE("Mediocre", "#EFEF00"),
        STRONG("Strong", "#1FFF1f"),
        VERY_STRONG("Very Strong", "#00BF00");

        private final String caption;
        private final String color;

        PasswordStrengthLevel(String caption, String color) {
            this.caption = caption;
            this.color = color;
        }

        public String getCaption() {
            return this.caption;
        }

        public String getColor() {
            return this.color;
        }
    }

    public record PasswordStrength(
            PasswordStrengthLevel passwordStrengthLevel,
            String feedback
    ) {}
}
