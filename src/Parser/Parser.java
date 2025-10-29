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
    }

    ;

    private enum terminals {
        NUMBER, IDENTIFIER, LPAREN, RPAREN, PLUS, MULTI, MINUS, EQUALS, CONDITIONAL, LAMBDA, LET, $
    }

    ;


    public static List<Object> parse(List<Token> input) {
        System.out.println("runs!");
        Stack<nonterminals> stack = new Stack<>();

        stack.push(nonterminals.$);
        stack.push(nonterminals.program);

        nonterminals currentNonTerminals = nonterminals.program;
        terminals currentTerminal = null;

        for (Token current : input) {
//            char c = input.charAt(i);
//
//            if(Character.isDigit(c)){
//                currentTerminal = terminals.NUMBER;
//            }
//            else if(Character.isLetter(c)){
//                currentTerminal =  terminals.IDENTIFIER;
//            }

//            switch (c) {
//                case '(':
//                    currentTerminal = terminals.LPAREN;
//                    break;
//                case ')':
//                    currentTerminal = terminals.RPAREN;
//                    break;
//                case '+':
//                    currentTerminal = terminals.PLUS;
//                    break;
//                case '=':
//                    currentTerminal = terminals.EQUALS;
//                    break;
//                case '×':
//                    currentTerminal = terminals.MULTI;
//                    break;
//                case '-':
//                    currentTerminal = terminals.MINUS;
//                    break;
//                case '?':
//                    currentTerminal = terminals.CONDITIONAL;
//                    break;
//                case 'λ':
//                    currentTerminal = terminals.LAMBDA;
//                    break;
//                case '≜':
//                    currentTerminal = terminals.LET;
//                    break;
//                default:
//                    break;
//            }

            //Production Rules

            switch (currentTerminal) {
                case NUMBER:
                    //program NUMBER construction rule
                    if (currentNonTerminals == nonterminals.program) {
                        stack.pop();
                        stack.push(nonterminals.expr);
                        System.out.println("Number read");
                        currentNonTerminals = nonterminals.expr;
                    }
                    //expr NUMBER construction rule
                    else if (currentNonTerminals == nonterminals.expr) {

                    }
                    else if(currentNonTerminals == nonterminals.parenexpr) {

                    }
                    break;
                case IDENTIFIER:
                    if (currentNonTerminals == nonterminals.program) {
                        stack.pop();
                        stack.push(nonterminals.expr);
                        System.out.println("Identifier read");
                        currentNonTerminals = nonterminals.expr;
                    }
                    else if (currentNonTerminals == nonterminals.expr) {

                    }
                    else if(currentNonTerminals == nonterminals.parenexpr) {

                    }
                    break;
                case LPAREN:
                    if (currentNonTerminals == nonterminals.program) {
                        System.out.println("parenthesis read");
                        currentNonTerminals = nonterminals.expr;
                    }
                    else if (currentNonTerminals == nonterminals.expr) {

                    }
                    else if(currentNonTerminals == nonterminals.parenexpr) {

                    }
                    break;
                case RPAREN:
                    break;
                case PLUS:
                    break;
                case MULTI:
                    break;
                case EQUALS:
                    break;
                case MINUS:
                    break;
                case CONDITIONAL:
                    break;
                case LAMBDA:
                    break;
                case LET:
                    break;

                }

            }


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
}
