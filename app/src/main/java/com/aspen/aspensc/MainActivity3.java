package com.aspen.aspensc;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


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
                Log.d(TAG, "Save"); //this writes to LogCat set filter to app: com.aspen.aspenSC to filter out other system processes
               saveSig(sv.getImage());
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
    //simple example of saving
/*    public void saveSignature(View view) {

        Bitmap image = sv.getImage();
            File sd = Environment.getExternalStorageDirectory();
            File f = new File(sd, getCurrentTimeStamp() + ".jpg");


            try {
                if (sd.canWrite())
                {
                    f.createNewFile();
                    OutputStream os = new FileOutputStream(f);
                    image.compress(Bitmap.CompressFormat.JPEG, 90, os);
                    os.close();
                 }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
         }*/




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
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e)
        {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }


    private  File getOutputMediaFile()
    {
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");


        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists())
        {
            if (! mediaStorageDir.mkdirs()) //attempt to create directory //TODO: mkdirs isn't working UPDATE manifest need permission which is why the file directory was unable to be created. android.permission.WRITE_EXTERNAL_STORAGE
            {
                return null;
            }
        }

        File mediaFile;
        String mImageName="Shipment"+ getFakeOrderNumber() +getCurrentTimeStamp() +".jpg";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    public static String getCurrentTimeStamp()
    {
        try
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTimeStamp = dateFormat.format(new Date()); // Find todays date

            return currentTimeStamp;
        }
        catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public static int getFakeOrderNumber()
    {
        //Normally I should be passing in an orderNumber from the list view before this screen, since that hasn't been created
        //yet this will be the placeholder
        int orderNumber;
        orderNumber = 415323;
        return orderNumber;
    }

    private void ClearSig()
    {
        sv.clearSignature();
    }




}
