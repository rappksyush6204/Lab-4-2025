package functions;

import functions.meta.*;

// Вспомогательный класс со статическими методами для работы с функциями
public class Functions {
    
    // Приватный конструктор - запрещаем создание объектов
    private Functions() {
        throw new AssertionError("Нельзя создавать объекты класса Functions");
    }
    
    //Сдвиг функции вдоль осей
    public static Function shift(Function f, double shiftX, double shiftY) {
        return new Shift(f, shiftX, shiftY);
    }
    
    //Масштабирование функции вдоль осей
    public static Function scale(Function f, double scaleX, double scaleY) {
        return new Scale(f, scaleX, scaleY);
    }
    
    // Возведение функции в степень
    public static Function power(Function f, double power) {
        return new Power(f, power);
    }
    
    // Сумма двух функций
    public static Function sum(Function f1, Function f2) {
        return new Sum(f1, f2);
    }
    
    //Произведение двух функций
    public static Function mult(Function f1, Function f2) {
        return new Mult(f1, f2);
    }
    
    // Композиция двух функций
    public static Function composition(Function f1, Function f2) {
        return new Composition(f1, f2);
    }
}