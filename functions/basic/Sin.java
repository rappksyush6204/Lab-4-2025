package functions.basic;

public class Sin extends TrigonometricFunction {
    
    @Override
    // x - угол в РАДИАНАХ (не в градусах!)
    // значение sin(x) от -1 до 1
    public double getFunctionValue(double x) {
        return Math.sin(x);
    }
}