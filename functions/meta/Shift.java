package functions.meta;

import functions.Function;
import java.io.*;

public class Shift implements Function, Externalizable {
    private static final long serialVersionUID = 1L;
    private Function f;
    private double shiftX;
    private double shiftY;
    
    // Конструктор без параметров для Externalizable
    public Shift() {
    }
    
    public Shift(Function f, double shiftX, double shiftY) {
        this.f = f;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
    }
    
    // Реализация Externalizable
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(f);
        out.writeDouble(shiftX);
        out.writeDouble(shiftY);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        f = (Function) in.readObject();
        shiftX = in.readDouble();
        shiftY = in.readDouble();
    }
    
    // Реализация Function
    @Override
    public double getLeftDomainBorder() {
        return f.getLeftDomainBorder() + shiftX;
    }
    
    @Override
    public double getRightDomainBorder() {
        return f.getRightDomainBorder() - shiftX;
    }
    
    @Override
    public double getFunctionValue(double x) {
        return shiftY + f.getFunctionValue(x - shiftX);
    }
}