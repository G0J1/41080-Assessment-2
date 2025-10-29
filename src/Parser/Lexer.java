package Parser;

import java.util.ArrayList;
import java.util.List;

public final class LexicalAnalyser {
    // Call this from main
    public static List<Token> analyse(String input) throws NumberException, ExpressionException {
        ArrayList<Token> tokens = new ArrayList<>();
        int i = 0, n = input.length();

        while (i < n) {
            char ch = input.charAt(i);

            // Skip whitespace
            if (Character.isWhitespace(ch)) { i++; continue; }

            // 2-char operator: LET := (optional, if you support it)
            if (ch == ':' && i + 1 < n && input.charAt(i + 1) == '=') {
                tokens.add(new Token(Token.TokenType.LET));
                i += 2;
                continue;
            }

            // Number: integer or decimal (e.g., 42, 0.5, 12.34)
            if (Character.isDigit(ch)) {
                int start = i;
                // integer part
                while (i < n && Character.isDigit(input.charAt(i))) i++;
                boolean isDouble = false;
                // optional fractional part
                if (i < n && input.charAt(i) == '.') {
                    isDouble = true;
                    i++; // consume '.'
                    if (i >= n || !Character.isDigit(input.charAt(i))) {
                        // "12." or "." are invalid numbers for this lexer
                        throw new NumberException();
                    }
                    while (i < n && Character.isDigit(input.charAt(i))) i++;
                }
                String num = input.substring(start, i);
                try {
                    if (isDouble) {
                        tokens.add(new Token(Double.parseDouble(num)));
                    } else {
                        // your Token(int) will set NUMBER
                        tokens.add(new Token(Integer.parseInt(num)));
                    }
                } catch (NumberFormatException e) {
                    throw new NumberException();
                }
                continue;
            }

            // Identifier: letter or underscore, followed by letters/digits/_/-
            if (isIdentStart(ch)) {
                int start = i;
                i++;
                while (i < n && isIdentPart(input.charAt(i))) i++;
                String ident = input.substring(start, i);
                // If you want to recognize "lambda" as λ at lex time, do it here.
                // Otherwise just emit IDENTIFIER; Token has no place to store the name.
                tokens.add(new Token(Token.TokenType.IDENTIFIER));
                continue;
            }

            // Single-character tokens (with ASCII fallbacks for '-' and '*')
            char mapped = mapAsciiFallback(ch); // '-'->'−', '*'->'×', else same
            Token.TokenType t = Token.typeOf(mapped);
            if (t != Token.TokenType.NONE) {
                tokens.add(new Token(t));
                i++;
                continue;
            }

            // Unknown character
            throw new ExpressionException();
        }

        return tokens;
    }

    private static boolean isIdentStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    private static boolean isIdentPart(char c) {
        return Character.isLetterOrDigit(c) || c == '_' || c == '-';
    }

    private static char mapAsciiFallback(char c) {
        // Map ASCII variants to the Unicode symbols your Token.typeOf understands
        if (c == '-') return '−';  // MINUS
        if (c == '*') return '×';  // MULT
        return c;
    }

}
