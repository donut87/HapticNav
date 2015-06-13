package donut87.de.navigationvibe;

/**
 * Created by nrj on 13/06/15.
 */
public class Vector2 {
    public double x = 0.0;
    public double y = 0.0;
    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 normalized() {

        return new Vector2(this.x * 1.0 / this.length(), this.y * 1.0 / this.length());
    }

    public double length() {

        return Math.sqrt(this.x * this.x + this.y * this.y);
    }

    /*
    ccpNormalize(const CGPoint v)
    {
        return ccpMult(v, 1.0f/ccpLength(v));
    }
    */
}
