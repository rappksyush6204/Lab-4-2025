package functions;

public class FunctionPointIndexOutOfBoundsException extends IndexOutOfBoundsException {
    public FunctionPointIndexOutOfBoundsException() {
        super("Выход за границы набора точек");
    }

    public FunctionPointIndexOutOfBoundsException(String message) {
        super(message);
    }

}

//исключение выхода за границы набора точек при обращении к ним по номеру,
// наследует от класса IndexOutOfBoundsException;
