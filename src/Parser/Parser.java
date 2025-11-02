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

    // --- Simple error context (kept minimal) ---
    private static final Stack<String> opNames = new Stack<>();
    private static final Stack<Integer> opExpected = new Stack<>();
    private static int parenDepth = 0;

    private static boolean isExprStart(Token.TokenType t) {
        return t == Token.TokenType.NUMBER
            || t == Token.TokenType.IDENTIFIER
            || t == Token.TokenType.LPAREN;
    }


    private static boolean hasClosingParenAhead(List<Token> input, int fromIndex) {
        for (int i = fromIndex; i < input.size(); i++) {
            Token t = input.get(i);
            if (t != null && t.getType() == Token.TokenType.RPAREN) return true;
        }
        return false;
    }

    public static final EnumMap<nonterminals, EnumMap<Token.TokenType, List<Object>>> parseTable = new EnumMap<>(nonterminals.class);

    static {
        parseTable.put(nonterminals.program, new EnumMap<>(Token.TokenType.class));
        parseTable.put(nonterminals.expr, new EnumMap<>(Token.TokenType.class));
        parseTable.put(nonterminals.parenexpr, new EnumMap<>(Token.TokenType.class));

        EnumMap<Token.TokenType, List<Object>> programRow = parseTable.get(nonterminals.program);
        EnumMap<Token.TokenType, List<Object>> exprRow = parseTable.get(nonterminals.expr);
        EnumMap<Token.TokenType, List<Object>> parenExprRow = parseTable.get(nonterminals.parenexpr);

        programRow.put(Token.TokenType.NUMBER, List.of(nonterminals.expr));
        programRow.put(Token.TokenType.IDENTIFIER, List.of(nonterminals.expr));
        programRow.put(Token.TokenType.LPAREN, List.of(nonterminals.expr));

        exprRow.put(Token.TokenType.NUMBER, List.of(Token.TokenType.NUMBER));
        exprRow.put(Token.TokenType.IDENTIFIER, List.of(Token.TokenType.IDENTIFIER));
        exprRow.put(Token.TokenType.LPAREN, List.of(Token.TokenType.LPAREN, nonterminals.parenexpr, Token.TokenType.RPAREN));

        parenExprRow.put(Token.TokenType.NUMBER, List.of(nonterminals.expr, nonterminals.expr));
        parenExprRow.put(Token.TokenType.IDENTIFIER, List.of(nonterminals.expr, nonterminals.expr));
        parenExprRow.put(Token.TokenType.LPAREN, List.of(nonterminals.expr, nonterminals.expr));
        parenExprRow.put(Token.TokenType.PLUS, List.of(Token.TokenType.PLUS, nonterminals.expr, nonterminals.expr));
        parenExprRow.put(Token.TokenType.MULT, List.of(Token.TokenType.MULT, nonterminals.expr, nonterminals.expr));
        parenExprRow.put(Token.TokenType.EQUALS, List.of(Token.TokenType.EQUALS, nonterminals.expr, nonterminals.expr));
        parenExprRow.put(Token.TokenType.MINUS, List.of(Token.TokenType.MINUS, nonterminals.expr, nonterminals.expr));
        parenExprRow.put(Token.TokenType.CONDITIONAL, List.of(Token.TokenType.CONDITIONAL, nonterminals.expr, nonterminals.expr, nonterminals.expr));
        parenExprRow.put(Token.TokenType.LAMBDA, List.of(Token.TokenType.LAMBDA, Token.TokenType.IDENTIFIER, nonterminals.expr));
        parenExprRow.put(Token.TokenType.LET, List.of(Token.TokenType.LET, Token.TokenType.IDENTIFIER, nonterminals.expr, nonterminals.expr));
    }

    public static List<Token> parse(List<Token> input) throws ExpressionException, NumberException {
        Stack<Object> stack = new Stack<>();
        parseTree.clear();

        opNames.clear();
        opExpected.clear();
        parenDepth = 0;

        stack.push(nonterminals.$);
        stack.push(nonterminals.program);
        Token lookahead = input.get(0);
        int lookaheadIndex = 0;

        while (!stack.isEmpty()) {
            Object top = stack.peek();

            if (lookaheadIndex < input.size()) {
                lookahead = input.get(lookaheadIndex);
            } else {
                lookahead = null;
            }

            if (lookahead == null && parenDepth > 0) {
                throw new ExpressionException("Unmatched parenthesis: missing ')' before end of input.");
            }

            if (top instanceof Token.TokenType) {
                if (lookahead != null && top == lookahead.getType()) {

                    if (top == Token.TokenType.LPAREN) {
                        parenDepth++;
                        opNames.push("apply");
                        opExpected.push(-1);
                    } else if (top == Token.TokenType.RPAREN) {
                        parenDepth--;
                        if (parenDepth < 0) {
                            throw new ExpressionException("Unmatched ')' at position " + lookaheadIndex + ".");
                        }
                        if (!opNames.isEmpty()) { opNames.pop(); opExpected.pop(); }
                    }

                    parseTree.add(lookahead);
                    lookaheadIndex++;
                    stack.pop();
                } else {

                    if (top == Token.TokenType.RPAREN
                            && lookahead != null
                            && isExprStart(lookahead.getType())
                            && !opNames.isEmpty()) {
                        String op = opNames.peek();
                        int expect = opExpected.peek();
                        if (expect > 0 && !"apply".equals(op)) {
                            if (!hasClosingParenAhead(input, lookaheadIndex)) {
                                throw new ExpressionException("Missing ')' to close an opening '(' (reached end of input).");
                            }
                            throw new ExpressionException("Wrong number of arguments to '"
                                    + op + "': expected " + expect + ", but found extra argument before ')'.");
                        }
                    }
                    else if (lookahead != null && lookahead.getType() == Token.TokenType.RPAREN && parenDepth == 0) {
                        throw new ExpressionException("Unmatched ')' at position " + lookaheadIndex + ".");
                    }
                    else {
                        throw new ExpressionException("Unexpected token");
                    }

                }
            }
            else if (top == nonterminals.$) {
                if (parenDepth != 0) {
                    throw new ExpressionException("Missing ')' to close an opening '(' (reached end of input).");
                }
                return parseTree;
            }
            else if (top instanceof nonterminals) {
                List<Object> production = (lookahead == null) ? null : parseTable.get(top).get(lookahead.getType());

                if (production != null) {

                    if (top == nonterminals.parenexpr && lookahead != null) {
                        Token.TokenType la = lookahead.getType();
                        if (!opNames.isEmpty()) { opNames.pop(); opExpected.pop(); }

                        if (la == Token.TokenType.PLUS || la == Token.TokenType.MINUS
                                || la == Token.TokenType.MULT || la == Token.TokenType.EQUALS) {
                            opNames.push(la == Token.TokenType.PLUS ? "+"
                                : la == Token.TokenType.MINUS ? "−"
                                : la == Token.TokenType.MULT ? "×" : "=");
                            opExpected.push(2);
                        } else if (la == Token.TokenType.CONDITIONAL) {
                            opNames.push("?");
                            opExpected.push(3);
                        } else if (la == Token.TokenType.LAMBDA) {
                            opNames.push("λ");
                            opExpected.push(2);
                        } else if (la == Token.TokenType.LET) {
                            opNames.push("≜");     // ≜ IDENT expr expr
                            opExpected.push(3);
                        } else if (isExprStart(la)) {
                            opNames.push("apply");
                            opExpected.push(-1);
                        } else {
                            opNames.push("apply");
                            opExpected.push(-1);
                        }
                    }

                    stack.pop();
                    for (int i = production.size() - 1; i >= 0; i--) stack.push(production.get(i));
                } else {

                    if (top == nonterminals.expr
                            && lookahead != null
                            && lookahead.getType() == Token.TokenType.RPAREN
                            && !opNames.isEmpty()) {
                        String op = opNames.peek();
                        int expect = opExpected.peek();
                        if (expect > 0 && !"apply".equals(op)) {
                            throw new ExpressionException("Wrong number of arguments to '"
                                    + op + "': not enough arguments before ')'.");
                        }
                    }
                    if (lookahead == null && parenDepth > 0) {
                        throw new ExpressionException("Missing ')' to close an opening '(' (reached end of input).");
                    }
                    throw new ExpressionException("No rule for " + top + " with lookahead " + lookahead);
                }
            }
            else {
                throw new ExpressionException("Extra input");
            }
        }

        if (parenDepth > 0) {
            throw new ExpressionException("Missing ')' to close an opening '(' (reached end of input).");
        }
        return parseTree;
    }

    public static String parseTreeToString(List<Token> input) throws ExpressionException, NumberException {
        String printedTree = "";
        parse(input);
        for (int i = 0; i < parseTree.size(); i++) {
            Token token = parseTree.get(i);
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
        return printedTree;
    }
}
