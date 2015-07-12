package com.tuesda.submitdemo;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.tuesda.submit.SubmitView;


public class MainActivity extends Activity {

    private SubmitView mSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSubmit = (SubmitView) findViewById(R.id.submit);
        mSubmit.setBackColor(0xff0097cd);

        mSubmit.setProgress(1);

        mSubmit.setOnProgressStart(new SubmitView.OnProgressStart() {
            @Override
            public void progressStart() {
                // do something when progress start
            }
        });

        mSubmit.setOnProgressDone(new SubmitView.OnProgressDone() {
            @Override
            public void progressDone() {
                // do something when progress is done
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
