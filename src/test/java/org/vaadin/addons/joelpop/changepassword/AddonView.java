package org.vaadin.addons.joelpop.changepassword;

import com.vaadin.flow.component.html.Div;

//@Route("")
public class AddonView extends Div {

    public AddonView() {
        TheAddon theAddon = new TheAddon();
        theAddon.setId("theAddon");
        add(theAddon);
    }
}
