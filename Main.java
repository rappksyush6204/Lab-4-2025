import functions.*;
import functions.basic.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {  
            testBasicFunctions();
            testMetaFunctions();
            testTabulation();
            testAssignment8();
            testSerialization();
            testExternalizable();
            
        } catch (Exception e) {
            System.out.println("Произошла ошибка: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testBasicFunctions() {
        System.out.println("1. Тестирование базовых функций:");
        
        Exp exp = new Exp();
        System.out.println("   Экспонента:");
        System.out.println("   exp(0) = " + exp.getFunctionValue(0));
        System.out.println("   exp(1) = " + exp.getFunctionValue(1));
        System.out.println("   exp(2) = " + exp.getFunctionValue(2));
        System.out.println("   Область определения: от " + exp.getLeftDomainBorder() + " до " + exp.getRightDomainBorder());
        
        Log log = new Log(Math.E);
        System.out.println("\n   Натуральный логарифм:");
        System.out.println("   ln(1) = " + log.getFunctionValue(1));
        System.out.println("   ln(2) = " + log.getFunctionValue(2));
        System.out.println("   ln(e) = " + log.getFunctionValue(Math.E));
        System.out.println("   Область определения: от " + log.getLeftDomainBorder() + " до " + log.getRightDomainBorder());
        
        Sin sin = new Sin();
        Cos cos = new Cos();
        
        System.out.println("\n   Тригонометрические функции:");
        System.out.println("   sin(0) = " + sin.getFunctionValue(0));
        System.out.println("   sin(π/2) = " + sin.getFunctionValue(Math.PI/2));
        System.out.println("   cos(0) = " + cos.getFunctionValue(0));
        System.out.println("   cos(π) = " + cos.getFunctionValue(Math.PI));
        System.out.println("   Область определения: от " + sin.getLeftDomainBorder() + " до " + sin.getRightDomainBorder());
        
        System.out.println();
    }
    
    private static void testMetaFunctions() {
        System.out.println("2. Тестирование мета-функций:");
        
        Sin sin = new Sin();
        Cos cos = new Cos();
        
        System.out.println("   Сумма квадратов sin²(x) + cos²(x):");
        Function sin2 = Functions.power(sin, 2);
        Function cos2 = Functions.power(cos, 2);
        Function sum = Functions.sum(sin2, cos2);
        
        for (double x = 0; x <= Math.PI; x += 0.5) {
            double result = sum.getFunctionValue(x);
            System.out.println("   x=" + x + ": " + result + " (должно быть 1.0)");
        }
        
        System.out.println("\n   Композиция ln(e^x):");
        Log log = new Log(Math.E);
        Exp exp = new Exp();
        Function composition = Functions.composition(log, exp);
        
        for (double x = 0.5; x <= 2.0; x += 0.5) {
            double result = composition.getFunctionValue(x);
            System.out.println("   x=" + x + ": ln(e^" + x + ") = " + result + " (должно быть " + x + ")");
        }
        
        System.out.println();
    }
    
    private static void testTabulation() {
        System.out.println("3. Тестирование табулирования:");
        
        Sin sin = new Sin();
        
        System.out.println("   Табулирование синуса на [0, π] с 10 точками:");
        TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
        
        System.out.println("   Табулированные точки:");
        for (int i = 0; i < tabulatedSin.getPointsCount(); i++) {
            System.out.println("   " + tabulatedSin.getPoint(i));
        }
        
        System.out.println();
    }
    
    private static void testAssignment8() throws IOException {
        System.out.println("4. Тестирование работы с функциями и файлами:");
        
        // 1. Sin и Cos с шагом 0.1
        System.out.println("   1. Sin и Cos на отрезке [0, π] с шагом 0.1:");
        Sin sin = new Sin();
        Cos cos = new Cos();
        
        System.out.println("   x\t\tSin(x)\t\tCos(x)");
        System.out.println("   ---------------------------------");
        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf("   %.1f\t\t%.6f\t%.6f%n", 
                x, sin.getFunctionValue(x), cos.getFunctionValue(x));
        }
        
        // 2. Табулированные аналоги и сравнение
        System.out.println("\n   2. Сравнение точных и табулированных функций:");
        TabulatedFunction tabSin = TabulatedFunctions.tabulate(sin, 0, Math.PI, 10);
        TabulatedFunction tabCos = TabulatedFunctions.tabulate(cos, 0, Math.PI, 10);
        
        System.out.println("   x\t\tSin(точн)\tSin(табл)\tОшибка\t\tCos(точн)\tCos(табл)\tОшибка");
        System.out.println("   ---------------------------------------------------------------------------------------");
        
        double maxSinError = 0;
        double maxCosError = 0;
        double avgSinError = 0;
        double avgCosError = 0;
        int count = 0;
        
        for (double x = 0; x <= Math.PI; x += 0.1) {
            double exactSin = sin.getFunctionValue(x);
            double tabSinVal = tabSin.getFunctionValue(x);
            double exactCos = cos.getFunctionValue(x);
            double tabCosVal = tabCos.getFunctionValue(x);
            
            double sinError = Math.abs(exactSin - tabSinVal);
            double cosError = Math.abs(exactCos - tabCosVal);
            
            maxSinError = Math.max(maxSinError, sinError);
            maxCosError = Math.max(maxCosError, cosError);
            avgSinError += sinError;
            avgCosError += cosError;
            count++;
            
            System.out.printf("   %.1f\t\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f\t%.6f%n", 
                x, exactSin, tabSinVal, sinError, exactCos, tabCosVal, cosError);
        }
        
        avgSinError /= count;
        avgCosError /= count;
        
        System.out.println("\n   Статистика ошибок:");
        System.out.printf("   Максимальная ошибка Sin: %.8f%n", maxSinError);
        System.out.printf("   Максимальная ошибка Cos: %.8f%n", maxCosError);
        System.out.printf("   Средняя ошибка Sin: %.8f%n", avgSinError);
        System.out.printf("   Средняя ошибка Cos: %.8f%n", avgCosError);
        
        // 3. Сумма квадратов табулированных функций
        System.out.println("\n   3. Сумма квадратов табулированных функций:");
        Function tabSin2 = Functions.power(tabSin, 2);
        Function tabCos2 = Functions.power(tabCos, 2);
        Function tabSum = Functions.sum(tabSin2, tabCos2);
        
        System.out.println("   x\t\tSin²+Cos²\tОжидаемое\tОшибка");
        System.out.println("   ---------------------------------------------");
        
        double maxSumError = 0;
        double avgSumError = 0;
        count = 0;
        
        for (double x = 0; x <= Math.PI; x += 0.1) {
            double result = tabSum.getFunctionValue(x);
            double expected = 1.0;
            double error = Math.abs(result - expected);
            
            maxSumError = Math.max(maxSumError, error);
            avgSumError += error;
            count++;
            
            System.out.printf("   %.1f\t\t%.6f\t%.1f\t\t%.6f%n", 
                x, result, expected, error);
        }
        
        avgSumError /= count;
        
        System.out.println("\n   Статистика для суммы квадратов:");
        System.out.printf("   Максимальная ошибка: %.8f%n", maxSumError);
        System.out.printf("   Средняя ошибка: %.8f%n", avgSumError);
        
        // 4. Работа с файлами
        System.out.println("\n   4. Работа с файлами:");
        testFileOperations();
    }
    
    private static void testFileOperations() throws IOException {
        // Экспонента - символьные потоки
        Exp exp = new Exp();
        TabulatedFunction tabExp = TabulatedFunctions.tabulate(exp, 0, 10, 11);
        
        System.out.println("   Табулированная экспонента (11 точек):");
        for (int i = 0; i < tabExp.getPointsCount(); i++) {
            System.out.println("   " + tabExp.getPoint(i));
        }
        
        // Запись в текстовый файл
        try (FileWriter writer = new FileWriter("exp_function.txt")) {
            TabulatedFunctions.writeTabulatedFunction(tabExp, writer);
            System.out.println("   Экспонента записана в файл: exp_function.txt");
        }
        
        // Чтение из текстового файла
        TabulatedFunction readExp;
        try (FileReader reader = new FileReader("exp_function.txt")) {
            readExp = TabulatedFunctions.readTabulatedFunction(reader);
            System.out.println("   Экспонента прочитана из файла");
        }
        
        System.out.println("   Сравнение исходной и прочитанной экспоненты:");
        double maxExpError = 0;
        double avgExpError = 0;
        int count = 0;
        
        for (int i = 0; i < tabExp.getPointsCount(); i++) {
            double origX = tabExp.getPointX(i);
            double origY = tabExp.getPointY(i);
            double readY = readExp.getFunctionValue(origX);
            double error = Math.abs(origY - readY);
            
            maxExpError = Math.max(maxExpError, error);
            avgExpError += error;
            count++;
            
            System.out.printf("   Точка %d (x=%.1f): исходная=%.6f, прочитанная=%.6f, ошибка=%.10f%n", 
                i, origX, origY, readY, error);
        }
        
        avgExpError /= count;
        System.out.printf("   Максимальная ошибка: %.10f%n", maxExpError);
        System.out.printf("   Средняя ошибка: %.10f%n", avgExpError);
        
        // Логарифм - байтовые потоки
        Log log = new Log(Math.E);
        TabulatedFunction tabLog = TabulatedFunctions.tabulate(log, 0.1, 10, 11);
        
        System.out.println("\n   Табулированный логарифм (11 точек):");
        for (int i = 0; i < tabLog.getPointsCount(); i++) {
            System.out.println("   " + tabLog.getPoint(i));
        }
        
        // Запись в бинарный файл
        try (FileOutputStream out = new FileOutputStream("log_function.dat")) {
            TabulatedFunctions.outputTabulatedFunction(tabLog, out);
            System.out.println("   Логарифм записан в файл: log_function.dat");
        }
        
        // Для сравнения - также записываем в текстовый формат
        try (FileWriter writer = new FileWriter("log_function.txt")) {
            TabulatedFunctions.writeTabulatedFunction(tabLog, writer);
            System.out.println("   Логарифм также записан в текстовый файл: log_function.txt");
        }
        
        // Чтение из бинарного файла
        TabulatedFunction readLog;
        try (FileInputStream in = new FileInputStream("log_function.dat")) {
            readLog = TabulatedFunctions.inputTabulatedFunction(in);
            System.out.println("   Логарифм прочитан из файла");
        }
        
        System.out.println("   Сравнение исходного и прочитанного логарифма:");
        double maxLogError = 0;
        double avgLogError = 0;
        count = 0;
        
        for (int i = 0; i < tabLog.getPointsCount(); i++) {
            double origX = tabLog.getPointX(i);
            double origY = tabLog.getPointY(i);
            double readY = readLog.getFunctionValue(origX);
            double error = Math.abs(origY - readY);
            
            maxLogError = Math.max(maxLogError, error);
            avgLogError += error;
            count++;
            
            System.out.printf("   Точка %d (x=%.1f): исходная=%.6f, прочитанная=%.6f, ошибка=%.10f%n", 
                i, origX, origY, readY, error);
        }
        
        avgLogError /= count;
        System.out.printf("   Максимальная ошибка: %.10f%n", maxLogError);
        System.out.printf("   Средняя ошибка: %.10f%n", avgLogError);
        
        // Сравнение размеров файлов
        File binFile = new File("log_function.dat");
        File textFile = new File("log_function.txt");
        
        System.out.println("\n   Сравнение форматов хранения:");
        System.out.println("   Размер бинарного файла: " + binFile.length() + " байт");
        System.out.println("   Размер текстового файла: " + textFile.length() + " байт");
    }
    
    private static void testSerialization() throws IOException, ClassNotFoundException {
        System.out.println("\n5. Сериализация (Serializable):");
        
        // Создаем функцию ln(e^x)
        Log log = new Log(Math.E);
        Exp exp = new Exp();
        Function composition = Functions.composition(log, exp);
        TabulatedFunction arrayFunc = TabulatedFunctions.tabulate(composition, 0, 5, 6);
        
        System.out.println("   Оригинальная функция ln(e^x):");
        for (int i = 0; i < arrayFunc.getPointsCount(); i++) {
            System.out.println("   " + arrayFunc.getPoint(i));
        }
        
        // Сериализация
        String serializableFile = "function_serializable.ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(serializableFile))) {
            oos.writeObject(arrayFunc);
            System.out.println("   Функция сериализована в файл: " + serializableFile);
        }
        
        // Десериализация
        TabulatedFunction deserializedArray;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(serializableFile))) {
            deserializedArray = (TabulatedFunction) ois.readObject();
            System.out.println("   Функция десериализована из файла");
        }
        
        System.out.println("   Десериализованная функция:");
        for (int i = 0; i < deserializedArray.getPointsCount(); i++) {
            System.out.println("   " + deserializedArray.getPoint(i));
        }
        
        System.out.println("   Сравнение оригинальной и десериализованной функции:");
        double maxError = 0;
        double avgError = 0;
        int count = 0;
        
        for (int i = 0; i < arrayFunc.getPointsCount(); i++) {
            double origX = arrayFunc.getPointX(i);
            double origY = arrayFunc.getPointY(i);
            double deserY = deserializedArray.getFunctionValue(origX);
            double error = Math.abs(origY - deserY);
            
            maxError = Math.max(maxError, error);
            avgError += error;
            count++;
            
            System.out.printf("   Точка %d (x=%.1f): исходная=%.6f, десериализ.=%.6f, ошибка=%.10f%n", 
                i, origX, origY, deserY, error);
        }
        
        avgError /= count;
        System.out.printf("   Максимальная ошибка: %.10f%n", maxError);
        System.out.printf("   Средняя ошибка: %.10f%n", avgError);
        
        // Удаляем временный файл
        new File(serializableFile).delete();
        System.out.println("   Временный файл удален");
    }
    
    private static void testExternalizable() throws IOException, ClassNotFoundException {
        System.out.println("\n6. Сериализация (Externalizable):");
        
        // Создаем функцию через массив точек
        FunctionPoint[] points = {
            new FunctionPoint(1, 0),
            new FunctionPoint(2, 0.693147),
            new FunctionPoint(3, 1.098612),
            new FunctionPoint(4, 1.386294),
            new FunctionPoint(5, 1.609438)
        };
        LinkedListTabulatedFunction linkedListFunc = new LinkedListTabulatedFunction(points);
        
        System.out.println("   Оригинальная функция:");
        for (int i = 0; i < linkedListFunc.getPointsCount(); i++) {
            System.out.println("   " + linkedListFunc.getPoint(i));
        }
        
        // Сериализация
        String externalizableFile = "function_externalizable.ser";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(externalizableFile))) {
            oos.writeObject(linkedListFunc);
            System.out.println("   Функция сериализована в файл: " + externalizableFile);
        }
        
        // Десериализация
        LinkedListTabulatedFunction deserializedLinkedList;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(externalizableFile))) {
            deserializedLinkedList = (LinkedListTabulatedFunction) ois.readObject();
            System.out.println("   Функция десериализована из файла");
        }
        
        System.out.println("   Десериализованная функция:");
        for (int i = 0; i < deserializedLinkedList.getPointsCount(); i++) {
            System.out.println("   " + deserializedLinkedList.getPoint(i));
        }
        
        System.out.println("   Сравнение оригинальной и десериализованной функции:");
        double maxError = 0;
        double avgError = 0;
        int count = 0;
        
        for (int i = 0; i < linkedListFunc.getPointsCount(); i++) {
            double origX = linkedListFunc.getPointX(i);
            double origY = linkedListFunc.getPointY(i);
            double deserY = deserializedLinkedList.getFunctionValue(origX);
            double error = Math.abs(origY - deserY);
            
            maxError = Math.max(maxError, error);
            avgError += error;
            count++;
            
            System.out.printf("   Точка %d (x=%.1f): исходная=%.6f, десериализ.=%.6f, ошибка=%.10f%n", 
                i, origX, origY, deserY, error);
        }
        
        avgError /= count;
        System.out.printf("   Максимальная ошибка: %.10f%n", maxError);
        System.out.printf("   Средняя ошибка: %.10f%n", avgError);
        
        System.out.println("\n   Выводы о способах сериализации:");
        System.out.println("   - Serializable: проще в использовании, автоматическая сериализация");
        System.out.println("   - Externalizable: больше контроля, можно оптимизировать процесс");
        
        // Удаляем временный файл
        new File(externalizableFile).delete();
        System.out.println("   Временный файл удален");
    }
}