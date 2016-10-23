package com.blackmeow.gist.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blackmeow.gist.R;
import com.microsoft.cognitiveservices.speechrecognition.DataRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.MicrophoneRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionMode;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionServiceFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.microsoft.bing.speech.SpeechClientStatus;
import com.microsoft.cognitiveservices.speechrecognition.DataRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.ISpeechRecognitionServerEvents;
import com.microsoft.cognitiveservices.speechrecognition.MicrophoneRecognitionClient;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionResult;
import com.microsoft.cognitiveservices.speechrecognition.RecognitionStatus;
import com.microsoft.cognitiveservices.speechrecognition.SpeechAudioFormat;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionMode;
import com.microsoft.cognitiveservices.speechrecognition.SpeechRecognitionServiceFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class ViewGist extends ActionBarActivity {

    static String fileName = null;

    /**
     * Gets the primary subscription key
     */
    public String getPrimaryKey() {
        return this.getString(R.string.primaryKey);
    }

    TextView textview2;
    TextView summary_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_gist);

        textview2 = (TextView) findViewById(R.id.textView2);
        summary_view = (TextView) findViewById(R.id.summary_view);
        String fulltext = "";

        Intent i = getIntent();
        fileName = i.getStringExtra("File Name");
        Log.i("Filename", fileName);
        TextView tv = (TextView) findViewById(R.id.textView4);
        tv.setText(fileName);

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/gist/text/";
        File pathfile = new File(path);
        if (!pathfile.exists()) {
            this.finish();
        }
        String arr[] = fileName.split("\\.");
        path += arr[0] + ".txt";
        File file = new File(path);
        try {
            FileReader fr = new FileReader(file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(path);
            int x = 0;
            while ((x = fin.read()) != -1) {
                Log.i(">>", ((char) x) + "");
                fulltext = fulltext+((char)x);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        textview2.setText(fulltext);

        List<String> k= null;
        try {
            k = new GetKeywords().execute(fulltext).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String keys[]=new String[k.size()];
        keys=k.toArray(keys);
        com.blackmeow.gist.activities.Summary obj=new com.blackmeow.gist.activities.Summary(keys,fulltext);
        com.blackmeow.gist.activities.Sentence ans[]=obj.getSentences();
        String b="";

        for(com.blackmeow.gist.activities.Sentence a:ans)
        {
            b=b+a.normal+". ";
        }
        Log.i("FINAL ANSWER",b);

        summary_view.setText(b);

    }
}
//{
//            this.WriteLine("********* Final n-BEST Results *********");
//            OutputStreamWriter outputStreamWriter = null;
//            try {
//                outputStreamWriter = new OutputStreamWriter(this.openFileOutput("config.txt", Context.MODE_PRIVATE));
//                for (int i = 0; i < response.Results.length; i++) {
//                    this.WriteLine("[" + i + "]" + " Confidence=" + response.Results[i].Confidence +
//                            " Text=\"" + response.Results[i].DisplayText + "\"");
//                    //need to save the files now
//                    outputStreamWriter.append(response.Results[i].DisplayText);
//                    outputStreamWriter.close();
//                    str=response.Results[i].DisplayText;
//                    Log.i("HELOOOOOOO",str);
//                }
//                List<String> k=new GetKeywords().execute(str).get();
//                String keys[]=new String[k.size()];
//                keys=k.toArray(keys);
//                com.blackmeow.gist.activities.Summary obj=new com.blackmeow.gist.activities.Summary(keys,str);
//                com.blackmeow.gist.activities.Sentence ans[]=obj.getSentences();
//                String b="";
//                for(com.blackmeow.gist.activities.Sentence a:ans)
//                {
//                    b=b+a.normal+". ";
//                }
//                Log.i("FINAL ANSWER",b);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }catch(IOException e){
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            }
//            this.WriteLine();
//        }
//    }

class GetKeywords extends AsyncTask<String, Integer, List<String>> {

    @Override
    protected List<String> doInBackground(String... param) {
        try {

            HttpClient httpclient = new DefaultHttpClient();
            List<String> ls = new ArrayList<>();
            try {
                //URIBuilder builder = new URIBuilder("https://westus.api.cognitive.microsoft.com/text/analytics/v2.0/keyPhrases");

                String uri = "https://westus.api.cognitive.microsoft.com/text/analytics/v2.0/keyPhrases";
                //URI uri = builder.build();
                HttpPost request = new HttpPost(uri);
                request.setHeader("Content-Type", "application/json");
                request.setHeader("Ocp-Apim-Subscription-Key", "d83e2715003446d79e9426aaaf8cb7e4");

                JSONObject obj = new JSONObject();
                JSONArray list = new JSONArray();
                JSONObject doc = new JSONObject();
                doc.put("language", "en");
                doc.put("id", 1);
                doc.put("text", param[0]);
                list.put(0, doc);
                obj.put("documents", list);
                Log.i("Main", obj.toString());

                // Request body
                StringEntity reqEntity = new StringEntity(obj.toString());
                request.setEntity(reqEntity);

                HttpResponse response = httpclient.execute(request);
                HttpEntity entity = response.getEntity();

                if (entity != null) {
                    Log.i("Main0", entity.toString());
                    String string = EntityUtils.toString(entity);
                    JSONObject res = new JSONObject(string);
                    JSONArray docJ = res.getJSONArray("documents");
                    Log.i("Documents", docJ.toString());
                    JSONObject phrases = docJ.getJSONObject(0);
                    Log.i("Phrase", phrases.toString());
                    JSONArray phrasearr = phrases.getJSONArray("keyPhrases");
                    Log.i("Array", phrasearr.toString());
                    List<String> keywords = new ArrayList<>();
                    for (int i = 0; i < phrasearr.length(); i++) {
                        keywords.add(phrasearr.getString(i).toString());
                    }

                    return keywords;
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}


