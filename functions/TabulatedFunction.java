package functions;

//Изменения для 2 ЗАДАНИЯ: сделала наследником Function 
// и удалила getLeftDomainBorder(), getRightDomainBorder(), getFunctionValue(double x)

public interface TabulatedFunction extends Function {
    int getPointsCount();
    
    FunctionPoint getPoint(int index) throws FunctionPointIndexOutOfBoundsException;
    
    void setPoint(int index, FunctionPoint point) 
        throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;
    
    double getPointX(int index) throws FunctionPointIndexOutOfBoundsException;
    
    void setPointX(int index, double x) 
        throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException;
    
    double getPointY(int index) throws FunctionPointIndexOutOfBoundsException;
    
    void setPointY(int index, double y) throws FunctionPointIndexOutOfBoundsException;
    
    void deletePoint(int index) throws IllegalStateException, FunctionPointIndexOutOfBoundsException;
    
    void addPoint(FunctionPoint point) throws InappropriateFunctionPointException;
    
    String toString();
}