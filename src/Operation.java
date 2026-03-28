public enum Operation {
    ADD("+") {
        @Override
        public double operate(double a, double b) {
            return a + b;
        }
    },
    SUBTRACT("-") {
        @Override
        public double operate(double a, double b) {
            return a - b;
        }
    },
    DIVIDE("/") {
        @Override
        public double operate(double a, double b) {
            return a / b;
        }
    },
    MULTIPLY("*") {
        @Override
        public double operate(double a, double b) {
            return a  * b;
        }
    };

    private final String sign;
    Operation(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }

    public abstract double operate(double a, double b);
}