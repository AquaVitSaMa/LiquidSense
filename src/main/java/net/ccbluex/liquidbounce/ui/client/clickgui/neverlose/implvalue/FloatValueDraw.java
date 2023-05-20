package net.ccbluex.liquidbounce.ui.client.clickgui.neverlose.implvalue;

import me.aquavit.liquidsense.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.ui.client.clickgui.neverlose.Impl;
import net.ccbluex.liquidbounce.ui.client.clickgui.neverlose.module.CustomModule;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import me.aquavit.liquidsense.value.FloatValue;
import me.aquavit.liquidsense.value.Value;
import org.lwjgl.input.Mouse;

import java.awt.Color;

public class FloatValueDraw {
    private boolean isClick = false;
    private CustomModule nModule;
    private FloatValue value;

    public void setClick(boolean click) {
        isClick = click;
    }

    public boolean isClick() {
        return isClick;
    }


    public FloatValueDraw(CustomModule module, Value<?> value) {
        this.nModule = module;
        this.value = (FloatValue) value;
    }

    public void draw() {
        value.floatvalue.translate(value.floatVal, 0f, 1.0f);

        float longValue = 51f;

        double inc = 0.01;

        float max = value.getMaximum();

        float min = value.getMinimum();

        double valAbs = nModule.mouseX - (nModule.positionX + 87f);

        double perc = Math.min(Math.max(0.0, valAbs / (longValue * Math.max(Math.min(value.get() / max, 0f), 1f))), 1.0);

        float valRel = (max - min) * (float) perc;

        value.floatVal = longValue * (value.get() - min) / (max - min);

        if (nModule.main.hovertoFloatL(nModule.positionX + 86f, nModule.positionY + 26.5f + nModule.valuePosY, nModule.positionX + 139f, nModule.positionY + 35.5f + nModule.valuePosY, nModule.mouseX,
                nModule.mouseY, false) && Mouse.isButtonDown(0) && isClick) {

            float idk = min + valRel;

            idk = (float) (Math.round(idk * (1 / inc)) / (1 / inc));

            value.set(idk);
        }

        if (nModule.main.hovertoFloatL(nModule.positionX + 145f, nModule.positionY + 24.5f + nModule.valuePosY, nModule.positionX + 170f, nModule.positionY + 33.5f + nModule.valuePosY , nModule.mouseX,
                nModule.mouseY, true)  && isClick) {
            Editbox.value = value;
            Editbox.modulename = nModule.module.getName();
            Editbox.newvalue = String.valueOf(value.get());
        }

        int rectColor;
        int selectColor;
        Color circleColor;
        int textColor ;
        int borderColor;

        switch (Impl.hue) {
            case "black":
                rectColor = new Color(50, 50, 50).getRGB();
                selectColor = new Color(34, 92, 123).getRGB();
                circleColor = new Color(57, 133, 236);
                textColor = new Color(175, 175, 175).getRGB();
                borderColor = new Color(13, 13, 13).getRGB();
                break;
            case "white":
                rectColor = new Color(230, 230, 230).getRGB();
                selectColor = new Color(3, 168, 245).getRGB();
                circleColor = new Color(3, 168, 245);
                textColor = new Color(90, 90, 90).getRGB();
                borderColor = new Color(255, 255, 255).getRGB();
                break;
            default:
                rectColor = new Color(6, 17, 32).getRGB();
                selectColor = new Color(34, 92, 123).getRGB();
                circleColor = new Color(57, 133, 236);
                textColor = new Color(175, 175, 175).getRGB();
                borderColor = new Color(7, 14, 22).getRGB();
                break;
        }

        RenderUtils.drawRect(nModule.positionX + 87f, nModule.positionY + 29.5f + nModule.valuePosY, nModule.positionX + 138f, nModule.positionY + 30.5f + nModule.valuePosY
                , rectColor); //剩余

        RenderUtils.drawRect(nModule.positionX + 87f, nModule.positionY + 29.5f + nModule.valuePosY, nModule.positionX + 87f + value.floatvalue.getX(), nModule.positionY + 30.5f + nModule.valuePosY
                , selectColor); //已选

        RenderUtils.drawFullCircle(nModule.positionX + 87f + value.floatvalue.getX(), nModule.positionY + 29.5f + nModule.valuePosY, 3f, 0f
                , circleColor);

        Fonts.font15.drawString(value.name, nModule.positionX + 6, nModule.positionY + 27 + nModule.valuePosY, textColor);

        RenderUtils.drawRectBordered(nModule.positionX + 145.0, nModule.positionY + 24.5 + nModule.valuePosY, nModule.positionX + 170.0, nModule.positionY + 33.5 + nModule.valuePosY, 0.7
                , borderColor
                , new Color(21, 31, 41, 100).getRGB());

        boolean check = Editbox.value != null && Editbox.value.getName().equals(value.getName()) && nModule.module.getName().equals(Editbox.modulename);

        nModule.main.drawText(check ? Editbox.newvalue : String.valueOf(value.get()), 5, Fonts.font14, nModule.positionX + 147, nModule.positionY + 28 + nModule.valuePosY,
                textColor);

        nModule.valuePosY += (int)nModule.module.getOpenValue().getY();
    }
}
