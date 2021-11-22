package net.ccbluex.liquidbounce.ui.client.hud.element.elements.extend;

public class AlphaData {

    private String playerName;
    private int alpha;

    public AlphaData(String playerName, int alpha) {
        this.playerName = playerName;
        this.alpha = alpha;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getAlpha() {
        return alpha;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

}
