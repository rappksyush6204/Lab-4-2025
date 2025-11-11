package functions;

import java.io.Serializable;

public class FunctionPoint implements Serializable {
    // Приватные поля для инкапсуляции данных
    private double x;  // Координата x точки
    private double y;  // Координата y точки

    // КОНСТРУКТОРЫ
    public FunctionPoint(double x, double y) { //Создаёт объект точки с заданными координатами
        this.x = x;
        this.y = y;
    }

    public FunctionPoint(FunctionPoint point) {
        this.x = point.x;  // Копируем x из переданной точки
        this.y = point.y;  // Копируем y из переданной точки
    }

    public FunctionPoint() { //Создаёт точку с координатами (0; 0)
        this(0.0, 0.0);
    }

    // ГЕТТЕРЫ (методы для чтения значений)
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // СЕТТЕРЫ (методы для изменения значений)
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
    
    // Строковое представление точки
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}