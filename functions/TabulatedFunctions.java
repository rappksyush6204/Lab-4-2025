package functions;

import java.io.*;

// содержит методы для ввода/вывода
public class TabulatedFunctions {
    
    // приватный конструктор
    private TabulatedFunctions() {
        throw new AssertionError("нельзя создавать объекты класса TabulatedFunctions");
    }
    
    // метод для вывода табулированной функции в байтовый поток
    // записывает количество точек и координаты всех точек
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        
        // записываем количество точек в функции
        dos.writeInt(function.getPointsCount());
        
        // записываем все точки (x, y) по порядку
        for (int i = 0; i < function.getPointsCount(); i++) {
            dos.writeDouble(function.getPointX(i)); // записываем координату x
            dos.writeDouble(function.getPointY(i)); // записываем координату y
        }
        
        // принудительно сбрасываем буфер, чтобы данные точно записались
        dos.flush();
    }

    // метод для ввода
    // читает данные и создает объект табулированной функции
    public static TabulatedFunction inputTabulatedFunction(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);
        
        //читаем количество точек
        int pointsCount = dis.readInt();
        
        // проверяем, что точек достаточно для создания функции
        if (pointsCount < 2) {
            throw new IllegalArgumentException("количество точек не может быть меньше двух");
        }
        
        //массив для хранения точек
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        
        // читаем координаты всех точек из потока
        for (int i = 0; i < pointsCount; i++) {
            double x = dis.readDouble(); // читаем x
            double y = dis.readDouble(); // читаем y
            points[i] = new FunctionPoint(x, y); // создаем новую точку
        }
        
        // проверяем, что точки упорядочены по x (возрастание)
        for (int i = 0; i < pointsCount - 1; i++) {
            if (points[i + 1].getX() - points[i].getX() < 1e-10) {
                throw new IllegalArgumentException("точки должны быть строго упорядочены по возрастанию x");
            }
        }
        return new ArrayTabulatedFunction(points);
    }

    // метод для записи в символьный поток
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) throws IOException {
        BufferedWriter bw = new BufferedWriter(out);
        
        // записываем количество точек
        bw.write(String.valueOf(function.getPointsCount()));
        bw.write(" ");
        
        // записываем все точки (x1 y1 x2 y2...)
        for (int i = 0; i < function.getPointsCount(); i++) {
            bw.write(String.valueOf(function.getPointX(i))); // записываем x
            bw.write(" "); 
            bw.write(String.valueOf(function.getPointY(i))); // записываем y
            if (i < function.getPointsCount() - 1) {
                bw.write(" ");
            }
        }
        
        bw.flush();
    }

    // метод для чтения из символьного потока
    public static TabulatedFunction readTabulatedFunction(Reader in) throws IOException {
        StreamTokenizer tokenizer = new StreamTokenizer(in);
        
        // для правильного разбора чисел
        tokenizer.resetSyntax();
        tokenizer.wordChars('0', '9');    // цифры
        tokenizer.wordChars('.', '.');   
        tokenizer.wordChars('-', '-');    
        tokenizer.wordChars('e', 'e');    // экспоненциальная запись
        tokenizer.wordChars('E', 'E');    // экспоненциальная запись
        tokenizer.whitespaceChars(' ', ' ');  
        tokenizer.whitespaceChars('\t', '\t'); 
        tokenizer.whitespaceChars('\n', '\n'); 
        tokenizer.whitespaceChars('\r', '\r'); 
        
        // читаем количество точек (первое число в потоке)
        if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
            throw new IOException("ожидалось количество точек");
        }
        int pointsCount = Integer.parseInt(tokenizer.sval);
        
        // проверяем корректность количества точек
        if (pointsCount < 2) {
            throw new IllegalArgumentException("количество точек не может быть меньше двух");
        }
        
        // массив для точек
        FunctionPoint[] points = new FunctionPoint[pointsCount];
        
        // читаем координаты всех точек
        for (int i = 0; i < pointsCount; i++) {
            // читаем координату x
            if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
                throw new IOException("ожидалось значение x для точки " + i);
            }
            double x = Double.parseDouble(tokenizer.sval);
            
            // читаем координату y
            if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
                throw new IOException("ожидалось значение y для точки " + i);
            }
            double y = Double.parseDouble(tokenizer.sval);
            
            // создаем новую точку
            points[i] = new FunctionPoint(x, y);
        }
        
        // проверяем упорядоченность точек по x
        for (int i = 0; i < pointsCount - 1; i++) {
            if (points[i + 1].getX() - points[i].getX() < 1e-10) {
                throw new IllegalArgumentException("точки должны быть строго упорядочены по возрастанию x");
            }
        }
        
        return new ArrayTabulatedFunction(points);
    }
    
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        // проверяем, что границы табулирования входят в область определения функции
        if (leftX < function.getLeftDomainBorder() - 1e-10 || rightX > function.getRightDomainBorder() + 1e-10) {
            throw new IllegalArgumentException("границы табулирования выходят за область определения функции");
        }
        
        // проверяем, что точек достаточно
        if (pointsCount < 2) {
            throw new IllegalArgumentException("количество точек не может быть меньше двух");
        }
        
        // проверяем, что левая граница действительно меньше правой
        if (leftX >= rightX - 1e-10) {
            throw new IllegalArgumentException("левая граница должна быть меньше правой");
        }
        
        // массив для значений функции в точках
        double[] values = new double[pointsCount];
        
        //шаг между точками
        double step = (rightX - leftX) / (pointsCount - 1);
        
        //значения функции во всех точках
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step; // x текущей точки
            values[i] = function.getFunctionValue(x); // значение функции
        }
        
        return new ArrayTabulatedFunction(leftX, rightX, values);
    }
}