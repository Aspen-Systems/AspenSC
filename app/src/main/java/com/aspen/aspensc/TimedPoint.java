package com.aspen.aspensc;

/**
 * Created by French on 1/19/2015.
 */
public class TimedPoint
{
    public final float x;
    public final float y;
    public final long timestamp;

    public TimedPoint(float x, float y)
    {
        this.x = x;
        this.y = y;
        this.timestamp = System.currentTimeMillis(); //calculate the time the user hold the point
    }

    public float velocityFrom(TimedPoint start)
    {
        float velocity = distanceTo(start) / (this.timestamp - start.timestamp);
        if (velocity != velocity) return 0f;
        return velocity;
    }

    public float distanceTo(TimedPoint point)
    {
        return (float) Math.sqrt(Math.pow(point.x - this.x, 2) + Math.pow(point.y - this.y, 2));
    }


}
