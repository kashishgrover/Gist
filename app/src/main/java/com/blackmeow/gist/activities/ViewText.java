package com.blackmeow.gist.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.blackmeow.gist.R;
import com.microsoft.cognitiveservices.speechrecognition.DataRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.MicrophoneRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionMode;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionServiceFactory;

import java.io.File;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class ViewText extends ActionBarActivity {

    private String mFileName = null;
    private String mFilePath = null;

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
        /*
        File f;
        do {
            mFileName = fileName;
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath += "/gist/";
            f = new File(mFilePath + mFileName);

            Log.i("File", f.toString());
        } while (f.exists() && !f.isDirectory());
        */
    }
}
