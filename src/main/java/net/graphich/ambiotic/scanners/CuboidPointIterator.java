package net.graphich.ambiotic.scanners;

/**
 * Created by jim on 9/14/2014.
 */
public class CuboidPointIterator extends Cuboid implements PointIterator {

    protected Point mCurrent;
    protected Point mCenter;

    @Override
    public Point next() {
        if(mCurrent.x > mMax.x)
            return null;

        Point location = new Point(mCurrent);

        mCurrent.z += 1;
        if(mCurrent.z > mMax.z) {
            mCurrent.z = mMin.z;
            mCurrent.y += 1;
        }
        if(mCurrent.y > mMax.y) {
            mCurrent.y = mMin.y;
            mCurrent.x += 1;
        }
        return location;
    }

    @Override
    public void translate(int dx, int dy, int dz)
    {
        super.translate(dx,dy,dz);
        mCenter.translate(dx,dy,dz);
    }

    @Override
    public void reset() {
        mCurrent = new Point(mMin);
    }

    public Point center() {
        return new Point(mCenter);
    }

    public CuboidPointIterator(int centerX, int centerY, int centerZ, int sizeX, int sizeY, int sizeZ) {
        super(
            new Point(centerX-sizeX/2,centerY-sizeY/2,centerZ-sizeZ/2),
            new Point(centerX+sizeX/2,centerY+sizeY/2,centerZ+sizeZ/2)
        );
        mCenter = new Point(centerX,centerY,centerZ);
        reset();
    }
}
