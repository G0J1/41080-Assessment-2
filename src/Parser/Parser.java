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
        Stack<nonterminals> stack = new Stack<>();

        stack.push(nonterminals.$);
        stack.push(nonterminals.program);
//      lookahead = first token (this is something we have to do before the loop)

        // idk if we'll need this stuff
        nonterminals currentNonTerminal = nonterminals.program;
        terminals currentTerminal = terminals.$;

        // this is the loop where the actual parsing is gonna happen
        while (stack.isEmpty() == false) {
            // psuedo code for stack loop
//            top = stack.pop()
//
//            if top is a terminal:
//                if top matches lookahead:
//                    advance to next token
//                else:
//                    error("unexpected token")
//            else if top is a nonterminal:
//                production = parseTable[top][lookahead.type]
//                if production exists:
//                    push production symbols in reverse order
//                else:
//                    error("no rule for " + top + " with " + lookahead)
//            else if top == $:
//                if lookahead == $:
//                    success!
//                else:
//                    error("extra input")
        }
//        for (Token current : input) {
//            currentTerminal = tokenToTerminal(current);
//
//            //Production Rules
//
//            switch (currentTerminal) {
//                case NUMBER:
//                    //program NUMBER construction rule
//                    if (currentNonTerminals == nonterminals.program) {
//                        stack.pop();
//                        stack.push(nonterminals.expr);
//                        System.out.println("Number read");
//                        currentNonTerminals = nonterminals.expr;
//                    }
//                    //expr NUMBER construction rule
//                    else if (currentNonTerminals == nonterminals.expr) {
//
//                    }
//                    else if(currentNonTerminals == nonterminals.parenexpr) {
//
//                    }
//                    break;
//                case IDENTIFIER:
//                    if (currentNonTerminals == nonterminals.program) {
//                        stack.pop();
//                        stack.push(nonterminals.expr);
//                        System.out.println("Identifier read");
//                        currentNonTerminals = nonterminals.expr;
//                    }
//                    else if (currentNonTerminals == nonterminals.expr) {
//
//                    }
//                    else if(currentNonTerminals == nonterminals.parenexpr) {
//
//                    }
//                    break;
//                case LPAREN:
//                    if (currentNonTerminals == nonterminals.program) {
//                        System.out.println("parenthesis read");
//                        currentNonTerminals = nonterminals.expr;
//                    }
//                    else if (currentNonTerminals == nonterminals.expr) {
//
//                    }
//                    else if(currentNonTerminals == nonterminals.parenexpr) {
//
//                    }
//                    break;
//                case RPAREN:
//                    break;
//                case PLUS:
//                    break;
//                case MULT:
//                    break;
//                case EQUALS:
//                    break;
//                case MINUS:
//                    break;
//                case CONDITIONAL:
//                    break;
//                case LAMBDA:
//                    break;
//                case LET:
//                    break;
//
//                }
//
//            }


//            char[] inputs = input.toCharArray();
//            ArrayList<Token> tokens = new ArrayList<Token>();
//            for (int i = 0; i < input.length(); i++) {
//                tokens.add(new Token(inputs[i]));
//            }
//            stack.pop();
//            stack.push(nonterminals.expr);


//            System.out.println(tokens);


    return Collections.<Object>emptyList();
    }

//    public static terminals tokenToTerminal(Token token) {
//        switch (token.getType()) {
//            case NUMBER:
//                return  terminals.NUMBER;
//            case IDENTIFIER:
//                return  terminals.IDENTIFIER;
//            case LPAREN:
//                return  terminals.LPAREN;
//            case RPAREN:
//                return  terminals.RPAREN;
//            case PLUS:
//                return  terminals.PLUS;
//            case MINUS:
//                return terminals.MINUS;
//            case MULT:
//                return terminals.MULT;
//            case EQUALS:
//                return terminals.EQUALS;
//            case CONDITIONAL:
//                return terminals.CONDITIONAL;
//            case LAMBDA:
//                return terminals.LAMBDA;
//            case LET:
//                return terminals.LET;
//            default:
//                return terminals.$;
//        }
//
//    }
}
