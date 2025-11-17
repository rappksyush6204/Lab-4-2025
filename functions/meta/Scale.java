package functions.meta;

import functions.Function;
import java.io.*;

public class Scale implements Function, Externalizable {
    private static final long serialVersionUID = 1L;
    private Function f;
    private double scaleX;
    private double scaleY;
    
    // Конструктор без параметров для Externalizable
    public Scale() {
    }
    
    public Scale(Function f, double scaleX, double scaleY) {
        this.f = f;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
    }
    
    // Реализация Externalizable
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(f);
        out.writeDouble(scaleX);
        out.writeDouble(scaleY);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        f = (Function) in.readObject();
        scaleX = in.readDouble();
        scaleY = in.readDouble();
    }
    
    // Реализация Function
    @Override
    public double getLeftDomainBorder() {
        if (Math.abs(scaleX) < 1e-10) {
            return Double.NEGATIVE_INFINITY;
        }
        // Учитываем направление масштабирования
        if (scaleX >= 0) {
            return f.getLeftDomainBorder() * scaleX;
        } else {
            return f.getRightDomainBorder() * scaleX;
        }
    }

    @Override
    public double getRightDomainBorder() {
        if (Math.abs(scaleX) < 1e-10) {
            return Double.POSITIVE_INFINITY;
        }
        // Учитываем направление масштабирования  
        if (scaleX >= 0) {
            return f.getRightDomainBorder() * scaleX;
        } else {
            return f.getLeftDomainBorder() * scaleX;
        }
    }

    @Override
    public double getFunctionValue(double x) {
        if (Math.abs(scaleX) < 1e-10) {
            // При scaleX = 0 функция становится КОНСТАНТОЙ f(0)
            return scaleY * f.getFunctionValue(0);
        }
        // При scaleX ≠ 0 применяем нормальное масштабирование
        return scaleY * f.getFunctionValue(x / scaleX);
    }
}