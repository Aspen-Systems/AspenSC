package com.aspen.aspensc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.String;



public class MainActivity3 extends ActionBarActivity
{
    private static final String TAG = "MainActivity3";
    private SignatureView sv;
    private Button btnClear;
    private Button btnCancel;

    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity3);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        final SignatureView sv = (SignatureView)findViewById(R.id.signatureView);
        Button btnClear =  (Button)findViewById(R.id.btnClear);
        Button btnCancel = (Button)findViewById(R.id.btnCancel);
        Button btnSave = (Button)findViewById(R.id.btnSave);

        btnClear.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.d(TAG, "Clear"); //this writes to LogCat set filter to app: com.aspen.aspenSC to filter out other system processes
                sv.clearSignature();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Log.d(TAG, "Cancel"); //this writes to LogCat set filter to app: com.aspen.aspenSC to filter out other system processes
                //Back to main Activity

                Intent intent = new Intent(v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Bitmap sig = sv.getImage(R.id.signatureView);
                Log.d(TAG, "Save"); //this writes to LogCat set filter to app: com.aspen.aspenSC to filter out other system processes

               saveSig(sig);
               UploadSignature(sig);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void saveSig(Bitmap image)
    {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null)
        {
            Log.d(TAG,
                    "Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try
        {
            if (!pictureFile.exists())
            {
                pictureFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            MediaStore.Images.Media.insertImage(this.getContentResolver(), pictureFile.getAbsolutePath(), pictureFile.getName(), pictureFile.getName());
            Toast.makeText(this.getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();



        } catch (FileNotFoundException e)
        {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }


    private  File getOutputMediaFile()
    {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory().toString()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files/"); //need to do to this, as writing to the root is not allowed. root is read only

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists())
        {
            mediaStorageDir.mkdirs();
        }
        File mediaFile;
        String mImageName = getCurrentTimeStamp() +".PNG";
        mediaFile = new File(mediaStorageDir.getPath() + mImageName);
        return mediaFile;
    }

    public static String getCurrentTimeStamp()
    {
        String finalString;
        try
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date
            finalString = currentTimeStamp.replace(":", "");
            finalString = finalString.replace("-", "");
            return finalString;
        }
        catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static int getFakeOrderNumber()
    {
        //Normally I should be passing in an Order/Shipment number from the list view before this screen, since that hasn't been created
        //yet this will be the placeholder
        int orderNumber;
        orderNumber = 415323;
        return orderNumber;
    }

    private void ClearSig()
    {
        sv.clearSignature();
    }

    public void UploadSignature(Bitmap sig)
    {
        //ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //mBitmap.compress(Bitmap.CompressFormat.JPEG, 75, bos);
        //byte[] data = bos.toByteArray();
        byte[] sendData;
        //String sendData;
        sendData = getEncoded64ImageStringFromBitmap(sig);


        // Making HTTP request
        try {

            DefaultHttpClient httpClient = new DefaultHttpClient();
            //change
            String URL1 = "http://localhost:65007/CanopyWebService.svc/SubmitInvoiceSignature";

            HttpPost httpPost = new HttpPost(URL1);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json" );

            ContentBody bin = null;

            MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            entityBuilder.addBinaryBody("imageFileName", sendData); //error here I think I am missing httpMimeCore.jar or something like that, Not sure why it waits until this method to crash


            HttpEntity entity = entityBuilder.build();
            httpPost.setEntity(entity);

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
