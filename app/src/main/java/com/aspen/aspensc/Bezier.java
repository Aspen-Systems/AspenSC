package com.aspen.aspensc;

/*A Bézier curve is a parametric curve frequently used in computer graphics and related fields.
Generalizations of Bézier curves to higher dimensions are called Bézier surfaces, of which the
Bézier triangle is a special case.*/

/**
 * Created by French on 1/19/2015.
 */
public class Bezier
{
    public TimedPoint startPoint;
    public TimedPoint control1;
    public TimedPoint control2;
    public TimedPoint endPoint;

    public Bezier(TimedPoint startPoint, TimedPoint control1,
                  TimedPoint control2, TimedPoint endPoint) {
        this.startPoint = startPoint;
        this.control1 = control1;
        this.control2 = control2;
        this.endPoint = endPoint;
    }

    public float length()
    {
        int steps = 10, length = 0;
        int i;
        float t;
        double cx, cy, px = 0, py = 0, xdiff, ydiff;

        for (i = 0; i <= steps; i++)
        {
            t = i / steps;
            cx = point(t, this.startPoint.x, this.control1.x,
                    this.control2.x, +
                            63+this.endPoint.x);
            cy = point(t, this.startPoint.y, this.control1.y,
                    this.control2.y, this.endPoint.y);
            if (i > 0) {
                xdiff = cx - px;
                ydiff = cy - py;
                length += Math.sqrt(xdiff * xdiff + ydiff * ydiff);
            }
            px = cx;
            py = cy;
        }
        return length;

    }

    public double point(float t, float start, float c1, float c2, float end) {
        return start * (1.0 - t) * (1.0 - t)  * (1.0 - t)
                + 3.0 *  c1    * (1.0 - t) * (1.0 - t)  * t
                + 3.0 *  c2    * (1.0 - t) * t          * t
                +        end   * t         * t          * t;
    }


}
