import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public class CalculatorController {
    private final static int HISTORY_CAPACITY = 20;
    private static final Map<String, Operation> OPERATIONS = Map.of(
            "+", Operation.ADD,
            "-", Operation.SUBTRACT,
            "*", Operation.MULTIPLY,
            "/", Operation.DIVIDE
    );

    private final DecimalFormat df = new DecimalFormat("#.######");
    private final Calculator calculator = new Calculator();
    private final StringBuilder userInput = new StringBuilder();
    private final Deque<String> executionHistory = new ArrayDeque<>();
    private Double totalValue = null;
    private Operation currentOperation = Operation.ADD;
    private boolean isShowingResult = false;

    private boolean checkSign(String inputSign) {
        return switch(inputSign) {
            case "+", "-", "/", "*" -> true;
            default -> false;
        };
    }

    private double parseInput() {
        if (userInput.isEmpty() || userInput.toString().equals(".")) {
            return 0.0;
        }

        return Double.parseDouble(userInput.toString());
    }

    public void onOperation(Operation operation) {
        if (!userInput.isEmpty()) {
            onEquals();
        }

        currentOperation = operation;

        var last = executionHistory.peekLast();
        if (last != null && checkSign(last)) {
            executionHistory.removeLast();
        }
        executionHistory.addLast(operation.getSign());
    }

    public void onInput(String num) {
        isShowingResult = false;

        if (num.equals(".") && userInput.toString().contains(num)) {
            return;
        }

        executionHistory.add(num);
        userInput.append(num);
    }

    public void onEquals() {
        if (totalValue == null) {
            totalValue = parseInput();
        } else {
            try {
                totalValue = calculator.evaluate(currentOperation, totalValue, parseInput());
            } catch (ArithmeticException e) {
                onAllClear();
                userInput.append("Error. Can't divide by 0.");
                return;
            }
        }

        userInput.setLength(0);
        isShowingResult = true;
    }

    public void onClear() {
        int times = userInput.length();
        userInput.setLength(0);

        for (int i = 0; i < times; i++) {
            executionHistory.removeLast();
        }
    }

    public void onAllClear() {
        userInput.setLength(0);
        totalValue = null;
        executionHistory.clear();
        isShowingResult = false;
    }

    public void onBackward() {
        if (!userInput.isEmpty()) {
            if (!executionHistory.isEmpty()) executionHistory.removeLast();
            userInput.deleteCharAt(userInput.length() - 1);
        }
    }

    public void onKeyboard(String text) {
        System.out.println(text);
        if (text.length() == 1 && Character.isDigit(text.charAt(0))) {
            onInput(text);
            return;
        }

        if (OPERATIONS.containsKey(text)) {
            onOperation(OPERATIONS.get(text));
            return;
        }

        if (text.equals("=")) {
            onEquals();
            return;
        }
    }

    public String getResult() {

        if (isShowingResult && totalValue != null) {
            return df.format(totalValue);
        }

        return userInput.toString();
    }

    public String getHistory() {
        if (executionHistory.size() > HISTORY_CAPACITY) {
            while (executionHistory.size() > HISTORY_CAPACITY) {
                executionHistory.removeFirst();
            }
            executionHistory.addFirst("...");
        }

        return String.join("", executionHistory);
    }
}
