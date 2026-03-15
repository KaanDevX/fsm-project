import java.io.Serializable;

public class Transitions implements Serializable {
    String symbol;
    String currentState;
    String resultingState;

    public Transitions(String symbol, String currentState, String resultingState) {
        this.symbol = symbol;
        this.currentState = currentState;
        this.resultingState = resultingState;
    }

    public String getSymbol() {
        return symbol;
    }
    public String getCurrentState() {
        return currentState;
    }
    public String getResultingState() {
        return resultingState;
    }
    public String getSymbolAndCurrentState() {
        return getSymbol() + " " + getCurrentState();
    }
    public String getFullTransition() {
        return getSymbol() + " " + getCurrentState() + " " + getResultingState();
    }
}