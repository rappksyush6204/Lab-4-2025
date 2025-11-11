package functions;

import java.io.*;

public class LinkedListTabulatedFunction implements TabulatedFunction, Serializable, Externalizable {
    private static final long serialVersionUID = 1L;
    
    // Внутренний класс для узла списка
    private static class FunctionNode implements Serializable, Externalizable {
        private static final long serialVersionUID = 1L;
        private FunctionPoint point;
        private FunctionNode prev;
        private FunctionNode next;
        
        // Конструктор без параметров для Externalizable
        public FunctionNode() {
        }
        
        public FunctionNode(FunctionPoint point) {
            this.point = point;
        }
        
        public FunctionPoint getPoint() {
            return point;
        }
        
        public void setPoint(FunctionPoint point) {
            this.point = point;
        }
        
        public FunctionNode getPrev() {
            return prev;
        }
        
        public void setPrev(FunctionNode prev) {
            this.prev = prev;
        }
        
        public FunctionNode getNext() {
            return next;
        }
        
        public void setNext(FunctionNode next) {
            this.next = next;
        }
        
        // Реализация Externalizable для FunctionNode
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(point);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            point = (FunctionPoint) in.readObject();
        }
    }
    
    private FunctionNode head;
    private int pointsCount;
    private transient FunctionNode lastAccessedNode; // transient - не сериализуем
    private transient int lastAccessedIndex;

    // КОНСТРУКТОРЫ
    
    // Конструктор без параметров для Externalizable
    public LinkedListTabulatedFunction() {
        initEmptyList();
    }
    
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Должно быть не менее 2 точек");
        }
        
        initEmptyList();
        double step = (rightX - leftX) / (pointsCount - 1);
        
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            FunctionPoint point = new FunctionPoint(x, 0.0);
            addNodeToTail().setPoint(point);
        }
    }
    
    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Должно быть не менее 2 точек");
        }
        
        initEmptyList();
        double step = (rightX - leftX) / (values.length - 1);
        
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * step;
            FunctionPoint point = new FunctionPoint(x, values[i]);
            addNodeToTail().setPoint(point);
        }
    }
    
    // ЗАДАНИЕ 1: Конструктор с массивом точек
    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }
        
        // Проверяем упорядоченность по X с учетом машинного эпсилона
        for (int i = 0; i < points.length - 1; i++) {
            if (points[i + 1].getX() - points[i].getX() < 1e-10) {
            throw new IllegalArgumentException("Точки должны быть строго упорядочены по возрастанию X");
            }
        }
        
        initEmptyList();
        for (FunctionPoint point : points) {
            addNodeToTail().setPoint(new FunctionPoint(point));
        }
    }
    
    private void initEmptyList() {
        head = new FunctionNode(null);
        head.setPrev(head);
        head.setNext(head);
        pointsCount = 0;
        lastAccessedNode = head;
        lastAccessedIndex = -1;
    }
    
    // РЕАЛИЗАЦИЯ EXTERNALIZABLE
    
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);
        
        // Записываем все точки
        FunctionNode current = head.getNext();
        while (current != head) {
            out.writeDouble(current.getPoint().getX());
            out.writeDouble(current.getPoint().getY());
            current = current.getNext();
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        int count = in.readInt();
        initEmptyList();
        
        // Восстанавливаем точки
        for (int i = 0; i < count; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            FunctionPoint point = new FunctionPoint(x, y);
            addNodeToTail().setPoint(point);
        }
    }
    
    // ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ
    
    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс за границами: " + index);
        }
        
        FunctionNode node;
        int currentIndex;
        
        // Оптимизация: начинаем поиск с последнего доступного узла, если это эффективно
        if (lastAccessedNode != head && lastAccessedIndex != -1) {
            int distanceFromLast = Math.abs(index - lastAccessedIndex);
            int distanceFromStart = index;
            int distanceFromEnd = pointsCount - 1 - index;
            
            if (distanceFromLast <= distanceFromStart && distanceFromLast <= distanceFromEnd) {
                node = lastAccessedNode;
                currentIndex = lastAccessedIndex;
            } else if (distanceFromStart <= distanceFromEnd) {
                node = head.getNext();
                currentIndex = 0;
            } else {
                node = head.getPrev();
                currentIndex = pointsCount - 1;
            }
        } else {
            node = head.getNext();
            currentIndex = 0;
        }
        
        // Перемещаемся к нужному индексу
        while (currentIndex != index) {
            if (currentIndex < index) {
                node = node.getNext();
                currentIndex++;
            } else {
                node = node.getPrev();
                currentIndex--;
            }
        }
        
        lastAccessedNode = node;
        lastAccessedIndex = currentIndex;
        return node;
    }
    
    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(new FunctionPoint(0.0, 0.0));
        FunctionNode tail = head.getPrev();
        
        newNode.setPrev(tail);
        newNode.setNext(head);
        tail.setNext(newNode);
        head.setPrev(newNode);
        
        pointsCount++;
        return newNode;
    }
    
    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс за границами: " + index);
        }
        
        FunctionNode newNode = new FunctionNode(new FunctionPoint(0.0, 0.0));
        FunctionNode targetNode;
        
        if (index == pointsCount) {
            targetNode = head;
        } else {
            targetNode = getNodeByIndex(index);
        }
        
        FunctionNode prevNode = targetNode.getPrev();
        newNode.setPrev(prevNode);
        newNode.setNext(targetNode);
        prevNode.setNext(newNode);
        targetNode.setPrev(newNode);
        
        pointsCount++;
        lastAccessedNode = newNode;
        lastAccessedIndex = index;
        return newNode;
    }
    
    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException("Индекс за границами: " + index);
        }
        
        FunctionNode nodeToDelete = getNodeByIndex(index);
        FunctionNode prevNode = nodeToDelete.getPrev();
        FunctionNode nextNode = nodeToDelete.getNext();
        
        prevNode.setNext(nextNode);
        nextNode.setPrev(prevNode);
        
        pointsCount--;
        
        if (lastAccessedNode == nodeToDelete) {
            lastAccessedNode = head;
            lastAccessedIndex = -1;
        }
        
        return nodeToDelete;
    }
    
    // РЕАЛИЗАЦИЯ МЕТОДОВ TabulatedFunction
    
    @Override
    public int getPointsCount() {
        return pointsCount;
    }
    
    @Override
    public FunctionPoint getPoint(int index) {
        FunctionNode node = getNodeByIndex(index);
        return new FunctionPoint(node.getPoint());
    }
    
    @Override
    public void setPoint(int index, FunctionPoint point) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        
        // Проверяем упорядоченность с учетом машинного эпсилона
        if (index > 0) {
            double prevX = getNodeByIndex(index - 1).getPoint().getX();
            if (point.getX() <= prevX + 1e-10) {
                throw new InappropriateFunctionPointException("X точки должен быть больше предыдущего");
            }
        }
        if (index < pointsCount - 1) {
            double nextX = getNodeByIndex(index + 1).getPoint().getX();
            if (point.getX() >= nextX - 1e-10) {
                throw new InappropriateFunctionPointException("X точки должен быть меньше следующего");
            }
        }
        
        node.setPoint(new FunctionPoint(point));
    }
    
    @Override
    public double getPointX(int index) {
        FunctionNode node = getNodeByIndex(index);
        return node.getPoint().getX();
    }
    
    @Override
    public void setPointX(int index, double x) throws FunctionPointIndexOutOfBoundsException, InappropriateFunctionPointException {
        FunctionNode node = getNodeByIndex(index);
        FunctionPoint currentPoint = node.getPoint();
        
        // Проверяем упорядоченность
        if (index > 0) {
            double prevX = getNodeByIndex(index - 1).getPoint().getX();
            if (x <= prevX + 1e-10) {
                throw new InappropriateFunctionPointException("X точки должен быть больше предыдущего");
            }
        }
        if (index < pointsCount - 1) {
            double nextX = getNodeByIndex(index + 1).getPoint().getX();
            if (x >= nextX - 1e-10) {
                throw new InappropriateFunctionPointException("X точки должен быть меньше следующего");
            }
        }
        
        node.setPoint(new FunctionPoint(x, currentPoint.getY()));
    }
    
    @Override
    public double getPointY(int index) {
        FunctionNode node = getNodeByIndex(index);
        return node.getPoint().getY();
    }
    
    @Override
    public void setPointY(int index, double y) {
        FunctionNode node = getNodeByIndex(index);
        FunctionPoint currentPoint = node.getPoint();
        node.setPoint(new FunctionPoint(currentPoint.getX(), y));
    }
    
    @Override
    public void deletePoint(int index) {
        if (pointsCount < 3) {
            throw new IllegalStateException("Нельзя удалить точку: должно остаться минимум 2 точки");
        }
        deleteNodeByIndex(index);
    }
    
    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Ищем позицию для вставки
        int insertIndex = pointsCount;
        for (int i = 0; i < pointsCount; i++) {
            double currentX = getPointX(i);
            
            // Проверяем на дубликат с учетом машинного эпсилона
            if (Math.abs(currentX - point.getX()) < 1e-10) {
                throw new InappropriateFunctionPointException("Точка с таким X уже существует");
            }
            
            if (currentX > point.getX()) {
                insertIndex = i;
                break;
            }
        }
        
        FunctionNode newNode = addNodeByIndex(insertIndex);
        newNode.setPoint(new FunctionPoint(point));
    }
    
    // РЕАЛИЗАЦИЯ МЕТОДОВ Function
    
    @Override
    public double getLeftDomainBorder() {
        return pointsCount == 0 ? Double.NaN : head.getNext().getPoint().getX();
    }
    
    @Override
    public double getRightDomainBorder() {
        return pointsCount == 0 ? Double.NaN : head.getPrev().getPoint().getX();
    }
    
    @Override
    public double getFunctionValue(double x) {
        if (pointsCount == 0) {
            return Double.NaN;
        }
        
        double leftBorder = getLeftDomainBorder();
        double rightBorder = getRightDomainBorder();
        
        if (x < leftBorder - 1e-10 || x > rightBorder + 1e-10) {
            return Double.NaN;
        }
        
        // Линейная интерполяция между точками
        for (int i = 0; i < pointsCount - 1; i++) {
            double x1 = getPointX(i);
            double x2 = getPointX(i + 1);
            
            if (x >= x1 - 1e-10 && x <= x2 + 1e-10) {
                double y1 = getPointY(i);
                double y2 = getPointY(i + 1);
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        
        return Double.NaN;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        FunctionNode current = head.getNext();
        
        for (int i = 0; i < pointsCount; i++) {
            sb.append(current.getPoint().toString());
            if (i < pointsCount - 1) {
                sb.append("\n");
            }
            current = current.getNext();
        }
        
        return sb.toString();
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        FunctionPoint[] pointsArray = new FunctionPoint[pointsCount];
        FunctionNode current = head.getNext();
        
        for (int i = 0; i < pointsCount; i++) {
            pointsArray[i] = new FunctionPoint(current.getPoint());
            current = current.getNext();
        }
        
        return new LinkedListTabulatedFunction(pointsArray);
    }
}