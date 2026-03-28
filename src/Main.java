import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.DecimalFormat;
import java.util.ArrayDeque;
import java.util.Deque;

public class Main extends Application {
    private final static int STD_BUTTON_WIDTH = 85;
    private final static int STD_BUTTON_HEIGHT = 75;
    private final static int STD_BUTTON_FONT_SIZE = 22;
    private final static int STD_LABEL_FONT_SIZE = 30;
    private final static int HISTORY_CAPACITY = 20;
    private final static DecimalFormat DF = new DecimalFormat("#.######");

    private final StringBuilder userInput = new StringBuilder();
    private final Deque<String> executionHistory = new ArrayDeque<>();
    private Double totalValue = null;
    private Operation currentOperation = Operation.ADD;
    private boolean isShowingResult = false;

    private void evaluate() {
        if (userInput.isEmpty()) return;

        if (totalValue == null) {
            totalValue = Double.parseDouble(userInput.toString());
            userInput.setLength(0);
            return;
        }

        totalValue = currentOperation.operate(totalValue, Double.parseDouble(userInput.toString()));
        userInput.setLength(0);

        isShowingResult = true;
    }

    private void setCurrentOperation(Operation operation) {
        if (checkSign(executionHistory.peekLast())) {
            executionHistory.removeLast();
        }

        executionHistory.add(operation.getSign());
        currentOperation = operation;
    }

    private void inputNum(String num) {
        isShowingResult = false;

        if (num.equals(".") && userInput.toString().contains(num)) {
            return;
        }

        executionHistory.add(num);
        userInput.append(num);
    }

    private void clearInput() {
        int times = userInput.length();
        userInput.setLength(0);

        for (int i = 0; i < times; i++) {
            executionHistory.removeLast();
        }
    }

    private void clearAll() {
        clearInput();
        totalValue = null;
        executionHistory.clear();
        isShowingResult = false;
    }

    private void backward() {
        if (!userInput.isEmpty()) {
            if (!executionHistory.isEmpty()) executionHistory.removeLast();
            userInput.deleteCharAt(userInput.length() - 1);
        }
    }

    private void updateLabels(Label mainLabel, Label historyLabel) {
        mainLabel.setText(isShowingResult ? DF.format(totalValue) : userInput.toString());

        if (executionHistory.size() > HISTORY_CAPACITY + 3) {
            while (executionHistory.size() > HISTORY_CAPACITY + 3) {
                executionHistory.removeFirst();
            }
            executionHistory.addFirst("...");
        }

        historyLabel.setText(String.join("", executionHistory));
    }

    private boolean checkSign(String inputSign) {
        if (inputSign == null) return false;

        for (var operation : Operation.values()) {
            if (operation.getSign().equals(inputSign)) {
                return true;
            }
        }

        return false;
    }

    private Button createInputButton(String text) {
        final var btn = new Button(text);
        btn.setOnAction(e -> inputNum(btn.getText()));
        btn.setPrefSize(STD_BUTTON_WIDTH, STD_BUTTON_HEIGHT);
        btn.setStyle("-fx-font-size:  " + STD_BUTTON_FONT_SIZE + " px;");

        return btn;
    }

    private Button createActionButton(String text, EventHandler<ActionEvent> e) {
        final var btn = new Button(text);
        btn.setOnAction(e);
        btn.setPrefSize(STD_BUTTON_WIDTH, STD_BUTTON_HEIGHT);
        btn.setStyle("-fx-font-size:  " + STD_BUTTON_FONT_SIZE + " px;");

        return btn;
    }

    @Override
    public void start(Stage stage) {

        //
        // Layouts
        //

        final var mainPane = new BorderPane();
        final var labelsPane = new VBox();
        final var buttonsPane = new GridPane();

        //
        // Labels
        //

        final var mainDisplay = new Label("Ожидание выражения...");
        mainDisplay.setStyle("-fx-font-size: " + STD_LABEL_FONT_SIZE + " px;");
        final var historyDisplay = new Label();
        historyDisplay.setStyle("-fx-text-fill: gray; -fx-font-size: " + (STD_LABEL_FONT_SIZE - 8) + " px;");

        //
        // Action buttons
        //

        final var buttonClear = createActionButton("C", e -> clearInput());

        final var buttonAllClear = createActionButton("AC", e -> clearAll());

        final var buttonBackward = createActionButton("<x", e -> backward());

        final var buttonPlus = createActionButton("+", e -> {
            setCurrentOperation(Operation.ADD);
            evaluate();
        });

        final var buttonMinus = createActionButton("-", e -> {
            setCurrentOperation(Operation.SUBTRACT);
            evaluate();
        });

        final var buttonMultiply = createActionButton("*", e -> {
            setCurrentOperation(Operation.MULTIPLY);
            evaluate();
        });

        final var buttonDivide = createActionButton("/", e -> {
            setCurrentOperation(Operation.DIVIDE);
            evaluate();
        });

        final var buttonExecute = new Button("=");
        buttonExecute.setOnAction(e -> evaluate());
        buttonExecute.setPrefSize(STD_BUTTON_WIDTH, STD_BUTTON_HEIGHT * 2);
        buttonExecute.setStyle("-fx-font-size:  " + STD_BUTTON_FONT_SIZE + " px;");

        //
        // Input buttons
        //

        final var buttonNum0 = createInputButton("0");
        final var buttonNum1 = createInputButton("1");
        final var buttonNum2 = createInputButton("2");
        final var buttonNum3 = createInputButton("3");
        final var buttonNum4 = createInputButton("4");
        final var buttonNum5 = createInputButton("5");
        final var buttonNum6 = createInputButton("6");
        final var buttonNum7 = createInputButton("7");
        final var buttonNum8 = createInputButton("8");
        final var buttonNum9 = createInputButton("9");
        final var buttonDot = createInputButton(".");

        //
        // Main Stage
        //

        labelsPane.getChildren().add(mainDisplay);
        labelsPane.getChildren().add(historyDisplay);
        labelsPane.getChildren().add(new Separator());

        buttonsPane.addRow(0, buttonClear, buttonAllClear, buttonPlus, buttonMinus);
        buttonsPane.addRow(1, buttonNum7, buttonNum8, buttonNum9, buttonMultiply);
        buttonsPane.addRow(2, buttonNum4, buttonNum5, buttonNum6, buttonDivide);
        buttonsPane.addRow(3, buttonNum1, buttonNum2, buttonNum3, buttonExecute);
        buttonsPane.addRow(4, buttonNum0, buttonDot, buttonBackward);
        GridPane.setRowSpan(buttonExecute, 2);

        mainPane.setTop(labelsPane);
        mainPane.setCenter(buttonsPane);

        final var scene = new Scene(mainPane);
        scene.addEventHandler(ActionEvent.ACTION, e -> updateLabels(mainDisplay, historyDisplay));

        stage.setScene(scene);
        stage.setTitle("Calculator");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}