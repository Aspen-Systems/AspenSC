package com.aspen.aspensc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity
{

    private String[] mResponse;
    private EditText txtInput;
    TextView txtOutput;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    public void GoToSigCap(View view)
    {
        Intent intent = new Intent(this, MainActivity3.class);
        Button btnGoTo = (Button) findViewById(R.id.btnGoTo);
        startActivity(intent);
    }


    public void zzz(View view)
    {

        String[] input = new String[1];



        txtInput = (EditText)findViewById(R.id.txtEnterID);
        input[0] = txtInput.getText().toString();
        new GetExample().execute(input);
    }


    private class GetExample extends AsyncTask<String[], Void, String[]>
    {
        private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        private NetworkAccessService oNAS = new NetworkAccessService();

        @Override
        protected void onPreExecute()
        {
            this.dialog.setMessage("Loading...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected String[] doInBackground(String[]... params)
        {
            String[] result = new String[10];
            String str = params[0].toString();
            result[0] = oNAS.Test(str);
            return result;
        }
        @Override
        protected void onPostExecute(String[] result)
        {
            this.dialog.dismiss();
            txtOutput =  (TextView)findViewById(R.id.txtresult);
            txtOutput.setText(result[0]);

        }

    }
}
