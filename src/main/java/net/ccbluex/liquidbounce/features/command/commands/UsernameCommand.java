package net.ccbluex.liquidbounce.features.command.commands;

import net.ccbluex.liquidbounce.features.command.Command;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class UsernameCommand extends Command
{
    public UsernameCommand() {
        super("username");
    }

    @Override
    public void execute(final String[] args) {
        this.chat("Username: " + mc.thePlayer.getName());
        final StringSelection stringSelection = new StringSelection(UsernameCommand.mc.thePlayer.getName());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);
    }
}
