package me.aquavit.liquidsense.ui.client.hud.designer;

import net.ccbluex.liquidbounce.LiquidBounce;
import me.aquavit.liquidsense.ui.client.hud.element.Element;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class GuiHudDesigner extends GuiScreen {

    private EditorPanel editorPanel = new EditorPanel(this, 2, 2);
    private Element selectedElement;
    private boolean buttonAction;

    public final Element getSelectedElement() {
        return this.selectedElement;
    }

    public final void setSelectedElement(Element element) {
        this.selectedElement = element;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.editorPanel = new EditorPanel(this, this.width / 2, this.height / 2);
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        LiquidBounce.hud.render(true);
        LiquidBounce.hud.handleMouseMove(mouseX, mouseY);

        if (!LiquidBounce.hud.elements.contains(selectedElement))
            selectedElement = null;

        int wheel = Mouse.getDWheel();

        editorPanel.drawPanel(mouseX, mouseY, wheel);

        if (wheel != 0) {
            for (Element element : LiquidBounce.hud.elements) {
                if (element.isInBorder(mouseX / element.getScale() - element.getRenderX(),
                        mouseY / element.getScale() - element.getRenderY())) {
                    element.setScale(element.getScale() + (wheel > 0 ? 0.05f : -0.05f));
                    break;
                }
            }
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (buttonAction) {
            buttonAction = false;
            return;
        }

        LiquidBounce.hud.handleMouseClick(mouseX, mouseY, mouseButton);

        if (!(mouseX >= editorPanel.x && mouseX <= editorPanel.x + editorPanel.width && mouseY >= editorPanel.y &&
                mouseY <= editorPanel.y + Math.min(editorPanel.realHeight, 200))) {
            selectedElement = null;
            editorPanel.create = false;
        }

        if (mouseButton == 0) {
            for (Element element : LiquidBounce.hud.elements) {
                if (element.isInBorder(mouseX / element.getScale() - element.getRenderX(), mouseY / element.getScale() - element.getRenderY())) {
                    this.selectedElement = element;
                    break;
                }
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        LiquidBounce.hud.handleMouseReleased();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        LiquidBounce.fileManager.saveConfig(LiquidBounce.fileManager.hudConfig);
        super.onGuiClosed();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        switch (keyCode) {
            case Keyboard.KEY_DELETE:{
                if (selectedElement != null) {
                    LiquidBounce.hud.removeElement(selectedElement);
                }
                break;
            }
            case Keyboard.KEY_ESCAPE:{
                selectedElement = null;
                editorPanel.create = false;
                break;
            }
            default: {
                LiquidBounce.hud.handleKey(typedChar, keyCode);
            }
        }

        super.keyTyped(typedChar, keyCode);
    }
}
