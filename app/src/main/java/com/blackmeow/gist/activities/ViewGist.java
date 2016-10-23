package com.blackmeow.gist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.blackmeow.gist.R;

public class ViewGist extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_view);

        Intent i = getIntent();
        String fileName = i.getStringExtra("File Name");
        Log.i("Filename", fileName);
        TextView tv = (TextView) findViewById(R.id.textView4);
        tv.setText(fileName);
        Toast.makeText(this, fileName, Toast.LENGTH_SHORT).show();

    }

}
