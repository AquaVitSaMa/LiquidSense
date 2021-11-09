package net.ccbluex.liquidbounce.features.command.shortcuts.tokens;

import net.ccbluex.liquidbounce.features.command.shortcuts.Token;

public class Literal extends Token {
    private final String literal;

    public final String getLiteral() {
        return this.literal;
    }

    public Literal(String literal) {
        this.literal = literal;
    }
}
