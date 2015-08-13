package com.aspen.aspensc;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by French on 2/16/2015.
 */
public class NetworkAccessService
{

    public void UploadSignature(Bitmap sig, String filename)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        sig.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] imageData = bos.toByteArray();
        String encodedImage;
        String postMsg;
        String err;

        // Making HTTP request
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            String URL1 = "http://10.0.2.2:8080/Service1.svc/GetStream"; 
            HttpPost httpPost = new HttpPost(URL1);

            ContentBody bin = null;
            ByteArrayEntity Bae = new ByteArrayEntity(imageData);
            httpPost.setEntity(Bae);

            MultipartEntityBuilder mime = MultipartEntityBuilder.create();
            mime.addBinaryBody("image", imageData, ContentType.APPLICATION_OCTET_STREAM, filename);


            try {
                HttpResponse response = httpClient.execute(httpPost);
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        response.getEntity().getContent(), "UTF-8"));
                String sResponse;
                StringBuilder s = new StringBuilder();

                while ((sResponse = reader.readLine()) != null)
                {
                    s = s.append(sResponse);
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


    public String Test(String id)
    {
        String result;
        result = "bad request id: " + id;
        // Making HTTP request

        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            //change
            //String URL1 = "http://10.0.2.2:65007/RestService.svc/uploadImage/";
            String URL1 = "http://10.0.2.2:8080/CanopyWebService.svc/GetResult/" + id; //TODO make this a variable that is defined in a settings screen

            HttpGet httpget = new HttpGet(URL1);

            HttpResponse response = httpClient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null)
            {

                // A Simple JSON Response Read
                InputStream aa = entity.getContent();
                result = convertStreamToString(aa);
                // now you have the string representation of the HTML request
                aa.close();

            }

        } catch (Exception e)
        {
            Log.e(e.getClass().getName(), e.getMessage());
            e.printStackTrace();
        }
        return result;
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
