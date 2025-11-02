package Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Runner {
    public static void main(String[] args) throws NumberException, ExpressionException {
        try {
            String testInput = "(+ 2 x)";
            System.out.println("Input: " + testInput);
            List<Token> tokenizedInput = Lexer.analyse(testInput);
//        System.out.println(tokenizedInput);
//            System.out.println(Parser.parse(tokenizedInput));
            /*Parser.parse();*/
            Parser.printParseTree(tokenizedInput);
        }
        catch(Exception e) {
            System.out.println("Parsing failed");
        }

    }
}
