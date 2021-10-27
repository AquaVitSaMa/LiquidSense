package me.aquavitt.liquidssense.utils.misc;

import net.ccbluex.liquidbounce.ui.client.GuiMainMenu;
import me.aquavitt.liquidssense.utils.mc.MinecraftInstance;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ServerUtils extends MinecraftInstance {

    public static ServerData serverData;

    public static void connectToLastServer() {
        if (serverData == null)
            return;

        mc.displayGuiScreen(new GuiConnecting(new GuiMultiplayer(new GuiMainMenu()), mc, serverData));
    }

    public static String getRemoteIp() {
        String serverIp = "Singleplayer";

        if (mc.theWorld != null && mc.theWorld.isRemote) {
            final ServerData serverData = mc.getCurrentServerData();
            if (serverData != null)
                serverIp = serverData.serverIP;
        }

        return serverIp;
    }
}