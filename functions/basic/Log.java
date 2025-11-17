package functions.basic;

import functions.Function;
import java.io.Serializable;

//  Класс Log представляет логарифмическую функцию logₐ(x)
public class Log implements Function, Serializable {
    private static final long serialVersionUID = 1L;
    private double base; // Основание логарифма
    
    // Создает логарифмическую функцию с заданным основанием
    public Log(double base) {
        if (base <= 0 || Math.abs(base - 1) < 1e-10) {
            throw new IllegalArgumentException("Основание логарифма должно быть > 0 и ≠ 1");
        }
        this.base = base;
    }
    
    // Вычисляет значение логарифма logₐ(x)
    @Override
    public double getFunctionValue(double x) {
        if (x <= 0) {
            return Double.NaN; // Логарифм от неположительного числа не определен
        }
        // Формула замены основания: logₐ(x) = ln(x) / ln(a)
        return Math.log(x) / Math.log(base);
    }
    
    // Возвращает левую границу области определения
    @Override
    public double getLeftDomainBorder() {
        return 0; 
    }
    
    //Возвращает правую границу области определения
    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }
    
    // Возвращает основание логарифма
    public double getBase() {
        return base;
    }
}