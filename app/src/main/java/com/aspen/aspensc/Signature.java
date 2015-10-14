package com.aspen.aspensc;

/**
 * Created by rfrench on 10/14/2015.
 */
public class Signature
{


    private String mBase64Signature;
    private String mFilename;
    private int mOrderNumber;
    private double mLatitude;
    private double mLongitude;


    public String getBase64Signature() {
        return mBase64Signature;
    }

    public void setBase64Signature(String mBase64Signature) {
        this.mBase64Signature = mBase64Signature;
    }

    public String getFilename() {
        return mFilename;
    }

    public void setFilename(String mFilename) {
        this.mFilename = mFilename;
    }

    public int getOrderNumber() {
        return mOrderNumber;
    }

    public void setOrderNumber(int mOrderNumber) {
        this.mOrderNumber = mOrderNumber;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }



}
