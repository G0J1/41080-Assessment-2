package Parser;
import java.util.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Parser {


    private enum nonterminals {
        program, expr, parenexpr, $
    };

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
        exprRow.put(Token.TokenType.NUMBER, List.of(nonterminals.expr));
        exprRow.put(Token.TokenType.IDENTIFIER, List.of(nonterminals.expr));
        exprRow.put(Token.TokenType.LPAREN, List.of(nonterminals.expr));

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


    public static List<Object> parse(List<Token> input) {
        System.out.println("runs!");
        Stack<Object> stack = new Stack<>();

        stack.push(nonterminals.$);
        stack.push(nonterminals.program);
        Object lookahead = stack.peek();


        // this is the loop where the actual parsing is gonna happen
        while (!stack.isEmpty()) {
            // psuedo code for stack loop
           Object top = stack.pop();

            if (!(top instanceof nonterminals)) {
                if (top == lookahead) {
                    stack.peek();
                } else {
                    error("Unexpected token");
                }
            }
            else if (){
                production = parseTable[top][lookahead];

                if production exists{
                    push production symbols in reverse order
                }
                else{
                    error("no rule for " + top + " with " + lookahead)
                }
            else if (top == "$"){
                    if (lookahead == "$"){
                        System.out.println("Success!");
                    }
                }
                else{
                    error("extra input");
                }
                }
            }



    return Collections.<Object>emptyList();
    }
}
