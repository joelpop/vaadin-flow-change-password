package org.vaadin.addons.joelpop.changepassword;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.shared.Registration;
import org.vaadin.addons.joelpop.changepassword.ChangePassword.PasswordStrength;

import java.util.function.Function;

/**
 *
 * <pre>
 * +-header-------------------------------------+
 * | Change Password                            |
 * +-content------------------------------------+
 * | +-changePassword-------------------------+ |
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
    private final Dialog dialog;
    private final ChangePassword changePassword;
    private final Button okButton;

    public ChangePasswordDialog() {
        changePassword = new ChangePassword();

        dialog = getContent();
        dialog.setModal(true);
        dialog.setCloseOnOutsideClick(false);
        dialog.setCloseOnEsc(false);
        dialog.setHeaderTitle("Change Password");
        dialog.add(changePassword);

        var cancelButton = new Button("Cancel");
        cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        cancelButton.setDisableOnClick(true);
        cancelButton.addClickShortcut(Key.ESCAPE);
        cancelButton.addClickListener(this::onCancelClick);

        okButton = new Button("OK");
        okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        okButton.setDisableOnClick(true);
        okButton.addClickShortcut(Key.ENTER);
        okButton.addClickListener(this::onOkClick);

        var footer = dialog.getFooter();
        footer.add(cancelButton);
        footer.add(okButton);
    }

    private void onCancelClick(ClickEvent<Button> event) {
        dialog.close();
        fireCancelEvent(event.isFromClient());
    }

    private void onOkClick(ClickEvent<Button> event) {
        if (changePassword.isValid()) {
            dialog.close();
            fireOkEvent(event.isFromClient());
        }
        else {
            okButton.setEnabled(true);
        }
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

    public void setCurrentPassword(String password) {
        changePassword.setCurrentPassword(password);
    }

    public void setInfoText(Html html) {
        changePassword.setInfoText(html);
    }

    public void addRule(PasswordRule passwordRule) {
        changePassword.addRule(passwordRule);
    }

    public void setScorer(Function<String, PasswordStrength> scorer) {
        changePassword.setScorer(scorer);
    }

    // OkEvent

    public static class OkEvent extends ComponentEvent<ChangePasswordDialog> {
        public OkEvent(ChangePasswordDialog source, boolean fromClient) {
            super(source, fromClient);
        }

        public String getDesiredPassword() {
            return getSource().changePassword.getDesiredPassword();
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
