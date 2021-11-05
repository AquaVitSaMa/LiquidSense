package net.ccbluex.liquidbounce.features.command.commands;

import net.ccbluex.liquidbounce.features.command.Command;

public class PingCommand extends Command
{
    public PingCommand() {
        super("ping");
    }

    @Override
    public void execute(final String[] args) {
        this.chat("§3Your ping is §a" + mc.getNetHandler().getPlayerInfo(mc.thePlayer.getUniqueID()).getResponseTime() + " §3ms.");
    }
}
