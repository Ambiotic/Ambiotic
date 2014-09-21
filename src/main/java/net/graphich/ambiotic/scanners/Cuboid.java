package net.graphich.ambiotic.scanners;

import java.util.ArrayList;


/**
 * Created by Jim on 9/4/2014.
 */
public class Cuboid {
    protected ArrayList<Point> mVertices;
    protected int mHeight;
    protected int mWidth;
    protected int mLength;
    protected Point mMin;
    protected Point mMax;

    public Cuboid(int centerX, int centerY, int centerZ, int width, int height, int length) {
        init(
                new Point(centerX - width / 2, centerY - height / 2, centerZ - length / 2),
                new Point(centerX + width / 2, centerY + height / 2, centerZ + length / 2)
        );
    }

    public Cuboid(Point vx1, Point vx2) {
        init(vx1, vx2);
    }

    protected void init(Point vx1, Point vx2) {

        if (!vx1.canFormCuboid(vx2))
            throw new IllegalArgumentException("Cannot form cuboid from vx1 and vx2");

        mVertices = new ArrayList<Point>();
        mVertices.add(new Point(vx1.x, vx1.y, vx1.z));
        mVertices.add(new Point(vx2.x, vx2.y, vx2.z));
        mVertices.add(new Point(vx1.x, vx2.y, vx2.z));
        mVertices.add(new Point(vx2.x, vx1.y, vx1.z));
        mVertices.add(new Point(vx2.x, vx1.y, vx2.z));
        mVertices.add(new Point(vx1.x, vx1.y, vx2.z));
        mVertices.add(new Point(vx1.x, vx2.y, vx1.z));
        mVertices.add(new Point(vx2.x, vx2.y, vx1.z));
        mMin = new Point(mVertices.get(0));
        mMax = new Point(mVertices.get(0));
        for (Point p : mVertices) {
            if (mMin.y > p.y) mMin.y = p.y;
            if (mMin.z > p.z) mMin.z = p.z;
            if (mMin.x > p.x) mMin.x = p.x;
            if (mMax.y < p.y) mMax.y = p.y;
            if (mMax.z < p.z) mMax.z = p.z;
            if (mMax.x < p.x) mMax.x = p.x;

        }
        mLength = mMax.x - mMin.x;
        mHeight = mMax.y - mMin.y;
        mWidth = mMax.z - mMin.z;
    }

    public boolean contains(Point p) {
        return (
                mMin.z <= p.z && p.z <= mMax.z &&
                        mMin.y <= p.y && p.y <= mMax.y &&
                        mMin.x <= p.x && p.x <= mMax.x
        );
    }

    public void translate(int dx, int dy, int dz) {
        for (Point p : mVertices) {
            p.translate(dx, dy, dz);
        }
    }

    public Cuboid translated(int dx, int dy, int dz) {
        return new Cuboid(
                new Point(mMin.x + dx, mMin.y + dy, mMin.z + dz),
                new Point(mMax.x + dx, mMax.y + dy, mMax.z + dz)
        );
    }

    public Point maximum() {
        return new Point(mMax.x, mMax.y, mMax.z);
    }

    public Point minimum() {
        return new Point(mMin.x, mMin.y, mMin.z);
    }

    public String toString() {
        String me = "";
        for (Point p : mVertices) {
            me += "{" + p.toString() + "}\n";
        }
        me += "Min:" + mMin.toString() + "\n";
        me += "Max:" + mMax.toString() + "\n";
        return me;
    }

    public long volume() {
        return mWidth * mHeight * mLength;
    }

    public Cuboid intersection(Cuboid o) {
        Point vx1 = null;
        Point vx2 = null;
        for (Point vx : mVertices) {
            if (o.contains(vx)) {
                vx1 = new Point(vx.x, vx.y, vx.z);
                break;
            }
        }
        for (Point vx : o.mVertices) {
            if (this.contains(vx) && vx.canFormCuboid(vx1)) {
                vx2 = new Point(vx.x, vx.y, vx.z);
                break;
            }
        }
        if (vx1 == null || vx2 == null)
            return null;
        return new Cuboid(vx1, vx2);
    }

    public boolean equals(Cuboid o) {
        if (o.volume() != volume())
            return false;
        for (int i = 0; i < 8; i++) {
            if (!mVertices.get(i).equals(o.mVertices.get(i)))
                return false;
        }
        return true;
    }
}
