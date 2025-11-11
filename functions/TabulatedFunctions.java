package functions;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.Writer;


// Вспомогательный класс со статическими методами для работы с табулированными функциями
// Содержит методы для ввода/вывода, табулирования и сериализации функций
public class TabulatedFunctions {
    
    // Флаг для выбора типа табулированной функции по умолчанию
    private static boolean defaultUseLinkedList = false;
    
    // Приватный конструктор - запрещаем создание объектов
    private TabulatedFunctions() {
        throw new AssertionError("Нельзя создавать объекты класса TabulatedFunctions");
    }
    
    public static void setDefaultTabulatedFunctionClass(boolean useLinkedList) {
        defaultUseLinkedList = useLinkedList;
    }
    
    // Выводит табулированную функцию в байтовый поток
    public static void outputTabulatedFunction(TabulatedFunction function, OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        
        // Записываем количество точек
        dos.writeInt(function.getPointsCount());
        
        // Записываем все точки (x, y)
        for (int i = 0; i < function.getPointsCount(); i++) {
            dos.writeDouble(function.getPointX(i));
            dos.writeDouble(function.getPointY(i));
        }
        
        // Принудительно сбрасываем буфер
        dos.flush();
    }

    public static TabulatedFunction inputTabulatedFunction(InputStream in) {
        return inputTabulatedFunction(in, defaultUseLinkedList);
    }
    
    // Вводит табулированную функцию из байтового потока с выбором типа
    public static TabulatedFunction inputTabulatedFunction(InputStream in, boolean useLinkedList) {
        // Используем DataInputStream для удобного чтения примитивных типов
        try (DataInputStream dis = new DataInputStream(in)) {
            // Читаем количество точек
            int pointsCount = dis.readInt();
            
            // Проверяем корректность количества точек
            if (pointsCount < 2) {
                throw new IllegalArgumentException("Количество точек не может быть меньше двух");
            }
            
            // Создаем массив точек
            FunctionPoint[] points = new FunctionPoint[pointsCount];
            for (int i = 0; i < pointsCount; i++) {
                double x = dis.readDouble();
                double y = dis.readDouble();
                points[i] = new FunctionPoint(x, y);
            }
            
            // Проверяем упорядоченность точек по X с учетом машинного эпсилона
            for (int i = 0; i < pointsCount - 1; i++) {
                if (points[i].getX() >= points[i + 1].getX() - 1e-10) {
                    throw new IllegalArgumentException("Точки должны быть строго упорядочены по возрастанию X");
                }
            }
            
            // Создаем и возвращаем табулированную функцию выбранного типа
            if (useLinkedList) {
                return new LinkedListTabulatedFunction(points);
            } else {
                return new ArrayTabulatedFunction(points);
            }
            
        } catch (IOException e) {
            // Преобразуем checked исключение в unchecked
            throw new RuntimeException("Ошибка ввода функции из потока", e);
        }
    }

    // Записывает табулированную функцию в символьный поток
    public static void writeTabulatedFunction(TabulatedFunction function, Writer out) {
        // Используем BufferedWriter для эффективной записи
        try (BufferedWriter bw = new BufferedWriter(out)) {
            // Записываем количество точек
            bw.write(String.valueOf(function.getPointsCount()));
            bw.write(" ");
            
            // Записываем все точки (x, y) через пробел
            for (int i = 0; i < function.getPointsCount(); i++) {
                bw.write(String.valueOf(function.getPointX(i)));
                bw.write(" ");
                bw.write(String.valueOf(function.getPointY(i)));
                if (i < function.getPointsCount() - 1) {
                    bw.write(" ");
                }
            }
            
            // Принудительно сбрасываем буфер
            bw.flush();          
        } catch (IOException e) {
            throw new RuntimeException("Ошибка записи функции в поток", e);
        }
    }

    // Читает табулированную функцию из символьного потока (использует тип по умолчанию)
    public static TabulatedFunction readTabulatedFunction(Reader in) {
        return readTabulatedFunction(in, defaultUseLinkedList);
    }
    
