package Parser;
import java.util.Optional;

public class Token {
    public enum TokenType {

        NUMBER, IDENTIFIER, PLUS, MINUS, MULT, EQUALS, CONDITIONAL, LAMBDA, LET, LPAREN, RPAREN, NONE;

        public boolean isNumber() {
            switch (this) {
                case NUMBER:
                    return true;
                default:
                    return false;
            }
        }

        public boolean isIdentifier() {
            switch (this) {
                case IDENTIFIER:
                    return true;
                default:
                    return false;
            }
        }

    }

    private TokenType type;
    private double value;
    private String identifier;

    public Token() {
        this.type = TokenType.NONE;
        this.value = Double.NaN;
    }

    public Token(int value) {
        this.type = TokenType.NUMBER;
        this.value = value;
        this.identifier = null;
    }

    public Token(TokenType type) {
        this.type = type;
        this.value = Double.NaN;
        this.identifier = null;
    }

    public Token(String identifier) {
        this.type = TokenType.IDENTIFIER;
        this.value = Double.NaN;
        this.identifier = identifier;
    }

    public boolean isNumber() {
        return type.isNumber();
    }

    public boolean isIdentifier() {return type.isIdentifier(); }

    public TokenType getType() {
        return this.type;
    }

    public Optional<Double> getValue() {
        return this.isNumber() ? Optional.of(value) : Optional.empty();
    }

    public Optional<String> getIdentifierName() { return this.isIdentifier() ? Optional.of(identifier) : Optional.empty(); }

    public static TokenType typeOf(char symbol) {
        switch (symbol) {
            case '+':
                return TokenType.PLUS;
            case '−':
                return TokenType.MINUS;
            case '×':
                return TokenType.MULT;
            case '=':
                return TokenType.EQUALS;
            case '?':
                return TokenType.CONDITIONAL;
            case 'λ':
                return TokenType.LAMBDA;
            case '≜':
                return TokenType.LET;
            case '(':
                return TokenType.LPAREN;
            case ')':
                return TokenType.RPAREN;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return TokenType.NUMBER;
            default:
                return TokenType.NONE;
        }
    }

    @Override
    public String toString() {
        switch (this.type) {
            case NUMBER:
                return getValue().get().toString();
            case PLUS:
                return "+";
            case MINUS:
                return "−";
            case MULT:
                return "×";
            case EQUALS:
                return "=";
            case CONDITIONAL:
                return "?";
            case LAMBDA:
                return "λ";
            case LET:
                return "≜";
            case LPAREN:
                return "[";
            case RPAREN:
                return "]";
            case IDENTIFIER:
                if (identifier != null) {
                    return identifier;
                }
                return "error";
            default:
                return "Error converting token to String.";
        }
    }

    @Override
    public boolean equals(Object other) {

        if (this == other)
            return true;

        if (!(other instanceof Token))
            return false;

        return this.equals((Token) other);
    }

    public boolean equals(Token other) {

        if (!this.type.equals(other.type))
            return false;

        if (this.isNumber() && this.getValue().isPresent())
            return other.getValue().isPresent() && Math.abs(this.getValue().get() - other.getValue().get()) < 0.00001;

        if (this.isNumber() && !this.getValue().isPresent())
            return !other.getValue().isPresent();

        return true;

    }

}

