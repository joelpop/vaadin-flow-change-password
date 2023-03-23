package org.vaadin.addons.joelpop.changepassword;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.shared.Registration;
import org.vaadin.addons.joelpop.changepassword.ChangePasswordPanel.ChangePasswordI18n;

import java.util.List;
import java.util.function.Function;

/**
 * A dialog for changing, resetting, or establishing a password.
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
public class ChangePasswordDialog extends Composite<Dialog> implements ChangePassword {
    private transient ChangePasswordDialogI18n changePasswordDialogI18n;

    private final Dialog dialog;
    private final ChangePasswordPanel changePasswordPanel;
    private final Button cancelButton;
    private final Button okButton;

    /**
     * Create a change password dialog for changing a known password. Uses default English labels.
     */
    public ChangePasswordDialog() {
        this(ChangePasswordMode.CHANGE_KNOWN);
    }

    /**
     * Create a change password dialog for the supplied mode. Uses default English labels.
     *
     * @param changePasswordMode the type of password change to make
     */
    public ChangePasswordDialog(ChangePasswordMode changePasswordMode) {
        this(changePasswordMode, new ChangePasswordDialogI18n());
    }

    /**
     * Create a change password dialog for changing a known password. Uses the supplied labels.
     *
     * @param changePasswordDialogI18n the labels to use for the dialog in place of the default English ones
     */
    public ChangePasswordDialog(ChangePasswordDialogI18n changePasswordDialogI18n) {
        this(ChangePasswordMode.CHANGE_KNOWN, changePasswordDialogI18n);
    }

    /**
     * Create a change password dialog for the supplied mode and labels.
     *
     * @param changePasswordMode the type of password change to make
     * @param changePasswordDialogI18n the labels to use for the dialog in place of the default English ones
     */
    public ChangePasswordDialog(ChangePasswordMode changePasswordMode,
                                ChangePasswordDialogI18n changePasswordDialogI18n) {
        this.changePasswordDialogI18n = changePasswordDialogI18n;

        changePasswordPanel = new ChangePasswordPanel(changePasswordMode, changePasswordDialogI18n);

        dialog = getContent();
        dialog.setModal(true);
        dialog.setDraggable(true);
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

    @Override
    public ChangePasswordMode getChangePasswordMode() {
        return changePasswordPanel.getChangePasswordMode();
    }

    @Override
    public void setChangePasswordMode(ChangePasswordMode changePasswordMode) {
        changePasswordPanel.setChangePasswordMode(changePasswordMode);
    }

    /**
     * Return the labels used for the dialog.
     *
     * @return the labels used for the dialog
     */
    public ChangePasswordDialogI18n getChangePasswordDialogI18n() {
        return changePasswordDialogI18n;
    }

    /**
     * Set the labels for the dialog to use.
     *
     * @param changePasswordDialogI18n the labels for the dialog to use
     */
    public void setChangePasswordDialogI18n(ChangePasswordDialogI18n changePasswordDialogI18n) {
        this.changePasswordDialogI18n = changePasswordDialogI18n;

        changePasswordPanel.setChangePasswordI18n(changePasswordDialogI18n);
        dialog.setHeaderTitle(changePasswordDialogI18n.getHeadingLabel());
        cancelButton.setText(changePasswordDialogI18n.getCancelButtonLabel());
        okButton.setText(changePasswordDialogI18n.getOkButtonLabel());
    }

    @Override
    public void reset() {
        changePasswordPanel.reset();
    }

    @Override
    public String getUserid() {
        return changePasswordPanel.getUserid();
    }

    @Override
    public void setUserid(String userid) {
        changePasswordPanel.setUserid(userid);
    }

    @Override
    public String getCurrentPassword() {
        return changePasswordPanel.getCurrentPassword();
    }

    @Override
    public String getDesiredPassword() {
        return changePasswordPanel.getDesiredPassword();
    }

    @Override
    public void setInfoText(String infoText) {
        changePasswordPanel.setInfoText(infoText);
    }

    @Override
    public void setInfoText(Component infoTextComponent) {
        changePasswordPanel.setInfoText(infoTextComponent);
    }

    @Override
    public List<ChangePasswordRule> getUseridRules() {
        return changePasswordPanel.getUseridRules();
    }

    @Override
    public void setUseridRules(ChangePasswordRule... rules) {
        changePasswordPanel.setUseridRules(rules);
    }

    @Override
    public void addUseridRules(ChangePasswordRule... rules) {
        changePasswordPanel.addUseridRules(rules);
    }

    @Override
    public List<ChangePasswordRule> getPasswordRules() {
        return changePasswordPanel.getPasswordRules();
    }

    @Override
    public void setPasswordRules(ChangePasswordRule... rules) {
        changePasswordPanel.setPasswordRules(rules);
    }

    @Override
    public void addPasswordRules(ChangePasswordRule... rules) {
        changePasswordPanel.addPasswordRules(rules);
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
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

    /**
     * Return the open state of the dialog.
     *
     * @return {@code true} if the dialog is open, {@code false} otherwise
     */
    public boolean isOpened() {
        return dialog.isOpened();
    }

    /**
     * Set the open state of the dialog.
     *
     * @param opened the open state of the dialog to set
     */
    public void setOpened(boolean opened) {
        dialog.setOpened(opened);
    }

    /**
     * Open the dialog.
     */
    public void open() {
        dialog.open();
    }

    /**
     * Close the dialog.
     */
    public void close() {
        dialog.close();
    }


    /**
     * The labels for the dialog to use.
     * <p>
     *     The default labels are US English.
     * </p>
     */
    public static class ChangePasswordDialogI18n extends ChangePasswordI18n {
        private String headingLabel;
        private String cancelButtonLabel;
        private String okButtonLabel;

        /**
         * Create labels for the dialog initialized with US English.
         */
        public ChangePasswordDialogI18n() {
            headingLabel = "Change Password";
            cancelButtonLabel = "Cancel";
            okButtonLabel = "OK";
        }

        /**
         * Return the label used for the dialog heading.
         *
         * @return the dialog heading label
         */
        public String getHeadingLabel() {
            return headingLabel;
        }

        /**
         * Set the label to use for the dialog heading.
         *
         * @param headingLabel the label to use for the dialog heading
         */
        public void setHeadingLabel(String headingLabel) {
            this.headingLabel = headingLabel;
        }

        /**
         * Return the label used for the cancel button.
         *
         * @return the cancel button label
         */
        public String getCancelButtonLabel() {
            return cancelButtonLabel;
        }

        /**
         * Set the label to use for the cancel button.
         *
         * @param cancelButtonLabel the label to use for the cancel button
         */
        public void setCancelButtonLabel(String cancelButtonLabel) {
            this.cancelButtonLabel = cancelButtonLabel;
        }

        /**
         * Return the label used for the OK button.
         *
         * @return the OK button label
         */
        public String getOkButtonLabel() {
            return okButtonLabel;
        }

        /**
         * Set the label to use for the OK button.
         *
         * @param okButtonLabel the label to use for the OK button
         */
        public void setOkButtonLabel(String okButtonLabel) {
            this.okButtonLabel = okButtonLabel;
        }
    }

    // OkEvent

    /**
     * The event fired when the dialog is OKed.
     */
    public static class OkEvent extends ComponentEvent<ChangePasswordDialog> {
        /**
         * Create an OK event.
         *
         * @param source the dialog
         * @param fromClient {@code true} if the event originated from a client, {@code false} otherwise
         */
        public OkEvent(ChangePasswordDialog source, boolean fromClient) {
            super(source, fromClient);
        }

        /**
         * Return the value from the user ID field.
         *
         * @return the supplied user ID
         */
        public String getUserid() {
            return getSource().changePasswordPanel.getUserid();
        }

        /**
         * Return the value from the current password field.
         *
         * @return the supplied current password
         */
        public String getCurrentPassword() {
            return getSource().changePasswordPanel.getCurrentPassword();
        }

        /**
         * Return the value from the desired password field.
         *
         * @return the supplied desired password
         */
        public String getDesiredPassword() {
            return getSource().changePasswordPanel.getDesiredPassword();
        }
    }

    /**
     * Add an OK listener to the dialog.
     *
     * @param okListener a listener to call when the dialog is OKed
     * @return a registration to facilitate removal of the OK listener
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public Registration addOkListener(ComponentEventListener<OkEvent> okListener) {
        return addListener(OkEvent.class, (ComponentEventListener) okListener);
    }

    @SuppressWarnings("java:S3398")  // not moving to inner class to maintain pattern
    private void fireOkEvent(boolean fromClient) {
        fireEvent(new OkEvent(this, fromClient));
    }

    // CancelEvent

    /**
     * The event fired when the dialog is canceled.
     */
    public static class CancelEvent extends ComponentEvent<ChangePasswordDialog> {
        /**
         * Create a cancel event.
         *
         * @param source the dialog
         * @param fromClient {@code true} if the event originated from a client, {@code false} otherwise
         */
        public CancelEvent(ChangePasswordDialog source, boolean fromClient) {
            super(source, fromClient);
        }
    }

    /**
     * Add a cancel listener to the dialog.
     *
     * @param cancelListener a listener to call when the dialog is canceled
     * @return a registration to facilitate removal of the cancel listener
     */
    public Registration addCancelListener(ComponentEventListener<CancelEvent> cancelListener) {
        return addListener(CancelEvent.class, cancelListener);
    }

    @SuppressWarnings("java:S3398")  // not moving to inner class to maintain pattern
    private void fireCancelEvent(boolean fromClient) {
        fireEvent(new CancelEvent(this, fromClient));
    }

}
