package org.vaadin.addons.joelpop.changepassword;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.shared.Registration;
import org.vaadin.addons.joelpop.changepassword.ChangePasswordPanel.ChangePasswordI18n;
import org.vaadin.addons.joelpop.changepassword.ChangePasswordPanel.ChangePasswordType;
import org.vaadin.addons.joelpop.changepassword.ChangePasswordPanel.PasswordStrength;

import java.util.List;
import java.util.function.Function;

/**
 *
 * <pre>
 * +-header-------------------------------------+
 * | Change Password                            |
 * +-content------------------------------------+
 * | +-changePasswordPanel--------------------+ |
 * | |                                        | |
 * | |                                        | |
 * | |                                        | |
 * | |                                        | |
 * | |                                        | |
 * | |                                        | |
 * | |                                        | |
 * | |                                        | |
 * | |                                        | |
 * | |                                        | |
 * | |                                        | |
 * | |                                        | |
 * | |                                        | |
 * | |                                        | |
 * | |                                        | |
 * | |                                        | |
 * | |                                        | |
 * | +----------------------------------------+ |
 * +-footer-------------------------------------+
 * |              +-cancelButton-+ +-okButton-+ |
 * |              |    Cancel    | |    OK    | |
 * |              +--------------+ +----------+ |
 * +--------------------------------------------+
 * </pre>
 */
public class ChangePasswordDialog extends Composite<Dialog> {
    private transient ChangePasswordDialogI18n changePasswordDialogI18n;

    private final Dialog dialog;
    private final ChangePasswordPanel changePasswordPanel;
    private final Button cancelButton;
    private final Button okButton;

    public ChangePasswordDialog() {
        this(ChangePasswordType.CHANGE_KNOWN);
    }

    public ChangePasswordDialog(ChangePasswordType changePasswordType) {
        this(changePasswordType, new ChangePasswordDialogI18n());
    }

    public ChangePasswordDialog(ChangePasswordDialogI18n changePasswordDialogI18n) {
        this(ChangePasswordType.CHANGE_KNOWN, changePasswordDialogI18n);
    }

    public ChangePasswordDialog(ChangePasswordType changePasswordType,
                                ChangePasswordDialogI18n changePasswordDialogI18n) {
        this.changePasswordDialogI18n = changePasswordDialogI18n;

        changePasswordPanel = new ChangePasswordPanel(changePasswordType, changePasswordDialogI18n);

        dialog = getContent();
        dialog.setModal(true);
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.setHeaderTitle(changePasswordDialogI18n.getHeadingLabel());
        dialog.add(changePasswordPanel);

        cancelButton = new Button(changePasswordDialogI18n.getCancelButtonLabel());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.setDisableOnClick(true);
        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addClickListener(this::onCancelClick);

        okButton = new Button(changePasswordDialogI18n.getOkButtonLabel());
        okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        okButton.setDisableOnClick(true);
        okButton.addClickShortcut(Key.ENTER);
        okButton.addClickListener(this::onOkClick);

        var footer = dialog.getFooter();
        footer.add(cancelButton);
        footer.add(okButton);
    }

    public ChangePasswordDialogI18n getChangePasswordDialogI18n() {
        return changePasswordDialogI18n;
    }

    public void setChangePasswordDialogI18n(ChangePasswordDialogI18n changePasswordDialogI18n) {
        this.changePasswordDialogI18n = changePasswordDialogI18n;

        changePasswordPanel.setChangePasswordI18n(changePasswordDialogI18n);
        dialog.setHeaderTitle(changePasswordDialogI18n.getHeadingLabel());
        cancelButton.setText(changePasswordDialogI18n.getCancelButtonLabel());
        okButton.setText(changePasswordDialogI18n.getOkButtonLabel());
    }

    public void reset() {
        changePasswordPanel.reset();
    }

    public void setInfoText(Html html) {
        changePasswordPanel.setInfoText(html);
    }

