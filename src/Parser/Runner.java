package Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Runner {
    public static void main(String[] args) throws NumberException, ExpressionException {
        String testInput = " + 1 2";
        List<Token> tokenizedInput = Lexer.analyse(testInput);
        Parser.parse(tokenizedInput);
//        Parser.parse();
    }
}
