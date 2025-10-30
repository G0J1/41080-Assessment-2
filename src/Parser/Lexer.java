package Parser;

import java.util.ArrayList;
import java.util.List;

public final class Lexer{
    // Call this from main
    public static List<Token> analyse(String input) throws NumberException, ExpressionException {
        ArrayList<Token> tokens = new ArrayList<>();
        String buffer = "";
        String symbols = "+×−=?λ≜()";
        for  (int i = 0; i < input.length(); i++){
            char c = input.charAt(i);

            if (Character.isDigit(c) || Character.isLetter(c)) {
                buffer = buffer + c;
            }
            else if (Character.isWhitespace(c)) {
                addToTokenList(buffer, tokens, symbols);

                buffer = "";
            }
            else if (symbols.contains(String.valueOf(c))) {
                if (buffer.length() > 0) {
                    addToTokenList(buffer, tokens, symbols);
                    buffer = "";
                }

                addToTokenList(String.valueOf(c), tokens, symbols);
            }

        }
        if (!(buffer.isEmpty())) {
            addToTokenList(buffer, tokens, symbols);
        }
        return tokens;
    }

    public static void addToTokenList(String num, ArrayList<Token> Tokens, String symbols) {
        boolean isIDENTIFIER = false;
        for (char c : num.toCharArray()) {
            if (Character.isLetter(c)) {
                isIDENTIFIER = true;
            }
        }
//        if (isIDENTITY) {
//            Token newToken = new Token();
//
//        }
        if (num.matches("[0-9]+")) {
            int k = Integer.parseInt(num);
            Token newToken = new Token(k);
            Tokens.add(newToken);
        }
        else if (num.matches("[a-zA-Z][a-zA-Z0-9]*")) {
            System.out.println("tokenize identifier");
            Token newToken = new Token(num);
            Tokens.add(newToken);
        }
        else if (symbols.contains(num) && num.length() == 1) {
            Token newToken = new Token(Token.typeOf(num.charAt(0)));
            System.out.println(newToken.getType());
            Tokens.add(newToken);
        }
    }
}


