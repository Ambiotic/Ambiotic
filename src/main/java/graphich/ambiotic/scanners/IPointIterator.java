package graphich.ambiotic.scanners;

public interface IPointIterator {
    public Point next();
    public Point peek();
    public void reset();
}
