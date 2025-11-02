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

    // ==== INSERT A: error-context helpers ====
    private static final Deque<OpCtx> opStack = new ArrayDeque<>();
    private static int parenDepth = 0;

    private static final class OpCtx {
        final String name;             // "+", "−", "×", "=", "?", "λ", "≜", "apply"
        final int expectedExprStarts;  // -1 = variable-arity
        OpCtx(String name, int expectedExprStarts) {
            this.name = name;
            this.expectedExprStarts = expectedExprStarts;
        }
    }

    private static boolean isExprStart(Token.TokenType t) {
        return t == Token.TokenType.NUMBER
            || t == Token.TokenType.IDENTIFIER
            || t == Token.TokenType.LPAREN;
    }

    private static String tokenRepr(Token t) {
        return (t == null) ? "EOF" : t.toString();
    }
    // ==== END INSERT A ====

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
        parenExprRow.put(Token.TokenType.LET, List.of(Token.TokenType.LET, Token.TokenType.IDENTIFIER, nonterminals.expr, nonterminals.expr));
    }

    public static List<Token> parse(List<Token> input) throws ExpressionException, NumberException {
        Stack<Object> stack = new Stack<>();
        parseTree.clear();

        // ==== INSERT B: reset error-tracking ====
        opStack.clear();
        parenDepth = 0;
        // ==== END INSERT B ====

        stack.push(nonterminals.$);
        stack.push(nonterminals.program);
        Token lookahead = input.get(0);
        int lookaheadIndex = 0;

        // this is the loop where the actual parsing is gonna happen
        while (!stack.isEmpty()) {

            // pseudo code for stack loop
            Object top = stack.peek();

            if (lookaheadIndex < input.size()) {
                lookahead = input.get(lookaheadIndex);
            }

            if (top instanceof Token.TokenType) {
                if (top == lookahead.getType()) {

                    // ==== INSERT C1: track parens and unmatched ')' ====
                    if (top == Token.TokenType.LPAREN) {
                        parenDepth++;
                        // push placeholder; actual op set when parenexpr expands
                        opStack.push(new OpCtx("apply", -1));
                    } else if (top == Token.TokenType.RPAREN) {
                        parenDepth--;
                        if (parenDepth < 0) {
                            throw new ExpressionException("Unmatched ')' at position " + lookaheadIndex + ".");
                        }
                        if (!opStack.isEmpty()) opStack.pop();
                    }
                    // ==== END INSERT C1 ====

                    parseTree.add(lookahead);
                    lookaheadIndex++;
                    stack.pop();
                } else {

                    // ==== INSERT C2: tailored terminal mismatches ====
                    // Too many args: we expected ')' but saw another expr-start
                    if (top == Token.TokenType.RPAREN
                            && isExprStart(lookahead.getType())
                            && !opStack.isEmpty()) {
                        OpCtx ctx = opStack.peek();
                        if (ctx != null && ctx.expectedExprStarts > 0 && !"apply".equals(ctx.name)) {
                            throw new ExpressionException(
                                "Wrong number of arguments to '" + ctx.name
                                + "': expected " + ctx.expectedExprStarts
                                + ", but found extra argument before ')'."
                            );
                        }
                    }
                    // Unmatched ')' at top level
                    if (lookahead.getType() == Token.TokenType.RPAREN && parenDepth == 0) {
                        throw new ExpressionException("Unmatched ')' at position " + lookaheadIndex + ".");
                    }
                    // ==== END INSERT C2 ====

                    throw new ExpressionException("Unexpected token");
                }
            }
            else if (top == nonterminals.$) {
                // ==== INSERT E: final paren check ====
                if (parenDepth != 0) {
                    throw new ExpressionException("Missing ')' to close an opening '(' (reached end of input).");
                }
                // ==== END INSERT E ====
                return parseTree;
            }
            else if (top instanceof nonterminals) {

                List<Object> production = parseTable.get(top).get(lookahead.getType());

                if (production != null) {

                    // ==== INSERT D1: set operator context for parenexpr ====
                    if (top == nonterminals.parenexpr) {
                        Token.TokenType la = lookahead.getType();
                        // replace the placeholder pushed when '(' matched
                        if (!opStack.isEmpty()) opStack.pop();

                        if (la == Token.TokenType.PLUS || la == Token.TokenType.MINUS
                                || la == Token.TokenType.MULT || la == Token.TokenType.EQUALS) {
                            String name = (la == Token.TokenType.PLUS) ? "+"
                                    : (la == Token.TokenType.MINUS) ? "−"
                                    : (la == Token.TokenType.MULT) ? "×" : "=";
                            opStack.push(new OpCtx(name, 2));
                        } else if (la == Token.TokenType.CONDITIONAL) {
                            opStack.push(new OpCtx("?", 3));
                        } else if (la == Token.TokenType.LAMBDA) {
                            opStack.push(new OpCtx("λ", 2));           // λ IDENT expr
                        } else if (la == Token.TokenType.LET) {
                            opStack.push(new OpCtx("≜", 3));           // ≜ IDENT expr expr
                        } else if (isExprStart(la)) {
                            opStack.push(new OpCtx("apply", -1));      // function application: variable-arity
                        } else {
                            opStack.push(new OpCtx("apply", -1));      // safe default
                        }
                    }
                    // ==== END INSERT D1 ====

                    stack.pop();
                    for (int i = production.size() - 1; i >= 0; i--) {
                        stack.push(production.get(i));
                    }
                } else {
                    // ==== INSERT D2: not enough args & missing ')' ====
                    // Not enough args: we expected an expr but saw ')'
                    if (top == nonterminals.expr
                            && lookahead.getType() == Token.TokenType.RPAREN
                            && !opStack.isEmpty()) {
                        OpCtx ctx = opStack.peek();
                        if (ctx != null && ctx.expectedExprStarts > 0 && !"apply".equals(ctx.name)) {
                            throw new ExpressionException(
                                "Wrong number of arguments to '" + ctx.name
                                + "': not enough arguments before ')'."
                            );
                        }
                    }
                    // End-of-input while expecting more → likely missing ')'
                    if (lookaheadIndex >= input.size()) {
                        if (parenDepth > 0) {
                            throw new ExpressionException("Missing ')' to close an opening '(' (reached end of input).");
                        }
                    }
                    // ==== END INSERT D2 ====

                    // original throw
                    throw new ExpressionException("No rule for " + top + " with lookahead " + lookahead);
                }
            }
            else {
                throw new ExpressionException("Extra input");
            }
        }

        // ==== INSERT F: safety net ====
        if (parenDepth > 0) {
            throw new ExpressionException("Missing ')' to close an opening '(' (reached end of input).");
        }
        // ==== END INSERT F ====

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
