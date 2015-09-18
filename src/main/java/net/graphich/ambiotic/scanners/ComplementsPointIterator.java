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
public class ComplementsPointIterator implements IPointIterator {

    protected List<CuboidPointIterator> mSegments;

    public ComplementsPointIterator(Cuboid volume, Cuboid intersect) {
        //This function makes some important assumptions about volume and intersect:
        // 1) Intersect is completely contained within volume
        // 2) Intersect always shares at least 3 faces with volume (shares at least 1 "corner")
        // These conditions are met easily by always taking intersects of two cuboids
        //  of the same size ie:
        //
        // Cuboid oldVolume = new Cuboid(0,0,0,64,32,64);
        // Cuboid newVolume = oldVolume.translated(3,10,20);
        // Cuboid intersect = newVolume.intersect(oldVolume);
        // ComplementsPointIterator outOfRange = new ComplementsPointIterator(oldVolume,intersect);
        // ComplementsPointIterator inRange = new ComplementsPointIterator(newVolume,intersect);
        //
        // See BlockScanner for a practical application of this class for keeping the count of block types
        //   around a player up-to-date without rescanning the entire volume around a player.

        mSegments = new ArrayList<CuboidPointIterator>();

        Point vmax = volume.maximum();
        Point imax = intersect.maximum();
        Point vmin = volume.minimum();
        Point imin = intersect.minimum();

        if (vmax.x != imax.x || vmin.x != imin.x) {
            if (vmax.x > imax.x)
                mSegments.add(new CuboidPointIterator(new Point(imax.x, vmin.y, vmin.z), new Point(vmax.x, vmax.y, vmax.z)));
            else
                mSegments.add(new CuboidPointIterator(new Point(vmin.x, vmin.y, vmin.z), new Point(imin.x, vmax.y, vmax.z)));
        }

        if (vmax.y != imax.y || vmin.y != imin.y) {
            if (vmax.y > imax.y)
                mSegments.add(new CuboidPointIterator(new Point(imin.x, imax.y, vmin.z), new Point(imax.x, vmax.y, vmax.z)));
            else
                mSegments.add(new CuboidPointIterator(new Point(imin.x, vmin.y, vmin.z), new Point(imax.x, imin.y, vmax.z)));
        }

        if (vmax.z != imax.z || vmin.z != imin.z) {
            if (vmax.z > imax.z)
                mSegments.add(new CuboidPointIterator(new Point(imin.x, imin.y, imax.z), new Point(imax.x, imax.y, vmax.z)));
            else
                mSegments.add(new CuboidPointIterator(new Point(imin.x, imin.y, vmin.z), new Point(imax.x, imax.y, imin.z)));
        }

        /* Debugging code
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
        // Show that all points within mSegements are inside v but not in i
        while(p != null) {
            if(!volume.contains(p)) System.out.println("V does not contain : "+p);
            if(intersect.contains(p)) System.out.println("I contains : "+p);
            System.out.println("P Checked : "+p);
            p = this.next();
        }*/
    }

    @Override
    public Point next() {
        for (CuboidPointIterator i : mSegments) {
            Point rv = i.next();
            if (rv != null) {
                return rv;
            }
        }
        return null;
    }

    @Override
    public void reset() {
        for (CuboidPointIterator i : mSegments) {
            i.reset();
        }
    }
}
