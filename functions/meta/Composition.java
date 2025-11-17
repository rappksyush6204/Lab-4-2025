package functions.meta;

import functions.Function;
import java.io.*;

public class Composition implements Function, Externalizable {
    private static final long serialVersionUID = 1L;
    private Function f;
    private Function g;
    
    // Конструктор без параметров для Externalizable
    public Composition() {
    }
    
    public Composition(Function f, Function g) {
        this.f = f;
        this.g = g;
    }
    
    // Реализация Externalizable
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(f);
        out.writeObject(g);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        f = (Function) in.readObject();
        g = (Function) in.readObject();
    }
    
    // Реализация Function
    @Override
    public double getLeftDomainBorder() {
        // Область определения композиции f(g(x)) - это такие x, что g(x) ∈ domain(f)
        // Это сложная задача, поэтому для упрощения возвращаем пересечение
        // В реальной реализации нужно искать x такие, что g(x) ∈ [f_left, f_right]
        return Math.max(g.getLeftDomainBorder(), f.getLeftDomainBorder());
    }

    @Override
    public double getRightDomainBorder() {
        return Math.min(g.getRightDomainBorder(), f.getRightDomainBorder());
    }

    @Override
    public double getFunctionValue(double x) {
        // Проверяем, что x в области определения g
        if (x < g.getLeftDomainBorder() - 1e-10 || x > g.getRightDomainBorder() + 1e-10) {
            return Double.NaN;
        }
        
        double gx = g.getFunctionValue(x);
        
        // Проверяем, что g(x) находится в области определения f
        if (gx < f.getLeftDomainBorder() - 1e-10 || gx > f.getRightDomainBorder() + 1e-10) {
            return Double.NaN;
        }
        
        return f.getFunctionValue(gx);
    }
}