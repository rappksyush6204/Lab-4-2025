package functions.basic;
import functions.Function;
import java.io.Serializable;

// Абстрактный базовый класс для всех тригонометрических функций (abstract - значит нельзя создать объект этого класса напрямую)
// Все тригонометрические функции (sin, cos, tan) имеют одинаковую область определения
public abstract class TrigonometricFunction implements Function, Serializable {
    private static final long serialVersionUID = 1L;
    
    @Override
    public double getLeftDomainBorder() {
        return Double.NEGATIVE_INFINITY;
    }
    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }
}