package functions.basic;

public class Cos extends TrigonometricFunction {
    @Override
    public double getFunctionValue(double x) {
        // Math.cos(x) - встроенная Java функция для вычисления косинуса
        return Math.cos(x);
    }
}