    public List<ChangePasswordRule> getUseridRules() {
        return changePasswordPanel.getUseridRules();
    }

    public void setUseridRules(ChangePasswordRule... rules) {
        changePasswordPanel.setUseridRules(rules);
    }

    public void addUseridRule(ChangePasswordRule changePasswordRule) {
        changePasswordPanel.addUseridRule(changePasswordRule);
    }

    public List<ChangePasswordRule> getPasswordRules() {
        return changePasswordPanel.getPasswordRules();
    }

    public void setPasswordRules(ChangePasswordRule... rules) {
        changePasswordPanel.setPasswordRules(rules);
    }

    public void addPasswordRule(ChangePasswordRule changePasswordRule) {
        changePasswordPanel.addPasswordRule(changePasswordRule);
    }

    public void setScorer(Function<String, PasswordStrength> scorer) {
        changePasswordPanel.setScorer(scorer);
    }



    private void onCancelClick(ClickEvent<Button> event) {
        dialog.close();
        fireCancelEvent(event.isFromClient());
        cancelButton.setEnabled(true);
    }

    private void onOkClick(ClickEvent<Button> event) {
        if (changePasswordPanel.isValid()) {
            dialog.close();
            fireOkEvent(event.isFromClient());
        }
        okButton.setEnabled(true);
    }

    public boolean isOpened() {
        return dialog.isOpened();
    }

    public void setOpened(boolean opened) {
        dialog.setOpened(opened);
    }

    public void open() {
        dialog.open();
    }

    public void close() {
        dialog.close();
    }


    public static class ChangePasswordDialogI18n extends ChangePasswordI18n {
        private String headingLabel;
        private String cancelButtonLabel;
        private String okButtonLabel;

        public ChangePasswordDialogI18n() {
            headingLabel = "Change Password";
            cancelButtonLabel = "Cancel";
            okButtonLabel = "OK";
        }

        public String getHeadingLabel() {
            return headingLabel;
        }

        public void setHeadingLabel(String headingLabel) {
            this.headingLabel = headingLabel;
        }

        public String getCancelButtonLabel() {
            return cancelButtonLabel;
        }

        public void setCancelButtonLabel(String cancelButtonLabel) {
            this.cancelButtonLabel = cancelButtonLabel;
        }

        public String getOkButtonLabel() {
            return okButtonLabel;
        }

        public void setOkButtonLabel(String okButtonLabel) {
            this.okButtonLabel = okButtonLabel;
        }
    }

    // OkEvent

    public static class OkEvent extends ComponentEvent<ChangePasswordDialog> {
        public OkEvent(ChangePasswordDialog source, boolean fromClient) {
            super(source, fromClient);
        }

        public String getUserid() {
            return getSource().changePasswordPanel.getUserid();
        }

        public String getCurrentPassword() {
            return getSource().changePasswordPanel.getCurrentPassword();
        }

        public String getDesiredPassword() {
            return getSource().changePasswordPanel.getDesiredPassword();
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Registration addOkListener(ComponentEventListener<OkEvent> selectListener) {
        return addListener(OkEvent.class, (ComponentEventListener) selectListener);
    }

    @SuppressWarnings("java:S3398")  // not moving to inner class to maintain pattern
    private void fireOkEvent(boolean fromClient) {
        fireEvent(new OkEvent(this, fromClient));
    }

    // CancelEvent

    public static class CancelEvent extends ComponentEvent<ChangePasswordDialog> {
        public CancelEvent(ChangePasswordDialog source, boolean fromClient) {
            super(source, fromClient);
        }
    }

    public Registration addCancelListener(ComponentEventListener<CancelEvent> cancelListener) {
        return addListener(CancelEvent.class, cancelListener);
    }

    @SuppressWarnings("java:S3398")  // not moving to inner class to maintain pattern
    private void fireCancelEvent(boolean fromClient) {
        fireEvent(new CancelEvent(this, fromClient));
    }

}
