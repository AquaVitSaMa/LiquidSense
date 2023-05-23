package me.aquavit.liquidsense.command;

import me.aquavit.liquidsense.utils.mc.MinecraftInstance;
import me.aquavit.liquidsense.LiquidBounce;
import me.aquavit.liquidsense.utils.client.ClientUtils;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public abstract class Command extends MinecraftInstance {

    private final String command;
    private final String[] alias;

    public Command(String command, String ... alias) {
        this.command = command;
        this.alias = alias;
    }

    public abstract void execute(String[] args);

    public List<String> tabComplete(String[] args) {
        return new ArrayList<>();
    }

    protected final void chat(String msg) {
        ClientUtils.displayChatMessage("§8[§9§lLiquidSense§8] §3" + msg);
    }

    protected final void chatSyntax(String syntax) {
        ClientUtils.displayChatMessage("§8[§9§lLiquidSense§8] §3Syntax: §7" + LiquidBounce.commandManager.getPrefix() + syntax);
    }

    protected final void chatSyntax(String[] syntaxes) {
        ClientUtils.displayChatMessage("§8[§9§lLiquidSense§8] §3Syntax:");

        for (String syntax : syntaxes)
            ClientUtils.displayChatMessage("§8> §7"+LiquidBounce.commandManager.getPrefix()+command+" "+syntax.toLowerCase());
    }

    public final String getCommand() {
        return this.command;
    }

    public final String[] getAlias() {
        return this.alias;
    }

    protected final void chatSyntaxError() {
        ClientUtils.displayChatMessage("§8[§9§lLiquidSense§8] §3Syntax error");
    }

    protected final void playEdit() {
        mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("random.anvil_use"), (float)1.0f));
    }


}
