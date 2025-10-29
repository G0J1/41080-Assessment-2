package Token;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private int position;

    public Lexer(String source) {
        this.source = source;
        this.position = 0;
    }

    public List<Object> tokenize() {
        List<Object> tokens = new ArrayList<>();

        while (position < source.length()) {
            char current = source.charAt(position);

            if (Character.isWhitespace(current)) {
                position++;
                continue;
            }

            switch (current) {
                case '(': tokens.add("LPAREN"); position++; break;
                case ')': tokens.add("RPAREN"); position++; break;
                case '+': tokens.add("PLUS"); position++; break;
                case '=': tokens.add("EQUAL"); position++; break;
                case '?': tokens.add("IF"); position++; break;
                case 'λ': tokens.add("LAMBDA"); position++; break;
                case '≜': tokens.add("DEFINE"); position++; break;
                case '−': tokens.add("MINUS"); position++; break;
                case '×': tokens.add("MULT"); position++; break;
                default:
                    if (Character.isDigit(current)) {
                        tokens.add(Integer.parseInt(readNumber()));
                    } else if (Character.isLetter(current)) {
                        tokens.add(readIdentifier());
                    } else {
                        throw new RuntimeException("Unexpected character: '" + current + "' at position " + position);
                    }
                    break;
            }
        }

        return tokens;
    }

    private String readNumber() {
        int start = position;
        while (position < source.length() && Character.isDigit(source.charAt(position))) {
            position++;
        }
        return source.substring(start, position);
    }

    private String readIdentifier() {
        int start = position;
        while (position < source.length() && Character.isLetterOrDigit(source.charAt(position))) {
            position++;
        }
        return source.substring(start, position);
    }

    public static void main(String[] args) {
        String[] testInputs = {
                "(+ 2 x)",
                "(× (+ 1 2) 3)",
                "(λ x (+ x 1))"
        };

        for (String input : testInputs) {
            Lexer lexer = new Lexer(input);
            List<Object> tokens = lexer.tokenize();

            System.out.println("Input: " + input);
            System.out.print("Output: [");
            for (int i = 0; i < tokens.size(); i++) {
                Object token = tokens.get(i);
                if (token instanceof String && !((String) token).equals("LPAREN") && !((String) token).equals("RPAREN")) {
                    System.out.print("'" + token + "'");
                } else {
                    System.out.print(token);
                }
                if (i != tokens.size() - 1) System.out.print(", ");
            }
            System.out.println("]\n");
        }
    }
}