    // Читает табулированную функцию из символьного потока с выбором типа
    public static TabulatedFunction readTabulatedFunction(Reader in, boolean useLinkedList) {
        // Используем StreamTokenizer для разбора чисел из текста
        try {
            StreamTokenizer tokenizer = new StreamTokenizer(in);
            
            // Настраиваем tokenizer для разбора чисел
            tokenizer.resetSyntax();
            tokenizer.wordChars('0', '9');
            tokenizer.wordChars('.', '.');
            tokenizer.wordChars('-', '-');
            tokenizer.wordChars('e', 'e');
            tokenizer.wordChars('E', 'E');
            tokenizer.whitespaceChars(' ', ' ');
            tokenizer.whitespaceChars('\t', '\t');
            tokenizer.whitespaceChars('\n', '\n');
            tokenizer.whitespaceChars('\r', '\r');
            
            // Читаем количество точек
            if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
                throw new RuntimeException("Ожидалось количество точек");
            }
            int pointsCount = Integer.parseInt(tokenizer.sval);
            
            // Проверяем корректность количества точек
            if (pointsCount < 2) {
                throw new IllegalArgumentException("Количество точек не может быть меньше двух");
            }
            
            // Читаем все точки
            FunctionPoint[] points = new FunctionPoint[pointsCount];
            
            for (int i = 0; i < pointsCount; i++) {
                // Читаем x
                if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
                    throw new RuntimeException("Ожидалось значение x для точки " + i);
                }
                double x = Double.parseDouble(tokenizer.sval);
                
                // Читаем y
                if (tokenizer.nextToken() != StreamTokenizer.TT_WORD) {
                    throw new RuntimeException("Ожидалось значение y для точки " + i);
                }
                double y = Double.parseDouble(tokenizer.sval);
                
                points[i] = new FunctionPoint(x, y);
            }
            
            // Проверяем упорядоченность точек по X с учетом машинного эпсилона
            for (int i = 0; i < pointsCount - 1; i++) {
                if (points[i].getX() >= points[i + 1].getX() - 1e-10) {
                    throw new IllegalArgumentException("Точки должны быть строго упорядочены по возрастанию X");
                }
            }
            
            // Создаем и возвращаем табулированную функцию выбранного типа
            if (useLinkedList) {
                return new LinkedListTabulatedFunction(points);
            } else {
                return new ArrayTabulatedFunction(points);
            }
            
        } catch (IOException e) {
            // Преобразуем checked исключение в unchecked
            throw new RuntimeException("Ошибка чтения функции из потока", e);
        }
    }
    
    // Табулирует функцию на заданном отрезке с заданным количеством точек (использует тип по умолчанию)
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount) {
        return tabulate(function, leftX, rightX, pointsCount, defaultUseLinkedList);
    }
    
    // Табулирует функцию на заданном отрезке с заданным количеством точек с выбором типа
    public static TabulatedFunction tabulate(Function function, double leftX, double rightX, int pointsCount, boolean useLinkedList) {
        // Проверяем что границы табулирования входят в область определения функции
        if (leftX < function.getLeftDomainBorder() - 1e-10 || rightX > function.getRightDomainBorder() + 1e-10) {
            throw new IllegalArgumentException(
                "Границы табулирования [" + leftX + ", " + rightX + "] " +
                "выходят за область определения функции [" + 
                function.getLeftDomainBorder() + ", " + function.getRightDomainBorder() + "]"
            );
        }
        
        // Проверяем корректность количества точек
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }
        
        // Проверяем что левая граница меньше правой
        if (leftX >= rightX - 1e-10) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        
        // Создаем массив для значений Y
        double[] values = new double[pointsCount];
        
        // Вычисляем шаг между точками
        double step = (rightX - leftX) / (pointsCount - 1);
        
        // Заполняем массив значений Y, вычисляя функцию в каждой точке
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            values[i] = function.getFunctionValue(x);
        }
        
        // Возвращаем табулированную функцию выбранного типа
        if (useLinkedList) {
            return new LinkedListTabulatedFunction(leftX, rightX, values);
        } else {
            return new ArrayTabulatedFunction(leftX, rightX, values);
        }
    }
}