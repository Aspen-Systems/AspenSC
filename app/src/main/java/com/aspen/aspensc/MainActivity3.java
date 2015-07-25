package com.aspen.aspensc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

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
    private String mFileName;

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
               Bitmap[] signatures = {sig};
               UploadImage ui = new  UploadImage();
               ui.setFileName(mFileName);
               ui.execute(signatures);


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
        mFileName = getCurrentTimeStamp() +".PNG";
        mediaFile = new File(mediaStorageDir.getPath() + mFileName);
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
        catch (Exception e)
        {
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


    private class UploadImage extends AsyncTask<Bitmap, Void, Void>
    {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity3.this);
        private NetworkAccessService oNAS = new NetworkAccessService();
        private String mFileName;

        public void setFileName(String name)
        {
            this.mFileName = name;
        }

        @Override
        protected void onPreExecute()
        {
            this.dialog.setMessage("Loading...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Bitmap... params)
        {
            Bitmap sig = params[0];
            oNAS.UploadSignature(sig, mFileName);
            return null;
        }
        @Override
        protected void onPostExecute(Void result)
        {
            this.dialog.dismiss();
        }

    }



}
