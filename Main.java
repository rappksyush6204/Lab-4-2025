import functions.*;
import functions.basic.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Тестирование задания 3: Базовые функции
            testBasicFunctions();
            
            // Тестирование задания 4-5: Мета-функции
            testMetaFunctions();
            
            // Тестирование задания 6: Табулирование
            testTabulation();
            
            // Тестирование задания 7: Ввод-вывод
            testInputOutput();
            
            // Тестирование задания 9: Сериализация
            testSerialization(); // тестируем Serializable (ArrayTabulatedFunction, Sum, Mult)
            testExternalizableSerialization(); // тестируем Externalizable (LinkedListTabulatedFunction, Power, Composition)

            
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testBasicFunctions() {
        System.out.println("1. Тестирование базовых функций:");
        
        // Экспонента
        Exp exp = new Exp();
        System.out.printf("Exp(1) = %.4f%n", exp.getFunctionValue(1));
        System.out.printf("Область определения Exp: [%.1f, %.1f]%n", 
            exp.getLeftDomainBorder(), exp.getRightDomainBorder());
        
        // Логарифм
        Log log = new Log(Math.E); // натуральный логарифм
        System.out.printf("Ln(2) = %.4f%n", log.getFunctionValue(2));
        
        // Тригонометрические функции
        Sin sin = new Sin();
        Cos cos = new Cos();
        System.out.printf("Sin(π/2) = %.4f%n", sin.getFunctionValue(Math.PI/2));
        System.out.printf("Cos(π) = %.4f%n", cos.getFunctionValue(Math.PI));
        
        System.out.println();
    }
    
    private static void testMetaFunctions() {
        System.out.println("2. Тестирование meta функций:");
        
        Sin sin = new Sin();
        Cos cos = new Cos();
        
        // Сумма sin² + cos² (должна быть ≈ 1)
        Function sin2 = Functions.power(sin, 2);
        Function cos2 = Functions.power(cos, 2);
        Function sum = Functions.sum(sin2, cos2);
        
        System.out.println("sin²(x) + cos²(x) в точках:");
        for (double x = 0; x <= Math.PI; x += 0.5) {
            System.out.printf("x=%.1f: %.6f%n", x, sum.getFunctionValue(x));
        }
        
        // Композиция
        Log log = new Log(Math.E);
        Exp exp = new Exp();
        Function composition = Functions.composition(log, exp);
        System.out.printf("ln(e^2) = %.4f%n", composition.getFunctionValue(2));
        
        System.out.println();
    }
    
    private static void testTabulation() {
        System.out.println("3. Тестирование табулирования:");
        
        Sin sin = new Sin();
        Cos cos = new Cos();
        
        // Табулирование синуса и косинуса
        TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
        TabulatedFunction tabulatedCos = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);
        
        System.out.println("Табулированный синус (первые 5 точек):");
        for (int i = 0; i < Math.min(5, tabulatedSin.getPointsCount()); i++) {
            System.out.printf("  %s%n", tabulatedSin.getPoint(i));
        }
        
        System.out.println("Сравнение точного и табулированного синуса:");
        for (double x = 0; x <= Math.PI; x += 0.1) {
            double exact = sin.getFunctionValue(x);
            double tabulated = tabulatedSin.getFunctionValue(x);
            System.out.printf("x=%.1f: точный=%.6f, табулир=%.6f, разница=%.6f%n", 
                x, exact, tabulated, Math.abs(exact - tabulated));
        }
        
        System.out.println();
    }
    
    private static void testInputOutput() throws IOException {
        System.out.println("4. Тестирование ввода-вывода:");
        
        // Тестирование байтовых потоков
        testByteStreams();
        
        // Тестирование символьных потоков
        testCharStreams();
        
        System.out.println();
    }
    
    private static void testByteStreams() throws IOException {
        System.out.println(" Байтовые потоки:");
        
        // Создаем табулированную экспоненту
        Exp exp = new Exp();
        TabulatedFunction tabulatedExp = TabulatedFunctions.tabulate(exp, 0, 10, 11);
        
        // Записываем в байтовый поток
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        TabulatedFunctions.outputTabulatedFunction(tabulatedExp, byteOut);
        
        // Читаем из байтового потока
        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        TabulatedFunction readExp = TabulatedFunctions.inputTabulatedFunction(byteIn);
        
        System.out.println(" Сравнение исходной и прочитанной экспоненты:");
        for (int i = 0; i < readExp.getPointsCount(); i++) {
            double original = tabulatedExp.getPointY(i);
            double read = readExp.getPointY(i);
            System.out.printf(" Точка %d: исходная=%.6f, прочитанная=%.6f%n", 
                i, original, read);
        }
    }
    
    private static void testCharStreams() throws IOException {
        System.out.println(" Символьные потоки:");
        
        // Создаем табулированный логарифм
        Log log = new Log(Math.E);
        TabulatedFunction tabulatedLog = TabulatedFunctions.tabulate(log, 1, 10, 10);
        
        // Записываем в символьный поток
        StringWriter writer = new StringWriter();
        TabulatedFunctions.writeTabulatedFunction(tabulatedLog, writer);
        String serialized = writer.toString();
        
        System.out.println(" Сериализованные данные: " + serialized);
        
        // Читаем из символьного потока
        StringReader reader = new StringReader(serialized);
        TabulatedFunction readLog = TabulatedFunctions.readTabulatedFunction(reader);
        
        System.out.println(" Сравнение логарифмов:");
        for (int i = 0; i < readLog.getPointsCount(); i++) {
            System.out.printf("  %s -> %s%n", tabulatedLog.getPoint(i), readLog.getPoint(i));
        }
    }
    
    private static void testSerialization() throws IOException, ClassNotFoundException {
        System.out.println("5. Тестирование Serializable:");
    
        // 1. Тестируем ArrayTabulatedFunction (Serializable)
        System.out.println("  ArrayTabulatedFunction (Serializable):");
        Exp exp = new Exp();
        Log log = new Log(Math.E);
        Function composition = Functions.composition(log, exp);
        TabulatedFunction arrayFunc = TabulatedFunctions.tabulate(composition, 0, 10, 11);

        String arrayFile = "array_serializable.ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arrayFile))) {
            oos.writeObject(arrayFunc);
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arrayFile))) {
            TabulatedFunction deserializedArray = (TabulatedFunction) ois.readObject();
            System.out.println("  Восстановлено точек: " + deserializedArray.getPointsCount());
        }
        
        System.out.println("  ArrayTabulatedFunction успешно сериализована через Serializable");
        
        // 2. Тестируем мета-функции с Serializable (Sum, Mult)
        System.out.println("  Мета-функции Sum и Mult (Serializable):");
        Sin sin = new Sin();
        Cos cos = new Cos();
        
        // Sum с Serializable
        Function sumFunc = Functions.sum(sin, cos);
        String sumFile = "sum_serializable.ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(sumFile))) {
            oos.writeObject(sumFunc);
        }
        
        Function deserializedSum;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(sumFile))) {
            deserializedSum = (Function) ois.readObject();
        }
        System.out.println("  Sum успешно сериализован через Serializable");
        
        // Mult с Serializable  
        Function multFunc = Functions.mult(sin, cos);
        String multFile = "mult_serializable.ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(multFile))) {
            oos.writeObject(multFunc);
        }
        
        Function deserializedMult;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(multFile))) {
            deserializedMult = (Function) ois.readObject();
        }
        System.out.println("  Mult успешно сериализован через Serializable");
        
        // Удаляем временные файлы
        new File(arrayFile).delete();
        new File(sumFile).delete();
        new File(multFile).delete();
        
        System.out.println();
    }
    
    private static void testExternalizableSerialization() throws IOException, ClassNotFoundException {
        System.out.println("6. Тестирование Externalizable:");
        
        // 1. Тестируем LinkedListTabulatedFunction (Externalizable)
        System.out.println("  LinkedListTabulatedFunction (Externalizable):");
        FunctionPoint[] points = {
            new FunctionPoint(1, 0),
            new FunctionPoint(2, 0.693147),
            new FunctionPoint(3, 1.098612),
            new FunctionPoint(4, 1.386294),
            new FunctionPoint(5, 1.609438)
        };
        LinkedListTabulatedFunction linkedListFunc = new LinkedListTabulatedFunction(points);

        String linkedListFile = "linkedlist_externalizable.ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(linkedListFile))) {
            oos.writeObject(linkedListFunc);
        }
        
        LinkedListTabulatedFunction deserializedLinkedList;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(linkedListFile))) {
            deserializedLinkedList = (LinkedListTabulatedFunction) ois.readObject();
        }
        System.out.println("  LinkedListTabulatedFunction успешно сериализована через Externalizable");
        
        // 2. Тестируем мета-функции с Externalizable
        System.out.println("  Мета-функции с Externalizable:");
        Sin sin = new Sin();
        Cos cos = new Cos();
        
        // Power с Externalizable
        Function powerFunc = Functions.power(sin, 2);
        String powerFile = "power_externalizable.ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(powerFile))) {
            oos.writeObject(powerFunc);
        }
        
        Function deserializedPower;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(powerFile))) {
            deserializedPower = (Function) ois.readObject();
        }
        System.out.println("  Power успешно сериализован через Externalizable");
        
        // Composition с Externalizable
        Function compositionFunc = Functions.composition(sin, cos);
        String compositionFile = "composition_externalizable.ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(compositionFile))) {
            oos.writeObject(compositionFunc);
        }
        
        Function deserializedComposition;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(compositionFile))) {
            deserializedComposition = (Function) ois.readObject();
        }
        System.out.println("  Composition успешно сериализован через Externalizable");
        
        // Удаляем временные файлы
        new File(linkedListFile).delete();
        new File(powerFile).delete();
        new File(compositionFile).delete();
        
        System.out.println();
    }
}