package functions.meta;

import functions.Function;
import java.io.*;

public class Sum implements Function, Serializable {
    private static final long serialVersionUID = 1L;
    private Function f1;
    private Function f2;
    
    public Sum(Function f1, Function f2) {
        this.f1 = f1;
        this.f2 = f2;
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
        // Проверяем, что x в области определения каждой из функций
        if (x < f1.getLeftDomainBorder() - 1e-10 || x > f1.getRightDomainBorder() + 1e-10) {
            return Double.NaN;
        }
        if (x < f2.getLeftDomainBorder() - 1e-10 || x > f2.getRightDomainBorder() + 1e-10) {
            return Double.NaN;
        }
        
        double value1 = f1.getFunctionValue(x);
        double value2 = f2.getFunctionValue(x);
        
        // Проверяем на особые случаи (бесконечности)
        if (Double.isNaN(value1) || Double.isNaN(value2)) {
            return Double.NaN;
        }
        
        return value1 + value2;
    }
}