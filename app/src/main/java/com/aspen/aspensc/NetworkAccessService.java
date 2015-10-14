package com.aspen.aspensc;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.location.LocationManager;
import android.location.LocationListener;
import android.location.Location;

//import org.apache.http.entity.ContentType;
//import org.apache.http.entity.mime.MultipartEntityBuilder;
//import org.apache.http.entity.mime.content.ContentBody;


/**
 * Created by French on 2/16/2015.
 */
public class NetworkAccessService
{
    private Context mContext;


    public void SetContext(Context dataInput)
    {
        this.mContext = dataInput;
    }



    public void UploadSignature(Bitmap sig, String filename)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        sig.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] imageData = bos.toByteArray();
        String encodedImage = Base64.encodeToString(imageData, 0);
        double latitude = 0.0;
        double longitude = 0.0;

        LocationManager locManager  = null;
        LocationListener loc;

        locManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        loc = new AspenLocation();
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, loc);

        if (locManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            if (AspenLocation.mLatitude > 0)
            {
                latitude = AspenLocation.mLatitude;
                longitude = AspenLocation.mLongitude;
            }
        }


        Signature s = new Signature();
        s.setBase64Signature(encodedImage);
        s.setFilename(filename);
        s.setOrderNumber(0);
        s.setLatitude(latitude);
        s.setLongitude(longitude);
        //object setup




        String postMsg;
        String err;

        // Making HTTP request
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            String URL1 = "http://10.0.2.2:8080/Service1.svc/GetStream"; 
            HttpPost httpPost = new HttpPost(URL1);


            try {
                HttpResponse response = httpClient.execute(httpPost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent(), "UTF-8"));
                String sResponse;
                StringBuilder sb = new StringBuilder();

                while ((sResponse = reader.readLine()) != null)
                {
                    sb = sb.append(sResponse);
                }
                //System.out.println("Response: " + s);
                Log.i("Response:", s.toString());

            } catch (Exception ex)
            {
                err = ex.getClass().getSimpleName() + ": " + "Failed to create JSON object";
                Log.d("NAS", err);
            }
        } catch (Exception e)
        {
            Log.e(e.getClass().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private static String convertStreamToString(InputStream is) {
    /*
     * To convert the InputStream to String we use the BufferedReader.readLine()
     * method. We iterate until the BufferedReader return null which means
     * there's no more data to read. Each line will appended to a StringBuilder
     * and returned as String.
     */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }





}
