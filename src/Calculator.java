public class Calculator {

    public double evaluate(Operation operation, double a, double b) {
        return operation.operate(a, b);
    }

}
