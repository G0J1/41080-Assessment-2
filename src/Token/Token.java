package Token;

public class Token {
    public final String type;
    public final String value;

    public Token(String type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("Token(type='%s', value='%s')", type, value);
    }
}
