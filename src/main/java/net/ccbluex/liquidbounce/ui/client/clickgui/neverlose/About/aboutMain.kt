package net.ccbluex.liquidbounce.ui.client.clickgui.neverlose.Aboutimport net.ccbluex.liquidbounce.ui.client.neverlose.Implimport net.ccbluex.liquidbounce.ui.client.neverlose.Mainimport net.ccbluex.liquidbounce.ui.font.Fontsimport me.aquavit.liquidsense.utils.render.RenderUtilsimport java.awt.Colorobject aboutMain : Main() {    fun drawabout(mouseX : Int , mouseY : Int , main : Main) {        Fonts.csgo40.drawString("G" , Impl.coordinateX + 410 , Impl.coordinateY + 14 , -1)        if(main.hoverConfig(Impl.coordinateX + 410 , Impl.coordinateY + 14 , Impl.coordinateX + 420 , Impl.coordinateY + 26 , mouseX , mouseY , true)) {            Impl.openAbout = !Impl.openAbout        }        if(Impl.openAbout) {           RenderUtils.drawNLRect(Impl.coordinateX + 450  , Impl.coordinateY  + 50, Impl.coordinateX + 550 , Impl.coordinateY + 250 , 3f , Color(6, 16, 28).rgb)        }    } }