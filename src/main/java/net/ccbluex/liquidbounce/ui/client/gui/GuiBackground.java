package net.ccbluex.liquidbounce.ui.client.gui;

import me.aquavit.liquidsense.utils.misc.MiscUtils;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class GuiBackground extends GuiScreen {

    private GuiNewButton enabledButton;
    private GuiNewButton particlesButton;

    private final GuiScreen prevGui;

    private static boolean enabled = true;
    private static boolean particles = false;
    public static final Companion Companion = new Companion();

    public GuiBackground(GuiScreen prevGui) {
        this.prevGui = prevGui;
    }

    public void initGui() {
        enabledButton = new GuiNewButton(1, width / 2 - 100, height / 4 + 35, 200, 20,"Enabled (" + (enabled ? "On" : "Off") + ")");
        buttonList.add(enabledButton);
        particlesButton = new GuiNewButton(2, width / 2 - 100, height / 4 + 50 + 25, 200, 20,"Particles (" + (particles ? "On" : "Off") + ")");
        buttonList.add(particlesButton);
        buttonList.add(new GuiNewButton(3, width / 2 - 100, height / 4 + 50 + 25 * 2, 98, 20, "Change wallpaper"));
        buttonList.add(new GuiNewButton(4, width / 2 + 2, height / 4 + 50 + 25 * 2, 98, 20, "Reset wallpaper"));
        buttonList.add(new GuiNewButton(0, width / 2 - 100, height / 4 + 55 + 25 * 4 + 5, 200, 20,"Back"));
        super.initGui();
    }

    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1: {
                enabled = !enabled;
                this.enabledButton.displayString = "Enabled (" + (enabled ? "On" : "Off") + ')';
                break;
            }
            case 2: {
                particles = !particles;
                this.particlesButton.displayString = "Particles (" + (particles ? "On" : "Off") + ')';
                break;
            }
            case 3: {
                File file = MiscUtils.openFileChooser();
                if (file == null) {
                    return;
                }
                if (file.isDirectory()) {
                    return;
                }
                try {
                    Files.copy(file.toPath(), new FileOutputStream(LiquidBounce.INSTANCE.getFileManager().backgroundFile));
                    BufferedImage image = ImageIO.read(new FileInputStream(LiquidBounce.INSTANCE.getFileManager().backgroundFile));
                    LiquidBounce.INSTANCE.setBackground(new ResourceLocation(LiquidBounce.CLIENT_NAME.toLowerCase() + "/background.png"));
                    mc.getTextureManager().loadTexture(LiquidBounce.INSTANCE.getBackground(), (ITextureObject)new DynamicTexture(image));
                } catch (Exception e) {
                    e.printStackTrace();
                    MiscUtils.showErrorPopup((String)"Error", (String)("Exception class: " + e.getClass().getName() + "\nMessage: " + e.getMessage()));
                    LiquidBounce.fileManager.backgroundFile.delete();
                }
                break;
            }
            case 4: {
                LiquidBounce.INSTANCE.setBackground(null);
                LiquidBounce.fileManager.backgroundFile.delete();
                break;
            }
            case 0: {
                this.mc.displayGuiScreen(this.prevGui);
            }
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawBackground(0);
        Fonts.logo.drawCenteredString("Background", (float)this.width / 2.0f, (float)this.height / 8.0f + 5.0f, 4673984, true);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if (Keyboard.KEY_ESCAPE == keyCode) {
            this.mc.displayGuiScreen(this.prevGui);
            return;
        }
        super.keyTyped(typedChar, keyCode);
    }

    public static final class Companion {
        public final boolean getEnabled() {
            return enabled;
        }

        public final void setEnabled(boolean state) {
            enabled = state;
        }

        public final boolean getParticles() {
            return particles;
        }

        public final void setParticles(boolean state) {
            particles = state;
        }

        private Companion() {
        }
    }
}
