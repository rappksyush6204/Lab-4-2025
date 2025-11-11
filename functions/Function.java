package functions;

public interface Function {
    // Возвращаем значение левой границы области определения функции
    public double getLeftDomainBorder();
    
    // Возвращает значение правой границы области определения функции
    public double getRightDomainBorder();
    
    // Вычисляем значение функции в заданной точке X
    public double getFunctionValue(double x);

}
