package com.blackmeow.gist.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

//import com.microsoft.bing.speech.SpeechClientStatus;
//import com.microsoft.cognitiveservices.speechrecognition.DataRecognitionClient;
//import com.microsoft.cognitiveservices.speechrecognition.ISpeechRecognitionServerEvents;
//import com.microsoft.cognitiveservices.speechrecognition.MicrophoneRecognitionClient;
//import com.microsoft.cognitiveservices.speechrecognition.RecognitionResult;
//import com.microsoft.cognitiveservices.speechrecognition.RecognitionStatus;
//import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionMode;
//import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionServiceFactory;

import com.blackmeow.gist.R;
import com.blackmeow.gist.RecordingService;
import com.blackmeow.gist.activities.WavActivity;
import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.io.InputStream;

public class RecordFragment extends Fragment{//} implements ISpeechRecognitionServerEvents {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_POSITION = "position";
    private static final String LOG_TAG = RecordFragment.class.getSimpleName();

    private int position;

    //Recording controls
    private FloatingActionButton mRecordButton = null;
    private Button mPauseButton = null;

    private TextView mRecordingPrompt;
//    DataRecognitionClient dataClient = null;
//    MicrophoneRecognitionClient micClient = null;

    private boolean mStartRecording = true;
    private boolean mPauseRecording = true;

    private Chronometer mChronometer = null;
    long timeWhenPaused = 0; //stores time when user clicks pause button

    public static RecordFragment newInstance(int position) {
        RecordFragment f = new RecordFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);

        return f;
    }

    public RecordFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View recordView = inflater.inflate(R.layout.fragment_record, container, false);

        mChronometer = (Chronometer) recordView.findViewById(R.id.chronometer);
        //update recording prompt text
        mRecordingPrompt = (TextView) recordView.findViewById(R.id.recording_status_text);

        mRecordButton = (FloatingActionButton) recordView.findViewById(R.id.btnRecord);
        mRecordButton.setColorNormal(getResources().getColor(R.color.primary));
        mRecordButton.setColorPressed(getResources().getColor(R.color.primary_dark));
        mRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onRecord(mStartRecording);
                Intent A = new Intent(getActivity(), WavActivity.class);
                startActivity(A);
                mStartRecording = !mStartRecording;
            }
        });

        mPauseButton = (Button) recordView.findViewById(R.id.btnPause);
        mPauseButton.setVisibility(View.GONE); //hide pause button before recording starts
        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPauseRecord(mPauseRecording);
                mPauseRecording = !mPauseRecording;
            }
        });

        return recordView;
    }

    // Recording Start/Stop
    private void onRecord(boolean start){

        Intent intent = new Intent(getActivity(), RecordingService.class);

        if (start) {
            // start recording
            mRecordButton.setImageResource(R.drawable.ic_media_stop);
            //mPauseButton.setVisibility(View.VISIBLE);
            Toast.makeText(getActivity(),R.string.toast_recording_start,Toast.LENGTH_SHORT).show();
            File folder = new File(Environment.getExternalStorageDirectory() + "/Gist");
            if (!folder.exists()) {
                //folder /gist doesn't exist, create the folder
                folder.mkdir();
            }

//            this.micClient = SpeechRecognitionServiceFactory.createMicrophoneClient(
//                    getActivity(),
//                    this.getMode(),
//                    this.getDefaultLocale(),
//                    this,
//                    this.getPrimaryKey());
//            this.micClient.startMicAndRecognition();

            //start Chronometer
            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();

            //start RecordingService
            getActivity().startService(intent);
            //Log.i("Service:",getActivity().toString());
            //keep screen on while recording
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//            mRecordingPrompt.setText(getString(R.string.record_in_progress) + ".");

        } else {
            //stop recording
            mRecordButton.setImageResource(R.drawable.ic_mic_white_36dp);
            //mPauseButton.setVisibility(View.GONE);
            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            mRecordingPrompt.setText(getString(R.string.record_prompt));

            getActivity().stopService(intent);
            //allow the screen to turn off again once recording is finished
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    //TODO: implement pause recording
    private void onPauseRecord(boolean pause) {
        if (pause) {
            //pause recording
            mPauseButton.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ic_media_play ,0 ,0 ,0);
            mRecordingPrompt.setText((String)getString(R.string.resume_recording_button).toUpperCase());
            timeWhenPaused = mChronometer.getBase() - SystemClock.elapsedRealtime();
            mChronometer.stop();
        } else {
            //resume recording
            mPauseButton.setCompoundDrawablesWithIntrinsicBounds
                    (R.drawable.ic_media_pause ,0 ,0 ,0);
            mRecordingPrompt.setText((String)getString(R.string.pause_recording_button).toUpperCase());
            mChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenPaused);
            mChronometer.start();
        }
    }

    /**
     * Writes the line.
     */
