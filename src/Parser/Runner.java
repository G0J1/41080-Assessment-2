package Parser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Runner {
    static class TestResult {
        String input;
        String output;

        public TestResult(String input, String output) {
            this.input = input;
            this.output = output;
        }
    }


    public static void main(String[] args) throws NumberException, ExpressionException {
        ArrayList<TestResult> testResults = new ArrayList<>();

        try {
            String basicTest1 = "y";
            String basicTest2 = "67";
            String basicTest3 = "(− 2 2)";
            String basicTest4 = "(= x 5)";

            String nestedTest1 = "(+ (+ 4 2) 0)";
            String nestedTest2 = "(? (+ 1 z) 5 9)";

            String functionTest1 = "(≜ x 5 7)";
            String functionTest2 = "(λ a 5)";
            String functionTest3 = "((λ j (+ x 8)) 7)";

            String errorTest1 = "(+ 2 1";
            String errorTest2 = ")";
            String errorTest3 = "(− 2 x a)";


            ArrayList<String> basicTestCases = new ArrayList<>();
            ArrayList<String> basicTestCaseOutputs = new ArrayList<>();

            ArrayList<String> nestedTestCases = new ArrayList<>();
            ArrayList<String> nestedTestCaseOutputs = new ArrayList<>();

            ArrayList<String> functionTestCases = new ArrayList<>();
            ArrayList<String> functionTestCaseOutputs = new ArrayList<>();

            ArrayList<String> errorTestCases = new ArrayList<>();
            ArrayList<String> errorTestCaseOutputs = new ArrayList<>();

            basicTestCases.add(basicTest1);
            basicTestCases.add(basicTest2);
            basicTestCases.add(basicTest3);
            basicTestCases.add(basicTest4);

            nestedTestCases.add(nestedTest1);
            nestedTestCases.add(nestedTest2);

            functionTestCases.add(functionTest1);
            functionTestCases.add(functionTest2);
            functionTestCases.add(functionTest3);

            errorTestCases.add(errorTest1);
            errorTestCases.add(errorTest2);
            errorTestCases.add(errorTest3);

            for (String test : basicTestCases) {
                String output = Parser.parseTreeToString(Lexer.analyse(test));
                System.out.println("Input: " + test);
                System.out.println("Output: " + output);
                testResults.add(new TestResult(test, output));
            }

            for (String test : nestedTestCases) {
                String output = Parser.parseTreeToString(Lexer.analyse(test));
                System.out.println("Input: " + test);
                System.out.println("Output: " + output);
                testResults.add(new TestResult(test, output));
            }

            for (String test : functionTestCases) {
                String output = Parser.parseTreeToString(Lexer.analyse(test));
                System.out.println("Input: " + test);
                System.out.println("Output: " + output);
                testResults.add(new TestResult(test, output));
            }

            for (String test : errorTestCases) {
                try {
                    System.out.println("Input: " + test);
                    String output = Parser.parseTreeToString(Lexer.analyse(test));
                    System.out.println("Output: " + output);
//                testResults.add(new TestResult(test, output));
                }
                catch (Exception e) {
                    System.out.println(e.getMessage());
                }

            }

        }
        catch(Exception e) {
            System.out.println(e.getMessage());
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        String jsonOutput = gson.toJson(testResults);


        try (FileWriter file = new FileWriter("output.json")) {
            file.write(jsonOutput);
            System.out.println("JSON written to output.json");
        } catch (IOException e) {
            System.out.println("Error writing JSON: " + e.getMessage());
        }
    }

}

