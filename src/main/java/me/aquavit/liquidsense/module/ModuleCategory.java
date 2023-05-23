package me.aquavit.liquidsense.module;

public enum ModuleCategory {
    BLATANT("Blatant"),
    GHOST("Ghost"),
    PLAYER("Player"),
    MOVEMENT("Movement"),
    RENDER("Render"),
    WORLD("World"),
    EXPLOIT("Exploit"),
    MISC("Misc"),
    CLIENT("Client"),
    SCRIPTS("Scripts"),
    FUN("Fun");

    public final String displayName;

    ModuleCategory(final String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}
