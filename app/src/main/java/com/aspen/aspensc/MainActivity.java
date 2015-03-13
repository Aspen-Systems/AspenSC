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
    //todo test
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
        String response;
        String[] input = new String[1];

        EditText txtInput = (EditText)findViewById(R.id.txtEnterID);
        input[0] = txtInput.getText().toString();
        new GetExample().execute(input);
        TextView txtOutput =  (TextView)findViewById(R.id.txtresult);
        txtOutput.setText(response);
    }


    private class GetExample extends AsyncTask<Integer, Void, String>
    {
        private final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
        private NetworkAccessService oNAS = new NetworkAccessService();
        private String mResult = "";

        @Override
        protected void onPreExecute()
        {
            this.dialog.setMessage("Loading...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(int... params)
        {
            String str = params[0] + "";
            mResult += oNAS.Test(str);
            return null;
        }
        @Override
        protected String onPostExecute(Void result)
        {
            this.dialog.dismiss();
            return mResult;
        }

    }
}
