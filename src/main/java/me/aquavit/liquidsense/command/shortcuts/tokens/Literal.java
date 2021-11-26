package me.aquavit.liquidsense.command.shortcuts.tokens;

import me.aquavit.liquidsense.command.shortcuts.Token;

public class Literal extends Token {
    private final String literal;

    public final String getLiteral() {
        return this.literal;
    }

    public Literal(String literal) {
        this.literal = literal;
    }
}
