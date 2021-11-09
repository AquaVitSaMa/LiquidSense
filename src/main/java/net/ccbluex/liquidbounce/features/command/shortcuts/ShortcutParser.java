package net.ccbluex.liquidbounce.features.command.shortcuts;

import net.ccbluex.liquidbounce.features.command.shortcuts.tokens.Literal;
import net.ccbluex.liquidbounce.features.command.shortcuts.tokens.StatementEnd;

import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;

public final class ShortcutParser {
    private static final int SEPARATOR;
    public static final ShortcutParser INSTANCE;

    static {
        INSTANCE = new ShortcutParser();
        String string = ";";
        SEPARATOR = string.codePointAt(0);
    }

    public final List<List<String>> parse(String script) {
        List<Token> tokens = this.tokenize(script);

        List<List<String>> parsed = new ArrayList<>();
        List<String> tmpStatement = new ArrayList<>();

        for (Token token : tokens) {
            if (token instanceof Literal) {
                tmpStatement.add(((Literal) token).getLiteral());
            } else if (token instanceof StatementEnd){
                parsed.add(tmpStatement);
                tmpStatement.clear();
            }
        }

        if (!tmpStatement.isEmpty())
            throw new IllegalArgumentException("Unexpected end of statement!");

        return parsed;
    }

    private List<Token> tokenize(String script) {
        ArrayList<Token> tokens = new ArrayList<>();
        StringBuilder tokenBuf = new StringBuilder();

        PrimitiveIterator.OfInt ofInt = script.codePoints().iterator();
        while (ofInt.hasNext()) {
            Integer code = ofInt.next();
            if (Character.isWhitespace(code)) {
                this.finishLiteral(tokens, tokenBuf);
            } else if (code == SEPARATOR) {
                this.finishLiteral(tokens, tokenBuf);
                StatementEnd statementEnd = new StatementEnd();
                tokens.add(statementEnd);
            } else {
                tokenBuf.appendCodePoint(code);
            }
        }

        if (tokenBuf.length() > 0)
            throw new IllegalArgumentException("Unexpected end of literal!");

        return tokens;
    }

    private void finishLiteral(ArrayList<Token> tokens, StringBuilder tokenBuf) {
        if (tokenBuf.length() > 0) {
            tokens.add(new Literal(tokenBuf.toString()));
            tokenBuf.setLength(0);
        }
    }
}
