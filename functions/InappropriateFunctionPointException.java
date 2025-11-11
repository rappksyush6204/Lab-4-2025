package functions;

// Исключение для некорректных точек функции
public class InappropriateFunctionPointException extends Exception {

    // Конструктор по умолчанию
    public InappropriateFunctionPointException() {
        super("Некорректная точка функции");
    }

    // Конструктор с пользовательским сообщением
    public InappropriateFunctionPointException(String message) {
        super(message); // Передаем сообщение родительскому классу
    }

}