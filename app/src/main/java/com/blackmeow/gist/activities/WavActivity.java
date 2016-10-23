package com.blackmeow.gist.activities;

import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.blackmeow.gist.DBHelper;
import com.blackmeow.gist.R;

import java.io.File;

import omrecorder.AudioChunk;
import omrecorder.AudioSource;
import omrecorder.OmRecorder;
import omrecorder.PullTransport;
import omrecorder.Recorder;

public class WavActivity extends ActionBarActivity {
    Recorder recorder;
    ImageView recordButton;

    private String mFileName = null;
    private String mFilePath = null;

    private DBHelper mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);
        setupRecorder();
        mDatabase = new DBHelper(getApplicationContext());

        recordButton = (ImageView) findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),R.string.toast_recording_start,Toast.LENGTH_SHORT).show();
                recorder.startRecording();
            }
        });
        findViewById(R.id.stopButton).setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View view) {
                recorder.stopRecording();
                //Toast.makeText(getApplicationContext(),R.string.toast_recording_finish,Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), getString(R.string.toast_recording_finish) + " "
                        + mFilePath, Toast.LENGTH_LONG).show();
                mDatabase.addRecording(mFileName, mFilePath,0);
                recordButton.post(new Runnable() {
                    @Override public void run() {
                        animateVoice(0);
                    }
                });
            }
        });
    }

    private void setupRecorder() {
        recorder = OmRecorder.wav(
                new PullTransport.Default(mic(), new PullTransport.OnAudioChunkPulledListener() {
                    @Override public void onAudioChunkPulled(AudioChunk audioChunk) {
                        animateVoice((float) (audioChunk.maxAmplitude() / 200.0));
                    }
                }), file());
    }

    private void animateVoice(final float maxPeak) {
        recordButton.animate().scaleX(1 + maxPeak).scaleY(1 + maxPeak).setDuration(10).start();
    }

    private AudioSource mic() {
        return new AudioSource.Smart(MediaRecorder.AudioSource.MIC, AudioFormat.ENCODING_PCM_16BIT,
                AudioFormat.CHANNEL_IN_MONO, 44100);
    }

    @NonNull
    private File file() {
        int count = 0;
        File f;
        do{
            count++;
            mFileName = "Sample#" + (/*mDatabase.getCount() + */count) + ".wav";
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath += "/gist/";
            f = new File(mFilePath+mFileName);
        }while (f.exists() && !f.isDirectory());
        return new File(mFilePath, mFileName);
    }
}
