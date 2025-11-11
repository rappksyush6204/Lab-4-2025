package functions.basic;

// Тангенс = sin(x) / cos(x)
public class Tan extends TrigonometricFunction {
    @Override
    public double getFunctionValue(double x) {
        // Проверяем точки разрыва: cos(x) = 0
        Cos cos = new Cos();
        if (Math.abs(cos.getFunctionValue(x)) < 1e-10) {
            return Double.NaN;
        }
        return Math.tan(x);
    }
}