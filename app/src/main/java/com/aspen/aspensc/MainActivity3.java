package com.aspen.aspensc;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity3 extends ActionBarActivity
{
    private static final String TAG = "MainActivity3";
    private SignatureView sv;
    private Button btnClear;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity3);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);

        final SignatureView sv = (SignatureView)findViewById(R.id.signatureView);
        Button btnClear =  (Button)findViewById(R.id.btnClear);

        btnClear.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Log.d(TAG, "test"); //this writes to LogCat set filter to app: com.aspen.aspenSC to filter out other system processes
                sv.clearSignature();
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




    private void ClearSig()
    {
        sv.clearSignature();

    }


}
