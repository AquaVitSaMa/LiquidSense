package net.ccbluex.liquidbounce.ui.client.hud.element.elements.extend;

import me.aquavit.liquidsense.utils.render.Translate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import scala.Int;

public class AlphaData {

	public Translate translate = new Translate(0f , 0f);
	public String playername = "";

	public AlphaData (String name ) {
		playername = name;
	}

}
