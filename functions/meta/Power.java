package functions.meta;

import functions.Function;
import java.io.*;

public class Power implements Function, Externalizable {
    private static final long serialVersionUID = 1L;
    private Function f;
    private double power;
    
    // Конструктор без параметров для Externalizable
    public Power() {
    }
    
    public Power(Function f, double power) {
        this.f = f;
        this.power = power;
    }
    
    // Реализация Externalizable
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(f);
        out.writeDouble(power);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        f = (Function) in.readObject();
        power = in.readDouble();
    }
    
    // Реализация Function
    @Override
    public double getLeftDomainBorder() {
        // Для отрицательных степеней и дробных степеней нужны дополнительные проверки
        // По заданию считаем, что область определения совпадает
        return f.getLeftDomainBorder();
    }
    
    @Override
    public double getRightDomainBorder() {
        return f.getRightDomainBorder();
    }
    
    @Override
    public double getFunctionValue(double x) {
        // Проверяем, что x в области определения базовой функции
        if (x < f.getLeftDomainBorder() - 1e-10 || x > f.getRightDomainBorder() + 1e-10) {
            return Double.NaN;
        }
        
        double baseValue = f.getFunctionValue(x);
        
        // Проверяем особые случаи:
        
        // 1. Отрицательное основание и дробная степень
        if (baseValue < 0 && power != (int)power) {
            return Double.NaN; // Корень четной степени из отрицательного числа
        }
        
        // 2. Нулевое основание и отрицательная степень
        if (Math.abs(baseValue) < 1e-10 && power < 0) {
            return Double.NaN; // Деление на ноль
        }
        
        // 3. Нулевое основание и нулевая степень (0^0)
        if (Math.abs(baseValue) < 1e-10 && Math.abs(power) < 1e-10) {
            return Double.NaN; // Неопределенность 0^0
        }
        
        return Math.pow(baseValue, power);
    }
}