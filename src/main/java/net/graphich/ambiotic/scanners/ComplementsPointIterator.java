package net.graphich.ambiotic.scanners;

import java.util.ArrayList;
import java.util.List;

/**
 * For 2 Cuboid objects of equal dimensions displaced in space (c1,c2)
 * that intersect to form the cuboid i there are at most 3 cuboids
 * describing the space in c1 not in i and at most 3 cuboids
 * describing the space in c2 not in i.  This class builds those 3
 * cuboids as "segments" so that the space in c1 or c2 but not in i
 * can be iterated efficiently.
 */
public class ComplementsPointIterator implements PointIterator {

    protected List<CuboidPointIterator> mSegments;

    public ComplementsPointIterator(Cuboid volume, Cuboid intersect)
    {
        mSegments  = new ArrayList<CuboidPointIterator>();

        Point vmax = volume.maximum();
        Point imax = intersect.maximum();
        Point vmin = volume.minimum();
        Point imin = intersect.minimum();

        if(vmax.x != imax.x || vmin.x != imin.x) {
            if(vmax.x > imax.x)
                mSegments.add(new CuboidPointIterator(new Point(imax.x,vmin.y,vmin.z),new Point(vmax.x,vmax.y,vmax.z)));
            else
                mSegments.add(new CuboidPointIterator(new Point(vmin.x,vmin.y,vmin.z),new Point(imin.x,vmax.y,vmax.z)));
        }

        if(vmax.y != imax.y || vmin.y != imin.y) {
            if(vmax.y > imax.y)
                mSegments.add(new CuboidPointIterator(new Point(imin.x,imax.y,vmin.z), new Point(imax.x,vmax.y,vmax.z)));
            else
                mSegments.add(new CuboidPointIterator(new Point(imin.x,vmin.y,vmin.z), new Point(imax.x,imin.y,vmax.z)));
        }

        if(vmax.z != imax.z || vmin.z != imin.z) {
            if(vmax.z > imax.z)
                mSegments.add(new CuboidPointIterator(new Point(imin.x,imin.y,imax.z), new Point(imax.x,imax.y,vmax.z)));
            else
                mSegments.add(new CuboidPointIterator(new Point(imin.x,imin.y,vmin.z), new Point(imax.x,imax.y,imin.z)));
        }


        System.out.println("V : "+volume);
        System.out.println("I : "+intersect);
        System.out.println("Diff : "+(volume.volume()-intersect.volume()));
        long cvol = 0;
        for(Cuboid c : mSegments) {
            System.out.println("Segment : "+c);
            cvol += c.volume();
        }
        System.out.println("Calc : "+cvol);
        Point p = this.next();
        while(p != null) {
            if(!volume.contains(p)) System.out.println("V does not contain : "+p);
            if(intersect.contains(p)) System.out.println("I contains : "+p);
            p = this.next();
        }
    }

    @Override
    public Point next() {
        for(CuboidPointIterator i : mSegments) {
            Point rv = i.next();
            if(rv != null) {
                return rv;
            }
        }
        return null;
    }

    @Override
    public void reset() {

    }
}
