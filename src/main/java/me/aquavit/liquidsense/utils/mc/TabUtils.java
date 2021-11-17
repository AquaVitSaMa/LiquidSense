package me.aquavit.liquidsense.utils.mc;

import net.ccbluex.liquidbounce.ui.client.gui.elements.GuiUsernameField;
import net.minecraft.client.gui.GuiTextField;

public final class TabUtils {

    public static void tab(final GuiUsernameField... textFields) {
        for(int i = 0; i < textFields.length; i++) {
            final GuiUsernameField textField = textFields[i];

            if(textField.isFocused()) {
                textField.setFocused(false);
                i++;

                if(i >= textFields.length)
                    i = 0;

                textFields[i].setFocused(true);
                break;
            }
        }
    }
}
