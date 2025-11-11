package functions.meta;

import functions.Function;
import java.io.*;

// Класс Mult представляет произведение двух функций: f(x) * g(x)
// Область определения - пересечение областей определения f и g
public class Mult implements Function, Serializable, Externalizable {
    private static final long serialVersionUID = 1L;
    private Function f1;
    private Function f2;
    
    // Конструктор без параметров для Externalizable
    public Mult() {
    }
    
    public Mult(Function f1, Function f2) {
        this.f1 = f1;
        this.f2 = f2;
    }
    
    // Реализация Externalizable
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(f1);
        out.writeObject(f2);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        f1 = (Function) in.readObject();
        f2 = (Function) in.readObject();
    }
    
    // Реализация Function
    @Override
    public double getLeftDomainBorder() {
        return Math.max(f1.getLeftDomainBorder(), f2.getLeftDomainBorder());
    }
    
    @Override
    public double getRightDomainBorder() {
        return Math.min(f1.getRightDomainBorder(), f2.getRightDomainBorder());
    }
    
    @Override
    public double getFunctionValue(double x) {
        // Проверяем, что x в области определения каждлй из функций
        if (x < f1.getLeftDomainBorder() - 1e-10 || x > f1.getRightDomainBorder() + 1e-10) {
            return Double.NaN;
        }
        if (x < f2.getLeftDomainBorder() - 1e-10 || x > f2.getRightDomainBorder() + 1e-10) {
            return Double.NaN;
        }
        
        double value1 = f1.getFunctionValue(x);
        double value2 = f2.getFunctionValue(x);
        
        // Проверяем на особые случаи
        if (Double.isNaN(value1) || Double.isNaN(value2)) {
            return Double.NaN;
        }
        
        return value1 * value2;
    }
}