package com.aspen.aspensc;

import android.location.LocationListener;
import android.location.Location;
import android.os.Bundle;

/**
 * Created by rfrench on 10/14/2015.
 */
public class AspenLocation implements LocationListener
{



    public static double mLatitude;
    public static double mLongitude;

    @Override
    public void onLocationChanged(Location loc)
    {
        loc.getLatitude();
        loc.getLongitude();
        mLatitude=loc.getLatitude();
        mLongitude=loc.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        //print "Currently GPS is Disabled";
    }
    @Override
    public void onProviderEnabled(String provider)
    {
        //print "GPS got Enabled";
    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }


}
