package net.graphich.ambiotic.scanners;

public class Point {
    public int x;
    public int y;
    public int z;

    public Point(Point p) {
        x = p.x;
        y = p.y;
        z = p.z;
    }

    public Point(int i, int j, int k) {
        x = i;
        y = j;
        z = k;
    }

    public void translate(int dx, int dy, int dz) {
        x += dx;
        y += dy;
        z += dz;
    }

    public boolean canFormCuboid(Point p) {
        return !(x == p.x || z == p.z || y == p.y);
    }

    public String toString() {
        return " " + x + "," + y + "," + z;
    }

    public boolean equals(Point o) {
        return x == o.x && y == o.y && z == o.z;
    }
}
