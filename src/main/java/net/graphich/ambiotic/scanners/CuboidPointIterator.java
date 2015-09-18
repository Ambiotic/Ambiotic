package net.graphich.ambiotic.scanners;

public class CuboidPointIterator extends Cuboid implements IPointIterator {

    protected Point mCurrent;

    public CuboidPointIterator(Point p1, Point p2) {
        super(p1, p2);
        reset();
    }

    public CuboidPointIterator(int centerX, int centerY, int centerZ, int sizeX, int sizeY, int sizeZ) {
        super(centerX, centerY, centerZ, sizeX, sizeY, sizeZ);
        reset();
    }

    @Override
    public Point next() {
        if (mCurrent.x >= mMax.x)
            return null;

        Point location = new Point(mCurrent);

        mCurrent.z += 1;
        if (mCurrent.z >= mMax.z) {
            mCurrent.z = mMin.z;
            mCurrent.y += 1;
        }
        if (mCurrent.y >= mMax.y) {
            mCurrent.y = mMin.y;
            mCurrent.x += 1;
        }
        return location;
    }

    @Override
    public void reset() {
        mCurrent = new Point(mMin);
    }

}
