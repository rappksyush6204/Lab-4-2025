package functions.basic;

public class Cos extends TrigonometricFunction{
    private static final long serialVersionUID = 1L;
    
    @Override
    public double getFunctionValue(double x) {
        // Math.cos(x) - встроенная функция для вычисления косинуса
        return Math.cos(x);
    }
}