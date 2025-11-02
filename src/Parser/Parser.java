package Parser;
import java.util.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Parser {

    private static LinkedList<Token> parseTree = new LinkedList<Token>();

    private enum nonterminals {
        program, expr, parenexpr, $
    }

    ;

    // sets up empty parse table, outer map has non terminals as the key (horizontal row headings) and has the inner table
    // as its value, the inner table forms the vertical rows which are keyed by the tokentype (which is an enum in the token class)
    // and a List<Object> as its value (for each cell) which are the production rules
    public static final EnumMap<nonterminals, EnumMap<Token.TokenType, List<Object>>> parseTable = new EnumMap<>(nonterminals.class);

    static {
        // adds empty rows to the table for each non-terminal
        parseTable.put(nonterminals.program, new EnumMap<>(Token.TokenType.class));
        parseTable.put(nonterminals.expr, new EnumMap<>(Token.TokenType.class));
        parseTable.put(nonterminals.parenexpr, new EnumMap<>(Token.TokenType.class));

        // variables to easily access each row
        EnumMap<Token.TokenType, List<Object>> programRow = parseTable.get(nonterminals.program);
        EnumMap<Token.TokenType, List<Object>> exprRow = parseTable.get(nonterminals.expr);
        EnumMap<Token.TokenType, List<Object>> parenExprRow = parseTable.get(nonterminals.parenexpr);

        // populate production rules for program, it gets the token type as the terminal and a singular list with the expr
        // non-terminal due to the rule program := expr
        programRow.put(Token.TokenType.NUMBER, List.of(nonterminals.expr));
        programRow.put(Token.TokenType.IDENTIFIER, List.of(nonterminals.expr));
        programRow.put(Token.TokenType.LPAREN, List.of(nonterminals.expr));

        // does the same for the expr non terminal, it has the same rules as the program
        exprRow.put(Token.TokenType.NUMBER, List.of(Token.TokenType.NUMBER));
        exprRow.put(Token.TokenType.IDENTIFIER, List.of(Token.TokenType.IDENTIFIER));
        exprRow.put(Token.TokenType.LPAREN, List.of(Token.TokenType.LPAREN, nonterminals.parenexpr, Token.TokenType.RPAREN));

        // then also the production rules for paren-expr
        parenExprRow.put(Token.TokenType.NUMBER, List.of(nonterminals.expr, nonterminals.expr));
        parenExprRow.put(Token.TokenType.IDENTIFIER, List.of(nonterminals.expr, nonterminals.expr));
        parenExprRow.put(Token.TokenType.LPAREN, List.of(nonterminals.expr, nonterminals.expr));
        parenExprRow.put(Token.TokenType.PLUS, List.of(Token.TokenType.PLUS, nonterminals.expr, nonterminals.expr));
        parenExprRow.put(Token.TokenType.MULT, List.of(Token.TokenType.MULT, nonterminals.expr, nonterminals.expr));
        parenExprRow.put(Token.TokenType.EQUALS, List.of(Token.TokenType.EQUALS, nonterminals.expr, nonterminals.expr));
        parenExprRow.put(Token.TokenType.MINUS, List.of(Token.TokenType.MINUS, nonterminals.expr, nonterminals.expr));
        parenExprRow.put(Token.TokenType.CONDITIONAL, List.of(Token.TokenType.CONDITIONAL, nonterminals.expr, nonterminals.expr, nonterminals.expr));
        parenExprRow.put(Token.TokenType.LAMBDA, List.of(Token.TokenType.LAMBDA, Token.TokenType.IDENTIFIER, nonterminals.expr));
        parenExprRow.put(Token.TokenType.LET, List.of(Token.TokenType.LET, Token.TokenType.IDENTIFIER, nonterminals.expr));

    }


    public static List<Token> parse(List<Token> input) throws ExpressionException, NumberException {
        Stack<Object> stack = new Stack<>();

        stack.push(nonterminals.$);
        stack.push(nonterminals.program);
        Token lookahead = input.get(0);
//        System.out.println("stack: " + stack);
        int lookaheadIndex = 0;

        // this is the loop where the actual parsing is gonna happen
        while (!stack.isEmpty()) {

            // pseudo code for stack loop
            Object top = stack.peek();
//            System.out.println("parsetree: " + parseTree);
//            System.out.println("stack: " + stack);
//            System.out.println("top: " + top);
//            System.out.println("lookahead: " + lookahead);

            if (lookaheadIndex < input.size()) {
                lookahead = input.get(lookaheadIndex);
            }

            if (top instanceof Token.TokenType) {
                if (top == lookahead.getType()) {
                    parseTree.add(lookahead);
                    lookaheadIndex++;
                    stack.pop();
                } else {
                    throw new ExpressionException("Unexpected token");
                }
            }
            else if (top == nonterminals.$) {
                System.out.println("Success");
                return parseTree;
            }

            else if (top instanceof nonterminals) {

                List<Object> production = parseTable.get(top).get(lookahead.getType());

                if (production != null) {
                    stack.pop();
//                    System.out.println("production: " + production);
//                    System.out.println("production at index 0: " + production.get(0));
//                    System.out.println("table rule: " + parseTable.get(nonterminals.program).get(Token.TokenType.LPAREN));
//                        System.out.println(i);
//                        System.out.println(production.get(i));
                    for (int i = production.size() - 1; i >= 0; i--) {
                        stack.push(production.get(i));
                    }
                } else {
//                    System.out.println(lookahead.getType());
                    throw new ExpressionException("No rule for " + top + " with lookahead " + lookahead);
                }
            }

            else {
                throw new ExpressionException("Extra input");
            }
        }
        return parseTree;
    }

    public static void printParseTree(List<Token> input) throws ExpressionException, NumberException {
        String printedTree = "";
        parse(input);
        for (int i = 0; i < parseTree.size(); i++) {
            Token token = parseTree.get(i);
            System.out.println(token);
            if (token.isNumber()) {
                printedTree += token.getIntValue();
                if ((i + 1) < parseTree.size()) {
                    if (!(parseTree.get(i + 1).getType() == Token.TokenType.RPAREN)) {
                        printedTree += ", ";
                    }
                }
            }
            else if (token.isIdentifier()) {
                printedTree += "'" + token + "'";
                if ((i + 1) < parseTree.size()) {
                    if (!(parseTree.get(i + 1).getType() == Token.TokenType.RPAREN)) {
                        printedTree += ", ";
                    }
                }
            }
            else if (token.getType() ==  Token.TokenType.RPAREN ||  token.getType() ==  Token.TokenType.LPAREN) {
                printedTree += token;
                if ((i + 1) < parseTree.size()) {
                    if (token.getType() ==  Token.TokenType.RPAREN && !(parseTree.get(i + 1).getType() == Token.TokenType.RPAREN)) {
                        printedTree += ", ";
                    }
                }
            }
            else {
                printedTree += "'" + token.getType() + "'";
                if ((i + 1) < parseTree.size()) {
                    if (!(parseTree.get(i + 1).getType() == Token.TokenType.RPAREN)) {
                        printedTree += ", ";
                    }
                }

            }
        }


        System.out.println("Output: " + printedTree);
    }
}
