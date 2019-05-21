package oss.technion.openstreetheight.model.geo;

class Point {
    public final double x;
    public final double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // is a->b->c a counter-clockwise turn?
    // +1 if counter-clockwise, -1 if clockwise, 0 if collinear
    public static int ccw(Point a, Point b, Point c) {
        // return a.x*b.y - a.y*b.x + a.y*c.x - a.x*c.y + b.x*c.y - b.y*c.x;
        double area2 = (b.x - a.x) * (c.y - a.y) - (c.x - a.x) * (b.y - a.y);
        if      (area2 < 0) return -1;
        else if (area2 > 0) return +1;
        else                return  0;
    }
}
