import functions.*;
import functions.basic.*;
import functions.meta.*;
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
            testSerialization();
            testExternalizableSerialization(); // добавляем тестирование externalizable

            
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
        System.out.println("5. Тестирование сериализации:");
    
        // Создаем композицию: ln(e^x) = x (теоретически должна быть равна x)
        Exp exp = new Exp();
        Log log = new Log(Math.E);
        Function composition = Functions.composition(log, exp);
    
        // Табулируем композицию на отрезке [0, 10] с 11 точками
        TabulatedFunction tabulated = TabulatedFunctions.tabulate(composition, 0, 10, 11);
    
        System.out.println("  Исходная функция (ln(e^x)) - все 11 точек:");
        for (int i = 0; i < tabulated.getPointsCount(); i++) {
            double x = tabulated.getPointX(i);
            double y = tabulated.getPointY(i);
            System.out.printf("  x=%.1f: y=%.6f (ожидается: %.1f)%n", x, y, x);
        }
    
        // Сериализация в файл
        String filename = "tabulated_function.ser";
        try (ObjectOutputStream oos = new ObjectOutputStream( new FileOutputStream(filename))) {
            oos.writeObject(tabulated);
        }
    
        System.out.println(" Функция сериализована в файл: " + filename);
    
        // Десериализация из файла
        TabulatedFunction deserialized;
        try (ObjectInputStream ois = new ObjectInputStream( new FileInputStream(filename))) {
            deserialized = (TabulatedFunction) ois.readObject();
        }
    
        System.out.println(" Десериализованная функция - все 11 точек:");
        for (int i = 0; i < deserialized.getPointsCount(); i++) {
            double x = deserialized.getPointX(i);
            double y = deserialized.getPointY(i);
            double original = tabulated.getPointY(i);
            System.out.printf("  x=%.1f: исходная=%.6f, восстановленная=%.6f, разница=%.6f%n", 
                x, original, y, Math.abs(original - y));
        }
    
        // Проверяем размер файла
        File file = new File(filename);
        System.out.printf(" Размер файла сериализации: %d байт%n", file.length());
        
        file.delete();
        
        System.out.println();
    }
    private static void testExternalizableSerialization() throws IOException, ClassNotFoundException {
        System.out.println("7. тестирование сериализации через externalizable:");
    
        System.out.println("  создаем табулированную функцию натурального логарифма...");
        // создаем функцию натурального логарифма
        Log log = new Log(Math.E);
        // табулируем функцию на отрезке [1, 10] с 10 точками
        TabulatedFunction tabulated = TabulatedFunctions.tabulate(log, 1, 10, 10);
    
        System.out.println("  исходная функция (10 точек натурального логарифма):");
        // выводим все точки исходной функции
        for (int i = 0; i < tabulated.getPointsCount(); i++) {
            System.out.printf("  %s%n", tabulated.getPoint(i));
        }
    
        // сериализация через externalizable
        String externalizableFile = "function_externalizable.ser";
        System.out.println("  сериализуем функцию в файл: " + externalizableFile);
    
        // записываем объект в файл
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(externalizableFile))) {
            oos.writeObject(tabulated); // сериализуем объект
        }
    
        System.out.println("  функция успешно сериализована через externalizable");
    
        // десериализация
        System.out.println("  десериализуем функцию из файла...");
        TabulatedFunction deserializedExternalizable;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(externalizableFile))) {
            deserializedExternalizable = (TabulatedFunction) ois.readObject(); // восстанавливаем объект
        }
    
        System.out.println("  восстановленная функция через externalizable:");
        // сравниваем исходные и восстановленные значения
        for (int i = 0; i < deserializedExternalizable.getPointsCount(); i++) {
            double original = tabulated.getPointY(i); // исходное значение
            double restored = deserializedExternalizable.getPointY(i); // восстановленное значение
            System.out.printf("  точка %d: исходная=%.6f, восстановленная=%.6f, совпадение=%s%n", 
                i, original, restored, Math.abs(original - restored) < 1e-10 ? "да" : "нет");
        }
    
        // анализируем размер файла
        File extFile = new File(externalizableFile);
        System.out.printf("  размер файла externalizable: %d байт%n", extFile.length());
    
        // дополнительное тестирование с linkedlist
        testLinkedListExternalizable();
    
        // очищаем временные файлы
        extFile.delete();
        System.out.println("  временные файлы удалены");
    }

    private static void testLinkedListExternalizable() throws IOException, ClassNotFoundException {
        System.out.println("\n  дополнительное тестирование: linkedlisttabulatedfunction с externalizable");
        
        System.out.println("  создаем linkedlist функцию с 4 точками...");
        // создаем массив точек для linkedlist функции
        FunctionPoint[] points = {
            new FunctionPoint(1, 0),      // ln(1) = 0
            new FunctionPoint(2, 0.693),  // ln(2) ≈ 0.693
            new FunctionPoint(3, 1.099),  // ln(3) ≈ 1.099
            new FunctionPoint(4, 1.386)   // ln(4) ≈ 1.386
        };
        
        // создаем linkedlist табулированную функцию
        LinkedListTabulatedFunction linkedListFunc = new LinkedListTabulatedFunction(points);
        
        String linkedListFile = "linkedlist_externalizable.ser";
        System.out.println("  сериализуем linkedlist функцию в файл: " + linkedListFile);
        
        // сериализуем linkedlist функцию
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(linkedListFile))) {
            oos.writeObject(linkedListFunc);
        }
        
        System.out.println("  десериализуем linkedlist функцию...");
        LinkedListTabulatedFunction restoredLinkedList;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(linkedListFile))) {
            restoredLinkedList = (LinkedListTabulatedFunction) ois.readObject();
        }
        
        // проверяем корректность восстановления
        System.out.println("  linkedlist успешно восстановлен:");
        System.out.println("  количество точек в исходной функции: " + linkedListFunc.getPointsCount());
        System.out.println("  количество точек в восстановленной функции: " + restoredLinkedList.getPointsCount());
        System.out.println("  восстановление прошло " + 
            (linkedListFunc.getPointsCount() == restoredLinkedList.getPointsCount() ? "успешно" : "с ошибками"));
        
        // очищаем временный файл
        new File(linkedListFile).delete();
        System.out.println("  временный файл linkedlist удален");
    }
}