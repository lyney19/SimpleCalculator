import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    ///
    /// Constants
    ///

    private final static int STD_BUTTON_WIDTH = 85;
    private final static int STD_BUTTON_HEIGHT = 75;
    private final static int STD_BUTTON_FONT_SIZE = 22;
    private final static int STD_LABEL_FONT_SIZE = 30;

    private final CalculatorController controller = new CalculatorController();

    ///
    /// Layouts
    ///

    private final BorderPane mainPane = new BorderPane();
    private final VBox labelsPane = new VBox();
    private final GridPane buttonsPane = new GridPane();

    ///
    /// Labels
    ///

    private final Label mainDisplay = new Label("Ожидание выражения...");
    private final Label historyDisplay = new Label();

    ///
    /// Action buttons
    ///

    private final Button buttonClear = createActionButton("C", () -> {
        controller.onClear();
        updateUI();
    });

    private final Button buttonAllClear = createActionButton("AC", () -> {
        controller.onAllClear();
        updateUI();
    });

    private final Button buttonBackward = createActionButton("<x", () -> {
        controller.onBackward();
        updateUI();
    });

    private final Button buttonPlus = createActionButton("+", () -> {
        controller.onOperation(Operation.ADD);
        updateUI();
    });

    private final Button buttonMinus = createActionButton("-", () -> {
        controller.onOperation(Operation.SUBTRACT);
        updateUI();
    });

    private final Button buttonMultiply = createActionButton("*", () -> {
        controller.onOperation(Operation.MULTIPLY);
        updateUI();
    });

    private final Button buttonDivide = createActionButton("/", () -> {
        controller.onOperation(Operation.DIVIDE);
        updateUI();
    });

    private final Button buttonExecute = createExecutionButton();

    //
    // Input buttons
    //

    private final Button buttonNum0 = createInputButton("0");
    private final Button buttonNum1 = createInputButton("1");
    private final Button buttonNum2 = createInputButton("2");
    private final Button buttonNum3 = createInputButton("3");
    private final Button buttonNum4 = createInputButton("4");
    private final Button buttonNum5 = createInputButton("5");
    private final Button buttonNum6 = createInputButton("6");
    private final Button buttonNum7 = createInputButton("7");
    private final Button buttonNum8 = createInputButton("8");
    private final Button buttonNum9 = createInputButton("9");
    private final Button buttonDot = createInputButton(".");

    private void updateUI() {
        mainDisplay.setText(controller.getResult());
        historyDisplay.setText(controller.getHistory());
    }

    private Button createInputButton(String text) {
        final var btn = new Button(text);

        btn.setOnAction(e -> {
            controller.onInput(btn.getText());
            updateUI();
        });

        btn.setPrefSize(STD_BUTTON_WIDTH, STD_BUTTON_HEIGHT);
        btn.setStyle("-fx-font-size:  " + STD_BUTTON_FONT_SIZE + "px;");

        return btn;
    }

    private Button createActionButton(String text, Runnable action) {
        final var btn = new Button(text);
        btn.setOnAction(e -> action.run());
        btn.setPrefSize(STD_BUTTON_WIDTH, STD_BUTTON_HEIGHT);
        btn.setStyle("-fx-font-size:  " + STD_BUTTON_FONT_SIZE + "px;");

        return btn;
    }

    private Button createExecutionButton() {
        final var btn = new Button("=");

        btn.setOnAction(e -> {
            controller.onEquals();
            updateUI();
        });

        btn.setPrefSize(STD_BUTTON_WIDTH, STD_BUTTON_HEIGHT * 2);
        btn.setStyle("-fx-font-size:  " + STD_BUTTON_FONT_SIZE + "px;");

        return btn;
    }

    @Override
    public void start(Stage stage) {

        //
        // Main Stage
        //

        mainDisplay.setStyle("-fx-font-size: " + STD_LABEL_FONT_SIZE + "px;");
        historyDisplay.setStyle("-fx-text-fill: gray; -fx-font-size: " + (STD_LABEL_FONT_SIZE - 8) +"px;");

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

        scene.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER) {
                controller.onEquals();
                e.consume();
                updateUI();
            }
        });

        scene.setOnKeyTyped(e -> {
            controller.onKeyboard(e.getCharacter());
            updateUI();
        });

        stage.setScene(scene);
        stage.setTitle("Calculator");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}