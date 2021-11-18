/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.ui.client.gui.altmanager;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.thealtening.AltService;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.ui.client.gui.GuiAltManager;
import net.ccbluex.liquidbounce.ui.client.gui.elements.GuiButtonElement;
import net.ccbluex.liquidbounce.ui.client.gui.elements.GuiPasswordField;
import net.ccbluex.liquidbounce.ui.client.gui.elements.GuiUsernameField;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import me.aquavit.liquidsense.utils.client.ClientUtils;
import me.aquavit.liquidsense.utils.mc.TabUtils;
import me.aquavit.liquidsense.utils.login.MinecraftAccount;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.Proxy;

public class GuiAdd extends GuiScreen {

    private final GuiAltManager prevGui;

    private GuiButton addButton;
    private GuiButton clipboardButton;
    private GuiUsernameField username;
    private GuiPasswordField password;

    private String status = "§7Idle...";

    public GuiAdd(final GuiAltManager gui) {
        this.prevGui = gui;
    }

    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        buttonList.add(addButton = new GuiButtonElement(1, width / 2 - 100, height / 4 + 72, 200, 20,"Add"));
        buttonList.add(clipboardButton = new GuiButtonElement(2, width / 2 - 100, height / 4 + 96, 200, 20,"Clipboard"));
        buttonList.add(new GuiButtonElement(0, width / 2 - 100, height / 4 + 120,200, 20, "Back"));
        username = new GuiUsernameField(2, Fonts.font20, width / 2 - 100, 60, 120, 15);
        username.setFocused(true);
        username.setMaxStringLength(Integer.MAX_VALUE);
        password = new GuiPasswordField(3, Fonts.font20, width / 2 - 100, 80, 120, 15);
        password.setMaxStringLength(Integer.MAX_VALUE);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawBackground(0);

        drawCenteredString(Fonts.font20, "Add Account", width / 2, 34, 0xffffff);
        drawCenteredString(Fonts.font18, status == null ? "" : status, width / 2, height / 4 + 60, 0xffffff);

        drawRect(width / 2 - 108, 52, width / 2 + 116, 140, new Color(1,1,1, 80).getRGB());
        drawRect(width / 2 - 108, 52, width / 2 - 106, 140, new Color(17, 211,255, 255).getRGB());
        username.drawTextBox();
        password.drawTextBox();

        if(username.getText().isEmpty() && !username.isFocused())
            drawCenteredString(Fonts.font20, "Username / E-Mail", width / 2 - 55, 64, Color.WHITE.getRGB());

        if(password.getText().isEmpty() && !password.isFocused())
            drawCenteredString(Fonts.font20, "Password", width / 2 - 74, 83, Color.WHITE.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (!button.enabled)
            return;

        switch(button.id) {
            case 0:
                mc.displayGuiScreen(prevGui);
                break;
            case 1:
                if (LiquidBounce.fileManager.accountsConfig.altManagerMinecraftAccounts.stream().anyMatch(account -> account.getName().equals(username.getText()))) {
                    status = "§cThe account has already been added.";
                    break;
                }

                addAccount(username.getText(), password.getText());
                break;
            case 2:
                try{
                    final String clipboardData = (String) Toolkit.getDefaultToolkit().getSystemClipboard()
                            .getData(DataFlavor.stringFlavor);
                    final String[] accountData = clipboardData.split(":", 2);

                    if (!clipboardData.contains(":") || accountData.length != 2) {
                        status = "§cInvalid clipboard data. (Use: E-Mail:Password)";
                        return;
                    }

                    addAccount(accountData[0], accountData[1]);
                }catch(final UnsupportedFlavorException e) {
                    status = "§cClipboard flavor unsupported!";
                    ClientUtils.getLogger().error("Failed to read data from clipboard.", e);
                }
                break;
        }

        super.actionPerformed(button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        switch (keyCode) {
            case Keyboard.KEY_ESCAPE:
                mc.displayGuiScreen(prevGui);
                return;
            case Keyboard.KEY_TAB:
                TabUtils.tab(username, password);
                return;
            case Keyboard.KEY_RETURN:
                actionPerformed(addButton);
                return;
        }

        if(username.isFocused())
            username.textboxKeyTyped(typedChar, keyCode);

        if(password.isFocused())
            password.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        username.mouseClicked(mouseX, mouseY, mouseButton);
        password.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void updateScreen() {
        username.updateCursorCounter();
        password.updateCursorCounter();

        super.updateScreen();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        super.onGuiClosed();
    }

    private void addAccount(final String name, final String password) {
        if (LiquidBounce.fileManager.accountsConfig.altManagerMinecraftAccounts.stream()
                .anyMatch(account -> account.getName().equals(name))) {
            status = "§cThe account has already been added.";
            return;
        }

        addButton.enabled = clipboardButton.enabled = false;

        final MinecraftAccount account = new MinecraftAccount(name, password);

        new Thread(() -> {
            if (!account.isCracked()) {
                status = "§aChecking...";

                try {
                    final AltService.EnumAltService oldService = GuiAltManager.altService.getCurrentService();

                    if (oldService != AltService.EnumAltService.MOJANG) {
                        GuiAltManager.altService.switchService(AltService.EnumAltService.MOJANG);
                    }

                    final YggdrasilUserAuthentication userAuthentication = (YggdrasilUserAuthentication)
                            new YggdrasilAuthenticationService(Proxy.NO_PROXY, "")
                                    .createUserAuthentication(Agent.MINECRAFT);

                    userAuthentication.setUsername(account.getName());
                    userAuthentication.setPassword(account.getPassword());

                    userAuthentication.logIn();
                    account.setAccountName(userAuthentication.getSelectedProfile().getName());

                    if (oldService == AltService.EnumAltService.THEALTENING)
                        GuiAltManager.altService.switchService(AltService.EnumAltService.THEALTENING);
                } catch (NullPointerException | AuthenticationException | NoSuchFieldException | IllegalAccessException e) {
                    status = "§cThe account doesn't work.";
                    addButton.enabled = clipboardButton.enabled = true;
                    return;
                }
            }


            LiquidBounce.fileManager.accountsConfig.altManagerMinecraftAccounts.add(account);
            LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.accountsConfig);

            status = "§aThe account has been added.";
            prevGui.status = status;
            mc.displayGuiScreen(prevGui);
        }).start();
    }
}
