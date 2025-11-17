package functions.basic;

// Класс Exp реализует интерфейс Function
import functions.Function;
import java.io.Serializable;

// Класс Exp представляет функцию e^x
public class Exp implements Function, Serializable {
    private static final long serialVersionUID = 1L;
    
    @Override
    // Вычисляем значение экспоненты e^x в заданной точке
    public double getFunctionValue(double x) {
        // Math.exp(x) - встроенная Java функция для вычисления e^x
        return Math.exp(x);
    }
    
    @Override
    // Возвращает левую границу области определения функции
    public double getLeftDomainBorder() {
        // Double.NEGATIVE_INFINITY - специальная константа для -∞
        return Double.NEGATIVE_INFINITY;
    }
    
    @Override
    // Возвращает правую границу области определения функции
    public double getRightDomainBorder() {
        // Double.POSITIVE_INFINITY - специальная константа для +∞
        return Double.POSITIVE_INFINITY;
    }
}