//    private void WriteLine() {
//        this.WriteLine("");
//    }
//
//    /**
//     * Writes the line.
//     * @param text The line to write.
//     */
//    private void WriteLine(String text) {
//        this.mRecordingPrompt.append(text + "\n");
//    }
//
//    @Override
//    public void onPartialResponseReceived(String s) {
//        this.WriteLine("--- Partial result received by onPartialResponseReceived() ---");
//        this.WriteLine(s);
//        this.WriteLine();
//    }

//    @Override
//    public void onFinalResponseReceived(RecognitionResult response) {
//        boolean isFinalDicationMessage = this.getMode() == SpeechRecognitionMode.LongDictation &&
//                (response.RecognitionStatus == RecognitionStatus.EndOfDictation ||
//                        response.RecognitionStatus == RecognitionStatus.DictationEndSilenceTimeout);
//        if (null != this.micClient && ((this.getMode() == SpeechRecognitionMode.ShortPhrase) || isFinalDicationMessage)) {
//            // we got the final result, so it we can end the mic reco.  No need to do this
//            // for dataReco, since we already called endAudio() on it as soon as we were done
//            // sending all the data.
//            this.micClient.endMicAndRecognition();
//        }

//        if (isFinalDicationMessage) {
//            this._startButton.setEnabled(true);
//            this.isReceivedResponse = FinalResponseStatus.OK;
//        }

//        if (!isFinalDicationMessage) {
//            this.WriteLine("********* Final n-BEST Results *********");
//            for (int i = 0; i < response.Results.length; i++) {
//                this.WriteLine("[" + i + "]" + " Confidence=" + response.Results[i].Confidence +
//                        " Text=\"" + response.Results[i].DisplayText + "\"");
//            }
//
//            this.WriteLine();
//        }
//    }

//    @Override
//    public void onIntentReceived(String s) {
//        this.WriteLine("--- Intent received by onIntentReceived() ---");
//        this.WriteLine(s);
//        this.WriteLine();
//    }
//
//    @Override
//    public void onError(int i, String s) {
//        this.WriteLine("--- Error received by onError() ---");
//        this.WriteLine("Error code: " + SpeechClientStatus.fromInt(i) + " " + i);
//        this.WriteLine("Error text: " + s);
//        this.WriteLine();
//    }

//    @Override
//    public void onAudioEvent(boolean b) {
//        this.WriteLine("--- Microphone status change received by onAudioEvent() ---");
//        this.WriteLine("********* Microphone status: " + b + " *********");
//        if (b) {
//            this.WriteLine("Please start speaking.");
//        }
//
//        WriteLine();
//    }
//
//    private SpeechRecognitionMode getMode() {
//        return SpeechRecognitionMode.ShortPhrase;
//    }

    private String getDefaultLocale() {
        return "en-us";
    }

    /*
 * Speech recognition with data (for example from a file or audio source).
 * The data is broken up into buffers and each buffer is sent to the Speech Recognition Service.
 * No modification is done to the buffers, so the user can apply their
 * own VAD (Voice Activation Detection) or Silence Detection
 *
 * @param dataClient
 * @param recoMode
 * @param filename
 */
//    private class RecognitionTask extends AsyncTask<Void, Void, Void> {
//        DataRecognitionClient dataClient;
//        SpeechRecognitionMode recoMode;
//        String filename;
//
//        RecognitionTask(DataRecognitionClient dataClient, SpeechRecognitionMode recoMode, String filename) {
//            this.dataClient = dataClient;
//            this.recoMode = recoMode;
//            this.filename = filename;
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                // Note for wave files, we can just send data from the file right to the server.
//                // In the case you are not an audio file in wave format, and instead you have just
//                // raw data (for example audio coming over bluetooth), then before sending up any
//                // audio data, you must first send up an SpeechAudioFormat descriptor to describe
//                // the layout and format of your raw audio data via DataRecognitionClient's sendAudioFormat() method.
//                // String filename = recoMode == SpeechRecognitionMode.ShortPhrase ? "whatstheweatherlike.wav" : "batman.wav";
//                InputStream fileStream = getActivity().getAssets().open(filename);
//                int bytesRead = 0;
//                byte[] buffer = new byte[1024];
//
//                do {
//                    // Get  Audio data to send into byte buffer.
//                    bytesRead = fileStream.read(buffer);
//
//                    if (bytesRead > -1) {
//                        // Send of audio data to service.
//                        dataClient.sendAudio(buffer, bytesRead);
//                    }
//                } while (bytesRead > 0);
//
//            } catch (Throwable throwable) {
//                throwable.printStackTrace();
//            }
//            finally {
//                dataClient.endAudio();
//            }
//
//            return null;
//        }
//    }
}