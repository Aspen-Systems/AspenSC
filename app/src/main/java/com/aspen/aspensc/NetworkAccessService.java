package com.aspen.aspensc;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

/**
 * Created by French on 2/16/2015.
 */
public class NetworkAccessService
{

    public void UploadSignature(Bitmap sig)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        sig.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] imageData = bos.toByteArray();

        // Making HTTP request
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            //change
            String URL1 = "http://10.0.2.2:1978/RestService.svc/uploadImage/";

            HttpPost httpPost = new HttpPost(URL1);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json" );


            httpPost.setEntity(new ByteArrayEntity(imageData));

            HttpResponse response = httpClient.execute(httpPost);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent(), "UTF-8"));
            String sResponse;
            StringBuilder s = new StringBuilder();

            while ((sResponse = reader.readLine()) != null)
            {
                s = s.append(sResponse);
            }
            System.out.println("Response: " + s);
        } catch (Exception e)
        {
            Log.e(e.getClass().getName(), e.getMessage());
            e.printStackTrace();
        }
    }

    public static byte[] getEncoded64ImageStringFromBitmap(Bitmap bitmap)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 70, stream);
        byte[] byteFormat = stream.toByteArray();
        // get the base 64 string
        String imgString = Base64.encodeToString(byteFormat, Base64.NO_WRAP);

        return imgString.getBytes();
        //return imgString;
    }





}
